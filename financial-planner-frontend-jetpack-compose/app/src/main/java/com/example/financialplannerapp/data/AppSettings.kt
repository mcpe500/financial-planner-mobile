package com.example.financialplannerapp.data

/**
 * App Settings Data Class
 * 
 * Simple data class for app settings used throughout the UI layer.
 * Maps to Room entity for database persistence.
 */
data class AppSettings(
    val theme: String = "system", // light, dark, system
    val language: String = "id", // id, en, zh
    val currency: String = "IDR", // IDR, USD, EUR, JPY
    val notificationsEnabled: Boolean = true,
    val syncOnWifiOnly: Boolean = false,
    val autoBackupEnabled: Boolean = true
)