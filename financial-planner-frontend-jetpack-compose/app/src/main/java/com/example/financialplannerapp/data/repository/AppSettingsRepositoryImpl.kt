package com.example.financialplannerapp.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.financialplannerapp.data.local.dao.AppSettingsDao
import com.example.financialplannerapp.data.local.model.AppSettingsEntity

/**
 * Repository implementation for App Settings
 * 
 * Provides a clean API for accessing app settings data.
 * Handles data mapping and business logic for settings operations.
 */
class AppSettingsRepositoryImpl constructor(
    private val dao: AppSettingsDao
) : AppSettingsRepository {
    
    /**
     * Get app settings as Flow for reactive updates
     */
    override fun getSettings(): Flow<AppSettingsEntity?> {
        return dao.getSettings()
    }
    
    /**
     * Get app settings once (immediate access)
     */
    override suspend fun getSettingsOnce(): AppSettingsEntity? {
        return dao.getSettingsOnce()
    }
    
    /**
     * Save app settings (insert or update)
     */
    override suspend fun saveSettings(settings: AppSettingsEntity) {
        dao.insertSettings(settings.copy(updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * Update specific settings fields
     */
    override suspend fun updateTheme(theme: String) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(theme = theme, updatedAt = System.currentTimeMillis()))
    }
    
    override suspend fun updateLanguage(language: String) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(language = language, updatedAt = System.currentTimeMillis()))
    }
    
    override suspend fun updateCurrency(currency: String) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(currency = currency, updatedAt = System.currentTimeMillis()))
    }
    
    override suspend fun updateNotifications(enabled: Boolean) {
        val current = dao.getSettingsOnce() ?: getDefaultSettings()
        dao.insertSettings(current.copy(notificationsEnabled = enabled, updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * Reset settings to defaults
     */
    override suspend fun resetSettings() {
        dao.deleteAllSettings()
        dao.insertSettings(getDefaultSettings())
    }
    
    /**
     * Check if settings exist
     */
    override suspend fun settingsExist(): Boolean {
        return dao.settingsExist() > 0
    }
    
    /**
     * Get default settings
     */
    private fun getDefaultSettings(): AppSettingsEntity {
        return AppSettingsEntity(
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
