package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Long)

    @Query("SELECT * FROM bookings WHERE syncedToSupabase = 0")
    suspend fun getUnsyncedBookings(): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE status = 'Pending'")
    suspend fun getPendingBookings(): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingByIdDirect(id: Long): BookingEntity?

    @Query("UPDATE bookings SET status = :status, providerStatus = :providerStatus WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, providerStatus: String)

    @Query("UPDATE bookings SET paymentStatus = :paymentStatus, razorpayPaymentId = :paymentId WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, paymentStatus: String, paymentId: String?)

    @Query("UPDATE bookings SET syncedToSupabase = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)
}
