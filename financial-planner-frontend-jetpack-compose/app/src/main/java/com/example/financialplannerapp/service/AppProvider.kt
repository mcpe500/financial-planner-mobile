package com.example.financialplannerapp.service

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.AppContainer
import com.example.financialplannerapp.data.model.TranslationProvider

val LocalReactiveSettingsService = compositionLocalOf<ReactiveSettingsService> {
    error("No ReactiveSettingsService provided")
}

// CompositionLocal for translation provider
val LocalTranslationProvider = compositionLocalOf<TranslationProvider> { 
    error("No TranslationProvider provided") 
}

// Provide AppContainer for dependency injection
val LocalAppContainer = compositionLocalOf<AppContainer> { 
    error("No AppContainer provided") 
}

@Composable
fun AppProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    // Obtain AppContainer from Application
    val appContainer = (context.applicationContext as MainApplication).appContainer
    
    // Initialize services using correct methods from the codebase
    val settingsService = remember { ReactiveSettingsService.getInstance() }
    val translationService = remember { TranslationServiceImpl.getInstance(context) }

    // Collect current settings and translation state
    val currentSettings by settingsService.currentSettings.collectAsState()
    val translationState by translationService.translationStateFlow.collectAsState()

    // Effect to update translation service language when settings change
    LaunchedEffect(currentSettings.language) {
        translationService.changeLanguage(currentSettings.language)
    }

    // Provide services and theme down the tree
    CompositionLocalProvider(
        LocalAppContainer provides appContainer,
        LocalReactiveSettingsService provides settingsService,
        LocalTranslationProvider provides translationService
    ) {
        // Determine the theme based on settings using string values
        val useDarkTheme = when (currentSettings.theme) {
            "dark" -> true
            "light" -> false
            "system" -> isSystemInDarkTheme() // Let system decide
            else -> isSystemInDarkTheme() // Default to system
        }

        FinancialPlannerAppTheme(darkTheme = useDarkTheme) {
            content()
        }
    }
}