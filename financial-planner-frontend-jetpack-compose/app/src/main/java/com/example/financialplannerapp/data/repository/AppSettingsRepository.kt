package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.dao.AppSettingsDao
import com.example.financialplannerapp.data.model.AppSettings
import kotlinx.coroutines.flow.Flow

/**
 * App Settings Repository
 * 
 * Handles database operations for application settings.
 * Provides reactive access to settings changes.
 */
class AppSettingsRepository(private val dao: AppSettingsDao) {
    
    /**
     * Get app settings as Flow for reactive updates
     */
    fun getAppSettingsFlow(userId: String): Flow<AppSettings?> {
        return dao.getSettings()
    }
    
    /**
     * Get current app settings
     */
    suspend fun getAppSettings(userId: String): AppSettings? {
        return dao.getSettingsOnce()
    }
    
    /**
     * Save app settings
     */
    suspend fun saveAppSettings(settings: AppSettings) {
        dao.insertSettings(settings.copy(updatedAt = System.currentTimeMillis()))
    }
    
    /**
     * Update app settings
     */
    suspend fun updateAppSettings(settings: AppSettings) {
        dao.updateSettings(settings.copy(updatedAt = System.currentTimeMillis()))
    }
}