package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String,
    val block: String,
    val serviceName: String,
    val category: String = "Home Maintenance",
    val scheduledTime: String,
    val price: String,
    val status: String = "Pending", // "Pending", "Confirmed", "Completed", "Cancelled"
    val providerStatus: String = "Finding Partner", // "Finding Partner", "Assigned", "En Route", "Arrived", "Completed"
    val paymentStatus: String = "Pending", // "Pending", "PAID"
    val razorpayPaymentId: String? = null,
    val latitude: Double = 29.1492,
    val longitude: Double = 75.7217,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val syncedToSupabase: Boolean = false
)
