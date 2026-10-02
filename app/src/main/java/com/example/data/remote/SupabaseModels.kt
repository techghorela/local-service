package com.example.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseBooking(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String,
    @SerialName("address") val address: String,
    @SerialName("block") val block: String,
    @SerialName("service_name") val serviceName: String,
    @SerialName("scheduled_time") val scheduledTime: String,
    @SerialName("status") val status: String = "Pending",
    @SerialName("provider_status") val providerStatus: String = "Finding Partner",
    @SerialName("payment_status") val paymentStatus: String = "Pending",
    @SerialName("latitude") val latitude: Double = 29.1492,
    @SerialName("longitude") val longitude: Double = 75.7217,
    @SerialName("razorpay_payment_id") val razorpayPaymentId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SupabasePartner(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("phone") val phone: String,
    @SerialName("service_category") val serviceCategory: String,
    @SerialName("rating") val rating: Double = 4.8,
    @SerialName("is_available") val isAvailable: Boolean = true,
    @SerialName("latitude") val latitude: Double = 29.1492,
    @SerialName("longitude") val longitude: Double = 75.7217,
    @SerialName("verification_status") val verificationStatus: String = "Verified"
)

@Serializable
data class SupabaseService(
    @SerialName("id") val id: String,
    @SerialName("category") val category: String,
    @SerialName("name") val name: String,
    @SerialName("price") val price: String,
    @SerialName("description") val description: String,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class SupabasePartnerLocation(
    @SerialName("id") val id: String? = null,
    @SerialName("partner_id") val partnerId: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("updated_at") val updatedAt: String? = null
)
