package com.example.financialplannerapp.data

import com.example.financialplannerapp.data.local.model.AppSettingsEntity

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

// Mapper functions
fun AppSettingsEntity.toAppSettings(): AppSettings {
    return AppSettings(
        theme = this.theme,
        language = this.language,
        currency = this.currency,
        notificationsEnabled = this.notificationsEnabled,
        syncOnWifiOnly = this.syncOnWifiOnly,
        autoBackupEnabled = this.autoBackupEnabled
    )
}

fun AppSettings.toAppSettingsEntity(
    id: Int = 0, // Default ID for the single settings record
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = System.currentTimeMillis()
): AppSettingsEntity {
    return AppSettingsEntity(
        id = id,
        theme = this.theme,
        language = this.language,
        currency = this.currency,
        notificationsEnabled = this.notificationsEnabled,
        syncOnWifiOnly = this.syncOnWifiOnly,
        autoBackupEnabled = this.autoBackupEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}