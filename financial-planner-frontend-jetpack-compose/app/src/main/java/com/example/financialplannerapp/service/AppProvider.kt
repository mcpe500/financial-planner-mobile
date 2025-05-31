package com.example.financialplannerapp.service

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper

/**
 * App Provider
 * 
 * Main provider that combines theme and translation services with database persistence.
 * Automatically loads and saves user preferences.
 * 
 * Features:
 * - Reactive theme switching with database persistence
 * - Dynamic language switching with immediate UI updates
 * - Seamless integration between services
 * - Memory-safe context handling
 * 
 * Usage:
 * Wrap your entire app with this provider:
 * ```kotlin
 * AppProvider {
 *     // Your app content
 * }
 * ```
 */
@Composable
fun AppProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Database helper for loading/saving settings
    val databaseHelper = remember { AppSettingsDatabaseHelper.getInstance(context) }
    val userId = "default_user" // In production, get from authentication
    
    // State for current settings
    var currentTheme by remember { mutableStateOf("system") }
    var currentLanguage by remember { mutableStateOf("id") }
    
    // Load settings from database on startup
    LaunchedEffect(Unit) {
        val settings = databaseHelper.getAppSettings(userId)
        currentTheme = settings.theme
        currentLanguage = settings.language
    }
    
    // Theme switching handler
    val onThemeChange: (String) -> Unit = { theme ->
        currentTheme = theme
        coroutineScope.launch {
            databaseHelper.updateSetting(userId) { it.copy(theme = theme) }
        }
    }
    
    // Language switching handler
    val onLanguageChange: (String) -> Unit = { language ->
        currentLanguage = language
        coroutineScope.launch {
            databaseHelper.updateSetting(userId) { it.copy(language = language) }
        }
    }
    
    // Enhanced Translation Provider with live language switching
    TranslationProvider(language = currentLanguage) {
        // Enhanced Theme Provider with live theme switching and service integration
        EnhancedThemeProvider(
            theme = currentTheme,
            onThemeChange = onThemeChange,
            content = content
        )
    }
}

/**
 * Enhanced Theme Provider
 * 
 * Provides theme service with integrated change handling and database persistence.
 */
@Composable
private fun EnhancedThemeProvider(
    theme: String,
    onThemeChange: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val themeService = remember { ThemeService.getInstance() }
    val isSystemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    // Update theme service when theme changes
    LaunchedEffect(theme) {
        themeService.setTheme(theme)
    }
    
    val isDarkTheme = themeService.isDarkTheme(isSystemInDarkTheme)
    val colorScheme = themeService.getColorScheme(isDarkTheme)
      // Enhanced theme service with change callback
    val enhancedThemeService = remember(themeService, onThemeChange) {
        EnhancedThemeServiceWrapper(themeService, onThemeChange)
    }
    
    CompositionLocalProvider(
        LocalThemeService provides enhancedThemeService
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

/**
 * Enhanced Theme Service Wrapper
 * 
 * Wraps the theme service to provide change callbacks.
 */
private class EnhancedThemeServiceWrapper(
    private val themeService: ThemeService,
    private val onThemeChange: (String) -> Unit
) : ThemeService() {
    
    override fun setTheme(theme: String) {
        themeService.setTheme(theme)
        onThemeChange(theme)
    }
    
    override fun getCurrentTheme(): String = themeService.getCurrentTheme()
    
    override fun isDarkTheme(isSystemInDarkTheme: Boolean): Boolean = 
        themeService.isDarkTheme(isSystemInDarkTheme)
    
    override fun getColorScheme(isDark: Boolean) = 
        themeService.getColorScheme(isDark)
}

/**
 * Settings Manager Composable
 * 
 * Provides reactive settings management for the entire app.
 * Use this to get/set settings from any composable.
 */
@Composable
fun rememberSettingsManager(): SettingsManager {
    val context = LocalContext.current
    val translator = LocalTranslator.current
    val themeService = LocalThemeService.current
    val coroutineScope = rememberCoroutineScope()
    
    return remember {
        SettingsManager(
            context = context,
            translator = translator,
            themeService = themeService,
            coroutineScope = coroutineScope
        )
    }
}

/**
 * Settings Manager Class
 * 
 * Centralized settings management with reactive updates.
 */
class SettingsManager(
    private val context: Context,
    private val translator: TranslationService,
    private val themeService: ThemeService,
    private val coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    private val databaseHelper = AppSettingsDatabaseHelper.getInstance(context)
    private val userId = "default_user"
    
    /**
     * Change app theme
     */
    fun setTheme(theme: String) {
        themeService.setTheme(theme)
        coroutineScope.launch {
            databaseHelper.updateSetting(userId) { it.copy(theme = theme) }
        }
    }
    
    /**
     * Change app language
     */
    fun setLanguage(language: String) {
        translator.setLanguage(language)
        coroutineScope.launch {
            databaseHelper.updateSetting(userId) { it.copy(language = language) }
        }
    }
    
    /**
     * Change currency
     */
    fun setCurrency(currency: String) {
        coroutineScope.launch {
            databaseHelper.updateSetting(userId) { it.copy(currency = currency) }
        }
    }
    
    /**
     * Toggle notifications
     */
    fun setNotifications(enabled: Boolean) {
        coroutineScope.launch {
            databaseHelper.updateSetting(userId) { it.copy(notificationsEnabled = enabled) }
        }
    }
    
    /**
     * Get current settings
     */
    suspend fun getCurrentSettings(): com.example.financialplannerapp.data.AppSettings {
        return databaseHelper.getAppSettings(userId)
    }
    
    /**
     * Get reactive settings flow
     */
    fun getSettingsFlow(): kotlinx.coroutines.flow.Flow<com.example.financialplannerapp.data.AppSettings> {
        return databaseHelper.getAppSettingsFlow(userId)
    }
}