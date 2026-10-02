package com.example.data.remote

import android.util.Log
import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SupabaseRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client,
    private val bookingDao: BookingDao? = null
) {
    private val TAG = "SupabaseRepository"

    /**
     * Upload a booking to Supabase
     */
    suspend fun uploadBooking(booking: BookingEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val remoteBooking = SupabaseBooking(
                id = booking.id,
                name = booking.name,
                phone = booking.phone,
                address = booking.address,
                block = booking.block,
                serviceName = booking.serviceName,
                scheduledTime = booking.scheduledTime,
                status = booking.status,
                providerStatus = booking.providerStatus,
                paymentStatus = booking.paymentStatus,
                latitude = booking.latitude,
                longitude = booking.longitude,
                razorpayPaymentId = booking.razorpayPaymentId
            )
            client.from("bookings").insert(remoteBooking)
            bookingDao?.markSynced(booking.id)
            Log.d(TAG, "Successfully uploaded booking ${booking.id} to Supabase")
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Supabase uploadBooking skipped or network unavailable: ${e.message}")
            false
        }
    }

    /**
     * Sync pending / unsynced local bookings to Supabase on app start
     */
    suspend fun syncPendingBookings(): Int = withContext(Dispatchers.IO) {
        if (bookingDao == null) return@withContext 0
        var syncedCount = 0
        try {
            val unsynced = bookingDao.getUnsyncedBookings()
            Log.d(TAG, "Found ${unsynced.size} unsynced bookings to sync with Supabase")
            for (booking in unsynced) {
                val success = uploadBooking(booking)
                if (success) syncedCount++
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Error while syncing pending bookings to Supabase: ${e.message}")
        }
        syncedCount
    }

    /**
     * Push live partner location from device (e.g. Foreground Service)
     */
    suspend fun updatePartnerLocation(
        partnerId: String,
        latitude: Double,
        longitude: Double
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val update = SupabasePartnerLocation(
                partnerId = partnerId,
                latitude = latitude,
                longitude = longitude
            )
            client.from("partner_locations").insert(update)
            Log.d(TAG, "Pushed partner location to Supabase: $latitude, $longitude")
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Could not push partner location: ${e.message}")
            false
        }
    }

    /**
     * Fetch the most recent partner location from Supabase
     */
    suspend fun getLatestPartnerLocation(partnerId: String): SupabasePartnerLocation? =
        withContext(Dispatchers.IO) {
            try {
                val result = client.from("partner_locations")
                    .select {
                        filter {
                            eq("partner_id", partnerId)
                        }
                        order("updated_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                        limit(1)
                    }
                    .decodeList<SupabasePartnerLocation>()
                result.firstOrNull()
            } catch (e: Throwable) {
                Log.w(TAG, "Error querying partner location from Supabase: ${e.message}")
                null
            }
        }

    /**
     * Update payment status and Razorpay Payment ID in Supabase and Room
     */
    suspend fun updatePaymentStatus(
        bookingId: Long,
        paymentStatus: String,
        razorpayPaymentId: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("bookings").update(
                mapOf(
                    "payment_status" to paymentStatus,
                    "razorpay_payment_id" to (razorpayPaymentId ?: "")
                )
            ) {
                filter {
                    eq("id", bookingId)
                }
            }
            bookingDao?.updatePaymentStatus(bookingId, paymentStatus, razorpayPaymentId)
            Log.d(TAG, "Updated payment status in Supabase & Room for booking $bookingId")
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Error updating payment status in Supabase: ${e.message}")
            // Still update locally in Room
            bookingDao?.updatePaymentStatus(bookingId, paymentStatus, razorpayPaymentId)
            false
        }
    }

    /**
     * Listen for realtime booking status changes
     */
    fun subscribeToBookingChanges(
        bookingId: Long,
        coroutineScope: CoroutineScope,
        onStatusChanged: (status: String, providerStatus: String) -> Unit
    ): RealtimeChannel? {
        return try {
            val channel = client.channel("booking_$bookingId")
            val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "bookings"
            }

            changeFlow.onEach { action ->
                when (action) {
                    is PostgresAction.Update -> {
                        val newRecord = action.record
                        val recordId = newRecord["id"]?.toString()?.replace("\"", "")?.toLongOrNull()
                        if (recordId == bookingId) {
                            val status = newRecord["status"]?.toString()?.replace("\"", "") ?: "Pending"
                            val providerStatus = newRecord["provider_status"]?.toString()?.replace("\"", "") ?: "Assigned"
                            Log.d(TAG, "Realtime update for booking $bookingId: $status, $providerStatus")
                            onStatusChanged(status, providerStatus)

                            // Update local Room database
                            bookingDao?.updateStatus(bookingId, status, providerStatus)
                        }
                    }
                    else -> Unit
                }
            }.catch { e ->
                Log.w(TAG, "Realtime subscription stream issue: ${e.message}")
            }.launchIn(coroutineScope)

            coroutineScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Throwable) {
                    Log.w(TAG, "Failed to subscribe to channel: ${e.message}")
                }
            }
            channel
        } catch (e: Throwable) {
            Log.w(TAG, "Unable to setup Supabase Realtime channel: ${e.message}")
            null
        }
    }
}
