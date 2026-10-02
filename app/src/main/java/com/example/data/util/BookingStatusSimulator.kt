package com.example.data.util

import android.util.Log
import com.example.data.local.BookingDao
import com.example.data.remote.SupabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Simulates real-time provider progression for active bookings.
 * Updates Room local database and pushes updates to Supabase so customer UI
 * reflects live status changes (Pending -> Confirmed -> En Route -> Arrived -> Completed).
 */
class BookingStatusSimulator(
    private val bookingDao: BookingDao,
    private val supabaseRepository: SupabaseRepository,
    private val scope: CoroutineScope
) {
    private val TAG = "BookingStatusSimulator"
    private var simulationJob: Job? = null

    fun startSimulation() {
        if (simulationJob?.isActive == true) return

        simulationJob = scope.launch(Dispatchers.IO) {
            Log.d(TAG, "BookingStatusSimulator started")
            while (isActive) {
                try {
                    val pendingBookings = bookingDao.getPendingBookings()
                    val now = System.currentTimeMillis()

                    for (booking in pendingBookings) {
                        val ageMillis = now - booking.createdAt

                        when {
                            // After 10 seconds: Assign partner & confirm
                            ageMillis in 8_000L..25_000L && booking.providerStatus == "Finding Partner" -> {
                                Log.i(TAG, "Simulating booking ${booking.id} -> Confirmed & Assigned")
                                bookingDao.updateStatus(booking.id, "Confirmed", "Assigned")
                                supabaseRepository.uploadBooking(booking.copy(status = "Confirmed", providerStatus = "Assigned"))
                            }

                            // After 25 seconds: Partner is en route
                            ageMillis in 25_001L..45_000L && (booking.providerStatus == "Assigned" || booking.status == "Confirmed") -> {
                                if (booking.providerStatus != "En Route") {
                                    Log.i(TAG, "Simulating booking ${booking.id} -> En Route")
                                    bookingDao.updateStatus(booking.id, "Confirmed", "En Route")
                                    supabaseRepository.uploadBooking(booking.copy(status = "Confirmed", providerStatus = "En Route"))
                                }
                            }

                            // After 45 seconds: Partner has arrived
                            ageMillis in 45_001L..65_000L && booking.providerStatus == "En Route" -> {
                                Log.i(TAG, "Simulating booking ${booking.id} -> Arrived")
                                bookingDao.updateStatus(booking.id, "Confirmed", "Arrived")
                                supabaseRepository.uploadBooking(booking.copy(status = "Confirmed", providerStatus = "Arrived"))
                            }

                            // After 65 seconds: Service marked completed -> triggers Razorpay Pay Now
                            ageMillis > 65_000L && (booking.providerStatus == "Arrived" || (booking.status == "Confirmed" && booking.providerStatus != "Completed")) -> {
                                Log.i(TAG, "Simulating booking ${booking.id} -> Completed")
                                bookingDao.updateStatus(booking.id, "Completed", "Completed")
                                supabaseRepository.uploadBooking(booking.copy(status = "Completed", providerStatus = "Completed"))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Simulator loop exception: ${e.message}")
                }
                delay(5_000) // Poll every 5 seconds
            }
        }
    }

    /**
     * Manually trigger instant completion for quick demo testing
     */
    fun fastForwardToCompleted(bookingId: Long) {
        scope.launch(Dispatchers.IO) {
            bookingDao.updateStatus(bookingId, "Completed", "Completed")
            val updated = bookingDao.getBookingByIdDirect(bookingId)
            if (updated != null) {
                supabaseRepository.uploadBooking(updated)
            }
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }
}
