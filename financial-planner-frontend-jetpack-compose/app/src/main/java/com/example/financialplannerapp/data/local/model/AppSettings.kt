package com.example.financialplannerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * App Settings Entity for Room Database
 * 
 * Stores user preferences including theme, language, currency, and notification settings.
 * Uses REPLACE strategy to ensure only one settings record exists.
 */
@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Int = 0, // Single settings record
    val theme: String = "system", // light, dark, system
    val language: String = "id", // id, en, zh
    val currency: String = "IDR", // IDR, USD, EUR, JPY
    val notificationsEnabled: Boolean = true,
    val syncOnWifiOnly: Boolean = false,
    val autoBackupEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)