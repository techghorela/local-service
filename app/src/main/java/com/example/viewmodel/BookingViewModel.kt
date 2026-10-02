package com.example.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookingEntity
import com.example.data.local.ServiceEntity
import com.example.data.local.UserPreferences
import com.example.data.model.HisarBlock
import com.example.data.model.TimeSlot
import com.example.data.repository.BookingRepository
import com.example.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class BookingUiState(
    val currentStep: Int = 1, // 1, 2, 3, or 4 for success
    val service: ServiceEntity? = null,
    // Step 1: Contact & Address
    val customerName: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val selectedBlock: String = HisarBlock.HISAR_1.displayName,
    val nameError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val isDetailsDeferred: Boolean = false,
    // Step 2: Schedule
    val scheduledDate: String = "",
    val selectedTimeSlot: TimeSlot = TimeSlot.MORNING,
    val isFlexibleSchedule: Boolean = false,
    val notes: String = "",
    // Step 3 / 4: Created Booking
    val isSubmitting: Boolean = false,
    val createdBookingId: Long? = null,
    val whatsAppIntentUri: String = ""
)

class BookingViewModel(
    private val bookingRepository: BookingRepository,
    private val serviceRepository: ServiceRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        // Default scheduled date to tomorrow
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val defaultDate = dateFormat.format(calendar.time)

        _uiState.update { it.copy(scheduledDate = defaultDate) }

        // Preload user profile from DataStore if available
        viewModelScope.launch {
            val profile = userPreferences.userProfileFlow.firstOrNull()
            if (profile != null) {
                _uiState.update { current ->
                    current.copy(
                        customerName = if (current.customerName.isEmpty()) profile.name else current.customerName,
                        customerPhone = if (current.customerPhone.isEmpty()) profile.phone else current.customerPhone,
                        customerAddress = if (current.customerAddress.isEmpty()) profile.address else current.customerAddress,
                        selectedBlock = if (profile.block.isNotEmpty()) profile.block else current.selectedBlock
                    )
                }
            }
        }
    }

    fun setService(service: ServiceEntity) {
        _uiState.update { it.copy(service = service) }
    }

    fun loadServiceById(serviceId: String) {
        viewModelScope.launch {
            val found = serviceRepository.getServiceById(serviceId)
            if (found != null) {
                _uiState.update { it.copy(service = found) }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(customerName = name, nameError = null) }
    }

    fun updatePhone(phone: String) {
        _uiState.update { it.copy(customerPhone = phone, phoneError = null) }
    }

    fun updateAddress(address: String) {
        _uiState.update { it.copy(customerAddress = address, addressError = null) }
    }

    fun updateBlock(block: String) {
        _uiState.update { it.copy(selectedBlock = block) }
    }

    fun updateDate(date: String) {
        _uiState.update { it.copy(scheduledDate = date) }
    }

    fun updateTimeSlot(slot: TimeSlot) {
        _uiState.update { it.copy(selectedTimeSlot = slot) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun setFlexibleSchedule(flexible: Boolean) {
        _uiState.update { it.copy(isFlexibleSchedule = flexible) }
    }

    /**
     * Allows the user to continue booking immediately when details/values
     * are not available right now. Values will be marked as deferred so they can
     * be stored and updated later from My Bookings.
     */
    fun continueWithDeferredValues() {
        val current = _uiState.value
        val fallbackName = if (current.customerName.isNotBlank()) current.customerName.trim() else "Customer (Hisar)"
        val fallbackPhone = if (current.customerPhone.isNotBlank()) current.customerPhone.trim() else "Not Provided Yet"
        val fallbackAddress = if (current.customerAddress.isNotBlank()) current.customerAddress.trim() else "Address to be confirmed"
        val fallbackNotes = if (current.notes.isNotBlank()) current.notes.trim() else "Details to be stored later"

        _uiState.update {
            it.copy(
                customerName = fallbackName,
                customerPhone = fallbackPhone,
                customerAddress = fallbackAddress,
                notes = fallbackNotes,
                nameError = null,
                phoneError = null,
                addressError = null,
                isDetailsDeferred = true,
                currentStep = 2
            )
        }
    }

    fun validateStep1(): Boolean {
        val state = _uiState.value
        var hasError = false
        var nameErr: String? = null
        var phoneErr: String? = null
        var addrErr: String? = null

        if (state.customerName.trim().isEmpty()) {
            nameErr = "Please enter your name"
            hasError = true
        }

        val cleanedPhone = state.customerPhone.filter { it.isDigit() }
        if (cleanedPhone.length < 10) {
            phoneErr = "Enter a valid 10-digit mobile number"
            hasError = true
        }

        if (state.customerAddress.trim().isEmpty()) {
            addrErr = "Please enter your home/service address"
            hasError = true
        }

        _uiState.update {
            it.copy(
                nameError = nameErr,
                phoneError = phoneErr,
                addressError = addrErr
            )
        }

        return !hasError
    }

    fun goToNextStep() {
        val current = _uiState.value.currentStep
        if (current == 1) {
            if (validateStep1()) {
                _uiState.update { it.copy(currentStep = 2) }
            }
        } else if (current == 2) {
            _uiState.update { it.copy(currentStep = 3) }
        }
    }

    fun goToPreviousStep() {
        val current = _uiState.value.currentStep
        if (current > 1 && current <= 3) {
            _uiState.update { it.copy(currentStep = current - 1) }
        }
    }

    fun confirmBooking() {
        val state = _uiState.value
        val service = state.service ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val scheduledTimeText = if (state.isFlexibleSchedule) {
                "Flexible Timing (Technician will coordinate)"
            } else {
                "${state.scheduledDate} (${state.selectedTimeSlot.timing})"
            }

            val (lat, lng) = when (state.selectedBlock) {
                HisarBlock.HISAR_1.displayName -> 29.1492 to 75.7217
                HisarBlock.HISAR_2.displayName -> 29.1550 to 75.7350
                HisarBlock.BARWALA.displayName -> 29.3787 to 75.9142
                HisarBlock.ADAMPUR.displayName -> 29.2885 to 75.4593
                HisarBlock.NARNAUND.displayName -> 29.2155 to 76.1408
                HisarBlock.UKLANA.displayName -> 29.5160 to 75.8710
                else -> 29.1492 to 75.7217
            }

            val bookingEntity = BookingEntity(
                name = state.customerName.trim(),
                phone = state.customerPhone.trim(),
                address = state.customerAddress.trim(),
                block = state.selectedBlock,
                serviceName = service.name,
                category = service.category,
                scheduledTime = scheduledTimeText,
                price = service.price,
                status = "Pending",
                providerStatus = "Finding Partner",
                paymentStatus = "Pending",
                latitude = lat,
                longitude = lng,
                notes = state.notes.trim()
            )

            val bookingId = bookingRepository.createBooking(bookingEntity)

            // Build WhatsApp Intent URL
            val messageText = """
                *New Booking via Local Service Hub (Hisar)*
                ━━━━━━━━━━━━━━━━━━━━━
                *Booking ID:* #LSH-$bookingId
                *Service:* ${service.name} (${service.price})
                *Customer Name:* ${state.customerName.trim()}
                *Phone:* ${state.customerPhone.trim()}
                *Address:* ${state.customerAddress.trim()}
                *Block:* ${state.selectedBlock}, Hisar
                *Scheduled Date & Time:* $scheduledTimeText
                *Notes:* ${if (state.notes.isNotEmpty()) state.notes else "None"}
                ━━━━━━━━━━━━━━━━━━━━━
                _Please confirm my technician assignment._
            """.trimIndent()

            val encodedMessage = URLEncoder.encode(messageText, "UTF-8")
            val whatsAppUrl = "https://wa.me/919588323460?text=$encodedMessage"

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    createdBookingId = bookingId,
                    currentStep = 4, // Success step
                    whatsAppIntentUri = whatsAppUrl
                )
            }
        }
    }

    fun resetBooking(service: ServiceEntity? = null) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val defaultDate = dateFormat.format(calendar.time)

        _uiState.update {
            it.copy(
                currentStep = 1,
                service = service ?: it.service,
                scheduledDate = defaultDate,
                selectedTimeSlot = TimeSlot.MORNING,
                notes = "",
                createdBookingId = null,
                whatsAppIntentUri = ""
            )
        }
    }
}

class BookingViewModelFactory(
    private val bookingRepository: BookingRepository,
    private val serviceRepository: ServiceRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingViewModel(bookingRepository, serviceRepository, userPreferences) as T
    }
}
