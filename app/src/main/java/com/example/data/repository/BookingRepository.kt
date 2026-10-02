package com.example.data.repository

import android.util.Log
import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import com.example.data.remote.SupabaseRepository
import kotlinx.coroutines.flow.Flow

class BookingRepository(
    private val bookingDao: BookingDao,
    private val supabaseRepository: SupabaseRepository? = null
) {
    private val TAG = "BookingRepository"

    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    fun getBookingById(id: Long): Flow<BookingEntity?> = bookingDao.getBookingById(id)

    suspend fun getBookingDirect(id: Long): BookingEntity? = bookingDao.getBookingByIdDirect(id)

    suspend fun createBooking(booking: BookingEntity): Long {
        val id = bookingDao.insertBooking(booking)
        val createdBooking = booking.copy(id = id)
        // Save to Supabase as well
        supabaseRepository?.uploadBooking(createdBooking)
        return id
    }

    suspend fun updateBooking(booking: BookingEntity) {
        bookingDao.updateBooking(booking)
        supabaseRepository?.uploadBooking(booking)
    }

    suspend fun updatePaymentStatus(bookingId: Long, paymentStatus: String, paymentId: String?) {
        bookingDao.updatePaymentStatus(bookingId, paymentStatus, paymentId)
        supabaseRepository?.updatePaymentStatus(bookingId, paymentStatus, paymentId)
    }

    suspend fun updateStatus(bookingId: Long, status: String, providerStatus: String) {
        bookingDao.updateStatus(bookingId, status, providerStatus)
    }

    suspend fun syncPendingWithCloud(): Int {
        return supabaseRepository?.syncPendingBookings() ?: 0
    }

    suspend fun deleteBooking(booking: BookingEntity) {
        bookingDao.deleteBooking(booking)
    }

    suspend fun deleteBookingById(id: Long) {
        bookingDao.deleteBookingById(id)
    }
}
