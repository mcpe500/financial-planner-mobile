package com.example.financialplannerapp.service

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.financialplannerapp.data.AppSettings
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper
import com.example.financialplannerapp.data.toAppSettings
import com.example.financialplannerapp.data.toAppSettingsEntity
import com.example.financialplannerapp.data.remote.ApiService
import com.example.financialplannerapp.data.remote.RetrofitClient
import com.example.financialplannerapp.MainApplication

/**
 * Settings Manager
 * 
 * Centralized manager for all app settings including theme, language, and other preferences.
 * Integrates with database persistence and reactive services.
 */
class SettingsManager private constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val themeService: ThemeService = ThemeService.getInstance()
) {
    private val databaseHelper = AppSettingsDatabaseHelper.getInstance(context, apiService)
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _currentSettings = MutableStateFlow(AppSettings())
    val currentSettings: Flow<AppSettings> = _currentSettings.asStateFlow()
    
    companion object {
        @Volatile
        private var INSTANCE: SettingsManager? = null
        
        fun getInstance(
            context: Context,
            apiService: ApiService,
            themeService: ThemeService = ThemeService.getInstance()
        ): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(
                    context.applicationContext,
                    apiService,
                    themeService
                ).also { INSTANCE = it }
            }
        }
    }
    
    init {
        // Load initial settings
        scope.launch {
            loadSettings()
        }
    }
    
    /**
     * Load settings from database
     */
    private suspend fun loadSettings() {
        try {
            val settingsEntity = databaseHelper.getAppSettings("default_user")
            val settings = settingsEntity.toAppSettings()
            _currentSettings.value = settings
            
            // Apply settings to services
            themeService.setTheme(settings.theme)
        } catch (e: Exception) {
            // Use default settings if loading fails
            val defaultSettings = AppSettings()
            _currentSettings.value = defaultSettings
            themeService.setTheme(defaultSettings.theme)
        }
    }
    
    /**
     * Get current settings synchronously
     */
    suspend fun getCurrentSettings(): AppSettings {
        return try {
            val settingsEntity = databaseHelper.getAppSettings("default_user")
            settingsEntity.toAppSettings()
        } catch (e: Exception) {
            AppSettings()
        }
    }
    
    /**
     * Set theme and persist to database
     */
    suspend fun setTheme(theme: String) {
        themeService.setTheme(theme)
        updateSetting { it.copy(theme = theme) }
    }
    
    /**
     * Set language and persist to database
     */
    suspend fun setLanguage(language: String) {
        updateSetting { it.copy(language = language) }
    }
    
    /**
     * Set currency and persist to database
     */
    suspend fun setCurrency(currency: String) {
        updateSetting { it.copy(currency = currency) }
    }
    
    /**
     * Set notifications and persist to database
     */
    suspend fun setNotifications(enabled: Boolean) {
        updateSetting { it.copy(notificationsEnabled = enabled) }
    }
    
    /**
     * Update setting with a transformation function
     */
    private suspend fun updateSetting(transform: (AppSettings) -> AppSettings) {
        try {
            val currentSettingsEntity = databaseHelper.getAppSettings("default_user")
            val currentSettings = currentSettingsEntity.toAppSettings()
            val updatedSettings = transform(currentSettings)
            databaseHelper.saveAppSettings("default_user", updatedSettings.toAppSettingsEntity())
            _currentSettings.value = updatedSettings
        } catch (e: Exception) {
            // Handle error gracefully
        }
    }
}

/**
 * Composable function to remember SettingsManager
 */
@Composable
fun rememberSettingsManager(): SettingsManager {
    val context = LocalContext.current
    val app = context.applicationContext as MainApplication
    val apiService = app.appContainer.apiService
    return remember {
        SettingsManager.getInstance(context, apiService)
    }
}