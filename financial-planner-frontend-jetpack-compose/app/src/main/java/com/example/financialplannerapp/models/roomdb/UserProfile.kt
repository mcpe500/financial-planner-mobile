package com.example.financialplannerapp.models.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String, // This could be the user ID from your backend
    val name: String?,
    val email: String?,
    val profileImageUrl: String?,
    val currencyPreference: String? = "IDR", // Default currency
    val notificationEnabled: Boolean = true,
    val pinCode: String?, // Encrypted PIN
    val biometricAuthEnabled: Boolean = false,
    val lastLogin: Date?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)