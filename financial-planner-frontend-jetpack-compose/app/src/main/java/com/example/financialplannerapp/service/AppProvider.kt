package com.example.financialplannerapp.service

import androidx.compose.runtime.*
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.example.financialplannerapp.data.model.Theme // Assuming Theme is an enum or similar
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme // Your app's theme composable
import com.example.financialplannerapp.service.TranslationProvider

val LocalReactiveSettingsService = compositionLocalOf<ReactiveSettingsService> {
    error("No ReactiveSettingsService provided")
}

// CompositionLocal for translation provider
val LocalTranslationProvider = staticCompositionLocalOf<TranslationProvider> { error("No TranslationProvider provided") }

// Provide AppContainer for dependency injection
val LocalAppContainer = staticCompositionLocalOf<AppContainer> { error("No AppContainer provided") }

@Composable
fun AppProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // Obtain AppContainer from Application
    val appContainer = (context.applicationContext as MainApplication).appContainer
    // Assuming ReactiveSettingsService.getInstance() doesn't need dbHelper directly for initialization anymore,
    // or it's handled internally. If dbHelper is still needed, it should be passed.
    val settingsService = remember { ReactiveSettingsService.getInstance(context) }
    val translationService = remember { TranslationServiceImpl.getInstance(context) }

    // Collect current settings and translation state
    val currentSettings by settingsService.appSettingsFlow.collectAsState()
    val translationState by translationService.translationStateFlow.collectAsState()

    // Effect to update translation service language when settings change
    LaunchedEffect(currentSettings?.language) {
        currentSettings?.language?.let { langCode ->
            translationService.changeLanguage(langCode)
        }
    }

    // Provide services and theme down the tree
    CompositionLocalProvider(
        LocalAppContainer provides appContainer,
        LocalReactiveSettingsService provides settingsService,
        LocalTranslationProvider provides translationService
    ) {
        // Determine the theme based on settings.
        // FinancialPlannerAppTheme will handle the actual theme application (dark/light/system).
        // It might internally observe currentSettings.theme or accept it as a parameter.
        val useDarkTheme = when (currentSettings?.theme) {
            Theme.DARK -> true
            Theme.LIGHT -> false
            Theme.SYSTEM -> null // Or use isSystemInDarkTheme()
            null -> null // Default or system
        }

        FinancialPlannerAppTheme(darkThemeOption = useDarkTheme) { // Pass theme preference
            // The content will now have access to settings and translations
            // No need to wrap with TranslationProvider here if LocalTranslationProvider is already set
            content()
        }
    }
}