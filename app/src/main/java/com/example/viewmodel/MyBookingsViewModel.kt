package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookingEntity
import com.example.data.repository.BookingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyBookingsViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val bookings: StateFlow<List<BookingEntity>> = bookingRepository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteBooking(booking: BookingEntity) {
        viewModelScope.launch {
            bookingRepository.deleteBooking(booking)
        }
    }

    fun deleteBookingById(id: Long) {
        viewModelScope.launch {
            bookingRepository.deleteBookingById(id)
        }
    }

    fun updateStatus(booking: BookingEntity, newStatus: String) {
        viewModelScope.launch {
            bookingRepository.updateBooking(booking.copy(status = newStatus))
        }
    }

    fun updatePaymentSuccess(bookingId: Long, paymentId: String) {
        viewModelScope.launch {
            bookingRepository.updatePaymentStatus(bookingId, "PAID", paymentId)
        }
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            bookingRepository.syncPendingWithCloud()
        }
    }

    fun updateBookingProgress(bookingId: Long, status: String, providerStatus: String) {
        viewModelScope.launch {
            bookingRepository.updateStatus(bookingId, status, providerStatus)
        }
    }

    /**
     * Updates and stores booking details (name, phone, address, block, time, notes)
     * when the customer provides them later.
     */
    fun updateBookingDetails(
        bookingId: Long,
        name: String,
        phone: String,
        address: String,
        block: String,
        scheduledTime: String,
        notes: String
    ) {
        viewModelScope.launch {
            val existing = bookings.value.find { it.id == bookingId } ?: return@launch
            val updated = existing.copy(
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                block = block.trim(),
                scheduledTime = scheduledTime.trim(),
                notes = notes.trim()
            )
            bookingRepository.updateBooking(updated)
        }
    }

    /**
     * Creates a test inquiry/booking instantly for test app verification.
     */
    fun createTestInquiry(
        serviceName: String = "AC Jet Cleaning & Service",
        block: String = "Hansi",
        price: String = "₹499",
        category: String = "AC Repair",
        customerName: String = "Test Customer (Hisar)",
        customerPhone: String = "9812345678",
        customerAddress: String = "Sector 14, Test Lane, Hisar",
        notes: String = "Test inquiry created for testing app inquiries & audit workflow"
    ) {
        viewModelScope.launch {
            val testInquiry = BookingEntity(
                name = customerName,
                phone = customerPhone,
                address = customerAddress,
                block = block,
                serviceName = serviceName,
                category = category,
                scheduledTime = "Tomorrow, 10:00 AM - 12:00 PM",
                price = price,
                status = "Pending",
                providerStatus = "Finding Partner",
                paymentStatus = "Pending",
                notes = notes,
                createdAt = System.currentTimeMillis()
            )
            bookingRepository.createBooking(testInquiry)
        }
    }

    /**
     * Advances provider status through the inquiry lifecycle:
     * Finding Partner -> Assigned -> En Route -> Arrived -> Completed
     */
    fun advanceInquiryStatus(bookingId: Long) {
        viewModelScope.launch {
            val booking = bookings.value.find { it.id == bookingId } ?: return@launch
            val (nextStatus, nextProviderStatus) = when (booking.providerStatus) {
                "Finding Partner" -> "Confirmed" to "Assigned"
                "Assigned" -> "Confirmed" to "En Route"
                "En Route" -> "Confirmed" to "Arrived"
                "Arrived" -> "Completed" to "Completed"
                else -> "Completed" to "Completed"
            }
            bookingRepository.updateStatus(bookingId, nextStatus, nextProviderStatus)
        }
    }

    /**
     * Instantly simulates test payment for an inquiry.
     */
    fun simulatePayment(bookingId: Long) {
        val testPaymentId = "pay_sim_${System.currentTimeMillis().toString().takeLast(6)}"
        updatePaymentSuccess(bookingId, testPaymentId)
    }
}

class MyBookingsViewModelFactory(
    private val bookingRepository: BookingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyBookingsViewModel(bookingRepository) as T
    }
}
