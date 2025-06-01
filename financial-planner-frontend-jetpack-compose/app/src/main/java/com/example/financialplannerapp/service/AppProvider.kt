package com.example.financialplannerapp.service

import androidx.compose.runtime.*
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import com.example.financialplannerapp.utils.TranslationProvider
import com.example.financialplannerapp.utils.ThemeProvider
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper

val LocalReactiveSettingsService = compositionLocalOf<ReactiveSettingsService> {
    error("No ReactiveSettingsService provided")
}

/**
 * App Provider - Main provider that wraps the entire app with database-backed settings
 */
@Composable
fun AppProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { AppSettingsDatabaseHelper.getInstance(context) }
    val settingsService = remember { ReactiveSettingsService.getInstance() }
    
    // Initialize settings service
    LaunchedEffect(Unit) {
        settingsService.initialize(dbHelper)
    }
    
    // Collect current settings
    val currentSettings by settingsService.currentSettings.collectAsState()
    
    // Apply theme and translation providers with reactive settings
    CompositionLocalProvider(
        LocalReactiveSettingsService provides settingsService,
    ) {
        ThemeProvider(theme = currentSettings.theme) {
            TranslationProvider(language = currentSettings.language) {
                content()
            }
        }
    }
}