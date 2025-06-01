package com.example.financialplannerapp.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.financialplannerapp.data.repository.AppSettingsRepository

/**
 * App Settings Database Helper
 * 
 * High-level helper for managing app settings with database persistence.
 * Provides a simplified interface for settings operations throughout the app.
 */
class AppSettingsDatabaseHelper private constructor(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val repository = AppSettingsRepository(database.appSettingsDao())
    
    companion object {
        @Volatile
        private var INSTANCE: AppSettingsDatabaseHelper? = null
        
        /**
         * Get singleton instance with application context to prevent memory leaks
         */
        fun getInstance(context: Context): AppSettingsDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppSettingsDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
      /**
     * Get app settings for a user (using default implementation for now)
     */
    suspend fun getAppSettings(userId: String): AppSettings {
        val roomSettings = repository.getSettingsOnce()
        return if (roomSettings != null) {
            AppSettings(
                theme = roomSettings.theme,
                language = roomSettings.language,
                currency = roomSettings.currency,
                notificationsEnabled = roomSettings.notificationsEnabled,
                syncOnWifiOnly = roomSettings.syncOnWifiOnly,
                autoBackupEnabled = roomSettings.autoBackupEnabled
            )
        } else {
            getDefaultSettings()
        }
    }
    
    /**
     * Save app settings for a user
     */
    suspend fun saveAppSettings(userId: String, settings: AppSettings) {
        val roomSettings = com.example.financialplannerapp.data.model.AppSettings(
            id = 0,
            theme = settings.theme,
            language = settings.language,
            currency = settings.currency,
            notificationsEnabled = settings.notificationsEnabled,
            syncOnWifiOnly = settings.syncOnWifiOnly,
            autoBackupEnabled = settings.autoBackupEnabled,
            updatedAt = System.currentTimeMillis()
        )
        repository.saveSettings(roomSettings)
    }
    
    /**
     * Get app settings as Flow for reactive updates
     */
    fun getAppSettingsFlow(userId: String): Flow<AppSettings?> {
        return repository.getSettings().map { roomSettings ->
            roomSettings?.let {
                AppSettings(
                    theme = it.theme,
                    language = it.language,
                    currency = it.currency,
                    notificationsEnabled = it.notificationsEnabled,
                    syncOnWifiOnly = it.syncOnWifiOnly,
                    autoBackupEnabled = it.autoBackupEnabled
                )
            }
        }
    }
    
    /**
     * Update theme setting
     */
    suspend fun updateTheme(userId: String, theme: String) {
        repository.updateTheme(theme)
    }
    
    /**
     * Update language setting
     */
    suspend fun updateLanguage(userId: String, language: String) {
        repository.updateLanguage(language)
    }
    
    /**
     * Update currency setting
     */
    suspend fun updateCurrency(userId: String, currency: String) {
        repository.updateCurrency(currency)
    }
    
    /**
     * Update notification setting
     */
    suspend fun updateNotifications(userId: String, enabled: Boolean) {
        repository.updateNotifications(enabled)
    }
    
    /**
     * Reset settings to defaults
     */
    suspend fun resetSettings(userId: String) {
        repository.resetSettings()
    }
    
    /**
     * Get default settings
     */
    private fun getDefaultSettings(): AppSettings {
        return AppSettings(
            theme = "system",
            language = "id",
            currency = "IDR",
            notificationsEnabled = true,
            syncOnWifiOnly = false,
            autoBackupEnabled = true
        )
    }
}