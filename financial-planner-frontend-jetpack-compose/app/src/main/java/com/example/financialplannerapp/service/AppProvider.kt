package com.example.financialplannerapp.service

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectAsState

/**
 * App Provider
 * 
 * Main provider that wraps the entire app with all necessary services.
 * Handles theme switching, translations, and settings management.
 */
@Composable
fun AppProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    // Create service instances
    val themeService = remember { ThemeService.getInstance() }
    val translationService = remember { TranslationService.getInstance() }
    val settingsManager = remember { 
        SettingsManager.getInstance(context, themeService, translationService)
    }
    
    // Observe current settings and theme changes
    val currentSettings by settingsManager.currentSettings.collectAsState(
        initial = com.example.financialplannerapp.data.AppSettings()
    )
    
    // Observe theme changes from the theme service
    val currentTheme by themeService.currentTheme.collectAsState(initial = "system")
    
    // Apply theme based on current settings or theme service
    val effectiveTheme = currentSettings.theme.takeIf { it.isNotEmpty() } ?: currentTheme
    
    // Apply theme based on current settings
    ThemeProvider(theme = effectiveTheme) {
        TranslationProvider(language = currentSettings.language) {
            content()
        }
    }
}