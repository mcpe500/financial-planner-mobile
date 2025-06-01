package com.example.financialplannerapp.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.financialplannerapp.data.AppSettings
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper

/**
 * Local Settings Service for Compose
 * 
 * Provides a reactive settings service that can be used throughout the app
 * with proper persistence and state management.
 */
class LocalSettingsService {
    private val _currentSettings = MutableStateFlow(
        AppSettings(
            theme = "system",
            language = "id", 
            currency = "IDR",
            notificationsEnabled = true,
            syncOnWifiOnly = false,
            autoBackupEnabled = true
        )
    )
    val currentSettings: StateFlow<AppSettings> = _currentSettings.asStateFlow()
    
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    /**
     * Initialize the service with database helper
     */
    fun initialize(dbHelper: AppSettingsDatabaseHelper) {
        serviceScope.launch {
            try {
                // Load existing settings from database
                val savedSettings = dbHelper.getAppSettings("default_user")
                _currentSettings.value = savedSettings
                
                // Listen for database changes
                dbHelper.getAppSettingsFlow("default_user").collect { settings ->
                    settings?.let {
                        _currentSettings.value = it
                    }
                }
            } catch (e: Exception) {
                // If loading fails, keep default settings
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Update settings with a transformation function
     */
    fun updateSettings(
        dbHelper: AppSettingsDatabaseHelper,
        transform: (AppSettings) -> AppSettings
    ) {
        serviceScope.launch {
            try {
                val currentSettings = _currentSettings.value
                val newSettings = transform(currentSettings)
                
                // Update local state immediately for UI responsiveness
                _currentSettings.value = newSettings
                
                // Persist to database
                dbHelper.saveAppSettings("default_user", newSettings)
                
                // Update individual services based on changes
                if (currentSettings.theme != newSettings.theme) {
                    // Theme change logic could go here
                }
                
                if (currentSettings.language != newSettings.language) {
                    // Language change logic could go here
                    // Note: TranslationService.getInstance() might not be available
                    // This can be handled at the UI level instead
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: LocalSettingsService? = null
        
        fun getInstance(): LocalSettingsService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalSettingsService().also { INSTANCE = it }
            }
        }
    }
}