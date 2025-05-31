package com.example.financialplannerapp.service

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.financialplannerapp.utils.Translations

/**
 * Translation Service
 * 
 * Centralized translation management with reactive language switching.
 * Uses the existing Translations.kt file as the source of truth.
 */
class TranslationService private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: TranslationService? = null
        
        fun getInstance(): TranslationService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TranslationService().also { INSTANCE = it }
            }
        }
    }
    
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: Flow<String> = _currentLanguage.asStateFlow()
    
    private var currentLanguageCode = "en"
    
    /**
     * Set current language
     */
    fun setLanguage(languageCode: String) {
        if (languageCode in listOf("en", "id", "zh")) {
            currentLanguageCode = languageCode
            _currentLanguage.value = languageCode
            
            // Update the Translations object
            Translations.currentLanguage = when (languageCode) {
                "id" -> Translations.Language.INDONESIAN
                "zh" -> Translations.Language.CHINESE
                else -> Translations.Language.ENGLISH
            }
        }
    }
    
    /**
     * Get current language
     */
    fun getCurrentLanguage(): String = currentLanguageCode
    
    /**
     * Get translation using Translations.Key directly
     */
    fun get(key: Translations.Key): String {
        return Translations.get(key)
    }
    
    /**
     * Get translation with string key for compatibility
     */
    fun get(key: String): String {
        return try {
            // Map string keys to Translation.Key objects
            val translationKey = when (key) {
                "back" -> Translations.Key.Back
                "app_settings" -> Translations.Key.AppSettings
                "theme_setting" -> Translations.Key.ThemeSetting
                "theme_setting_desc" -> Translations.Key.ThemeSettingDesc
                "theme_light" -> Translations.Key.ThemeLight
                "theme_dark" -> Translations.Key.ThemeDark
                "theme_system" -> Translations.Key.ThemeSystem
                "theme_changed_to" -> Translations.Key.ThemeChangedTo
                "language_setting" -> Translations.Key.LanguageSetting
                "language_setting_desc" -> Translations.Key.LanguageSettingDesc
                "currency_setting" -> Translations.Key.CurrencySetting
                "currency_setting_desc" -> Translations.Key.CurrencySettingDesc
                "currency_idr" -> Translations.Key.CurrencyIdr
                "currency_usd" -> Translations.Key.CurrencyUsd
                "currency_eur" -> Translations.Key.CurrencyEur
                "currency_jpy" -> Translations.Key.CurrencyJpy
                "currency_changed_to" -> Translations.Key.CurrencyChangedTo
                "notifications_setting" -> Translations.Key.NotificationsSetting
                "notifications_setting_desc" -> Translations.Key.NotificationsSettingDesc
                "enable_notifications" -> Translations.Key.EnableNotifications
                "notifications_enabled" -> Translations.Key.NotificationsEnabled
                "notifications_disabled" -> Translations.Key.NotificationsDisabled
                "save" -> Translations.Key.Save
                "cancel" -> Translations.Key.Cancel
                "confirm" -> Translations.Key.Confirm
                "settings" -> Translations.Key.Settings
                "loading" -> Translations.Key.Loading
                "error" -> Translations.Key.Error
                "success" -> Translations.Key.Success
                else -> return key // Return the key itself if not found
            }
            
            Translations.get(translationKey)
        } catch (e: Exception) {
            key // Return the key itself if translation fails
        }
    }
}

/**
 * Compose Local for Translation Service
 */
val LocalTranslator = compositionLocalOf<TranslationService> {
    error("No TranslationService provided")
}

/**
 * Translation Provider Composable
 */
@Composable
fun TranslationProvider(
    language: String = "en",
    content: @Composable () -> Unit
) {
    val translationService = remember { TranslationService.getInstance() }
    
    // Update language when it changes
    LaunchedEffect(language) {
        translationService.setLanguage(language)
    }
    
    CompositionLocalProvider(
        LocalTranslator provides translationService,
        content = content
    )
}

/**
 * Compose Local for Translation Service
 */
val LocalTranslator = compositionLocalOf<TranslationService> {
    error("No TranslationService provided")
}

/**
 * Translation Provider Composable
 */
@Composable
fun TranslationProvider(
    language: String = "en",
    content: @Composable () -> Unit
) {
    val translationService = remember { TranslationService.getInstance() }
    
    // Update language when it changes
    LaunchedEffect(language) {
        translationService.setLanguage(language)
    }
    
    CompositionLocalProvider(
        LocalTranslator provides translationService,
        content = content
    )
}

/**
 * Extension function for easy translation access
 * 
 * @param key Translation key
 * @param args Optional formatting arguments
 * @return Translated string
 */
@Composable
fun String.translate(vararg args: Any): String {
    val translator = LocalTranslator.current
    return translator.get(this, *args)
}