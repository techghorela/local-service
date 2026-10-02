package com.example.data.model

/**
 * Data classes and enums for Local Service Hub.
 */

enum class HisarBlock(val displayName: String) {
    HISAR_1("Hisar-1"),
    HISAR_2("Hisar-2"),
    BARWALA("Barwala"),
    ADAMPUR("Adampur"),
    NARNAUND("Narnaund"),
    UKLANA("Uklana");

    companion object {
        val allNames: List<String> = entries.map { it.displayName }
        fun fromDisplayName(name: String): HisarBlock =
            entries.find { it.displayName.equals(name, ignoreCase = true) } ?: HISAR_1
    }
}

enum class ServiceCategory(val displayName: String, val iconResName: String) {
    APPLIANCE_REPAIR("Appliance Repair", "ic_appliance"),
    HOME_MAINTENANCE("Home Maintenance", "ic_maintenance"),
    CONSTRUCTION("Construction", "ic_construction"),
    BEAUTY_SALON("Beauty & Salon", "ic_beauty");

    companion object {
        val allCategories = entries.map { it.displayName }
    }
}

enum class TimeSlot(val title: String, val timing: String) {
    MORNING("Morning Slot", "9:00 AM - 12:00 PM"),
    AFTERNOON("Afternoon Slot", "12:00 PM - 4:00 PM"),
    EVENING("Evening Slot", "4:00 PM - 8:00 PM");

    companion object {
        fun fromTitle(title: String): TimeSlot =
            entries.find { it.title.equals(title, ignoreCase = true) } ?: MORNING
    }
}

enum class BookingStatus(val displayName: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class PromoBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val discountTag: String,
    val imageResName: String,
    val targetCategory: String
)

data class UserProfile(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val block: String = HisarBlock.HISAR_1.displayName
)
