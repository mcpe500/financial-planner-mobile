package com.example.financialplannerapp.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.financialplannerapp.data.repository.AppSettingsRepository

/**
 * App Settings Data Model for UI
 * 
 * Simplified model for application settings used in UI layer.
 */
data class AppSettings(
    val theme: String = "system", // light, dark, system
    val language: String = "id", // id, en, zh
    val currency: String = "IDR",
    val notificationsEnabled: Boolean = true,
    val syncOnWifiOnly: Boolean = false,
    val autoBackupEnabled: Boolean = true
)

/**
 * App Settings Database Helper
 * 
 * Simplified helper for managing app settings with Room database.
 * Provides high-level operations for the UI layer.
 */
class AppSettingsDatabaseHelper private constructor(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val settingsRepository = AppSettingsRepository(database.appSettingsDao())
    
    companion object {
        @Volatile
        private var INSTANCE: AppSettingsDatabaseHelper? = null
        
        fun getInstance(context: Context): AppSettingsDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppSettingsDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
      /**
     * Get app settings from database
     */
    suspend fun getAppSettings(userId: String): AppSettings {
        return try {
            val dbSettings = settingsRepository.getAppSettings(userId)
            dbSettings?.let {
                AppSettings(
                    theme = it.theme,
                    language = it.language,
                    currency = it.currency,
                    notificationsEnabled = it.notificationsEnabled,
                    syncOnWifiOnly = it.syncOnWifiOnly,
                    autoBackupEnabled = it.autoBackupEnabled
                )
            } ?: AppSettings()
        } catch (e: Exception) {
            AppSettings()
        }
    }
    
    /**
     * Save app settings to database
     */
    suspend fun saveAppSettings(userId: String, settings: AppSettings) {
        val dbSettings = com.example.financialplannerapp.data.model.AppSettings(
            theme = settings.theme,
            language = settings.language,
            currency = settings.currency,
            notificationsEnabled = settings.notificationsEnabled,
            syncOnWifiOnly = settings.syncOnWifiOnly,
            autoBackupEnabled = settings.autoBackupEnabled,
            updatedAt = System.currentTimeMillis()
        )
        settingsRepository.saveAppSettings(dbSettings)
    }
    
    /**
     * Update app settings using a transformation function
     */
    suspend fun updateSetting(userId: String, updateBlock: (AppSettings) -> AppSettings) {
        val currentSettings = getAppSettings(userId)
        val updatedSettings = updateBlock(currentSettings)
        saveAppSettings(userId, updatedSettings)
    }
    
    /**
     * Get reactive settings updates as Flow
     */
    fun getAppSettingsFlow(userId: String): Flow<AppSettings> {
        return settingsRepository.getAppSettingsFlow(userId).map { dbSettings ->
            dbSettings?.let {
                AppSettings(
                    theme = it.theme,
                    language = it.language,
                    currency = it.currency,
                    notificationsEnabled = it.notificationsEnabled,
                    syncOnWifiOnly = it.syncOnWifiOnly,
                    autoBackupEnabled = it.autoBackupEnabled
                )
            } ?: AppSettings()
        }
    }
}