package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val name: String,
    val price: String,
    val priceRange: String,
    val description: String,
    val iconName: String,
    val rating: Double = 4.8,
    val reviewCount: Int = 124,
    val isPopular: Boolean = false,
    val isActive: Boolean = true
)
