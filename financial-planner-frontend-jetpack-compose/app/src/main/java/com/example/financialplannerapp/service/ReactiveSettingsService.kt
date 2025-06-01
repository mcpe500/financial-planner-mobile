package com.example.financialplannerapp.service

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.example.financialplannerapp.data.AppSettings
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper

/**
 * Reactive Settings Service
 * 
 * Manages app settings with reactive updates and database persistence.
 * Provides immediate UI updates when settings change.
 */
class ReactiveSettingsService private constructor() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Default settings
    private val defaultSettings = AppSettings(
        theme = "system",
        language = "id", 
        currency = "IDR",
        notificationsEnabled = true,
        syncOnWifiOnly = false,
        autoBackupEnabled = true
    )
    
    // Current settings state
    private val _currentSettings = MutableStateFlow(defaultSettings)
    val currentSettings: StateFlow<AppSettings> = _currentSettings.asStateFlow()
    
    // Database helper
    private var dbHelper: AppSettingsDatabaseHelper? = null
    
    companion object {
        @Volatile
        private var INSTANCE: ReactiveSettingsService? = null
        
        fun getInstance(): ReactiveSettingsService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReactiveSettingsService().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Initialize the service with database helper
     */
    suspend fun initialize(databaseHelper: AppSettingsDatabaseHelper) {
        dbHelper = databaseHelper
        loadSettings()
    }
    
    /**
     * Load settings from database
     */
    private suspend fun loadSettings() {
        dbHelper?.let { helper ->
            try {
                val settings = helper.getAppSettings("default_user") // Using default user ID
                _currentSettings.value = settings
            } catch (e: Exception) {
                // If loading fails, use defaults and save them
                _currentSettings.value = defaultSettings
                saveSettings(defaultSettings)
            }
        }
    }
    
    /**
     * Update settings with a transformation function
     */
    fun updateSettings(
        databaseHelper: AppSettingsDatabaseHelper,
        updateBlock: (AppSettings) -> AppSettings
    ) {
        serviceScope.launch {
            try {
                val currentSettings = _currentSettings.value
                val newSettings = updateBlock(currentSettings)
                
                // Update reactive state immediately
                _currentSettings.value = newSettings
                
                // Save to database
                saveSettings(databaseHelper, newSettings)
            } catch (e: Exception) {
                // Log error but don't crash the app
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Save settings to database
     */
    private suspend fun saveSettings(
        databaseHelper: AppSettingsDatabaseHelper,
        settings: AppSettings
    ) {
        try {
            databaseHelper.saveAppSettings("default_user", settings)
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }
    
    /**
     * Save settings using class-level dbHelper
     */
    private suspend fun saveSettings(settings: AppSettings) {
        dbHelper?.let { helper ->
            saveSettings(helper, settings)
        }
    }
    
    /**
     * Update theme specifically
     */
    fun updateTheme(databaseHelper: AppSettingsDatabaseHelper, theme: String) {
        updateSettings(databaseHelper) { settings ->
            settings.copy(theme = theme)
        }
    }
    
    /**
     * Update language specifically
     */
    fun updateLanguage(databaseHelper: AppSettingsDatabaseHelper, language: String) {
        updateSettings(databaseHelper) { settings ->
            settings.copy(language = language)
        }
    }
    
    /**
     * Update currency specifically
     */
    fun updateCurrency(databaseHelper: AppSettingsDatabaseHelper, currency: String) {
        updateSettings(databaseHelper) { settings ->
            settings.copy(currency = currency)
        }
    }
    
    /**
     * Update notifications specifically
     */
    fun updateNotifications(databaseHelper: AppSettingsDatabaseHelper, enabled: Boolean) {
        updateSettings(databaseHelper) { settings ->
            settings.copy(notificationsEnabled = enabled)
        }
    }
    
    /**
     * Get current theme
     */
    fun getCurrentTheme(): String = _currentSettings.value.theme
    
    /**
     * Get current language
     */
    fun getCurrentLanguage(): String = _currentSettings.value.language
    
    /**
     * Get current currency
     */
    fun getCurrentCurrency(): String = _currentSettings.value.currency
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        serviceScope.cancel()
    }
}
