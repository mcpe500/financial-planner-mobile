package com.example.financialplannerapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * App Settings Entity for Room Database
 * 
 * Stores user application preferences including theme, language, currency, and notifications.
 */
@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 0, // Single row table for global settings
    val theme: String = "system", // light, dark, system
    val language: String = "id", // id, en, zh
    val currency: String = "IDR",
    val notificationsEnabled: Boolean = true,
    val syncOnWifiOnly: Boolean = false,
    val autoBackupEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)