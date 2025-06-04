package com.example.financialplannerapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.financialplannerapp.data.dao.AppSettingsDao
import com.example.financialplannerapp.data.model.AppSettings

/**
 * Repository for App Settings
 * 
 * Provides a clean API for accessing app settings data.
 * Handles data mapping and business logic for settings operations.
 */
class AppSettingsRepository(private val dao: AppSettingsDao) {
    
    /**
     * Get app settings as Flow for reactive updates
     */
    fun getSettings(): Flow<AppSettings?> {
        return dao.getSettings()
    }
    
    /**
     * Get app settings once (immediate access)
     */
    suspend fun getSettingsOnce(): AppSettings? {
        return dao.getSettingsOnce()
    }
    
    /**
     * Save app settings (insert or update)
     */
    suspend fun saveSettings(settings: AppSettings) {
        dao.insertSettings(settings.copy(updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * Update specific settings fields
     */
    suspend fun updateTheme(theme: String) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(theme = theme, updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun updateLanguage(language: String) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(language = language, updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun updateCurrency(currency: String) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(currency = currency, updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun updateNotifications(enabled: Boolean) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(notificationsEnabled = enabled, updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * Reset settings to defaults
     */
    suspend fun resetSettings() {
        dao.deleteAllSettings()
        dao.insertSettings(getDefaultSettings())
    }
    
    /**
     * Check if settings exist
     */
    suspend fun settingsExist(): Boolean {
        return dao.settingsExist() > 0
    }
    
    /**
     * Get default settings
     */
    private fun getDefaultSettings(): AppSettings {
        return AppSettings(
            id = 0,
            theme = "system",
            language = "id",
            currency = "IDR",
            notificationsEnabled = true,
            syncOnWifiOnly = false,
            autoBackupEnabled = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}