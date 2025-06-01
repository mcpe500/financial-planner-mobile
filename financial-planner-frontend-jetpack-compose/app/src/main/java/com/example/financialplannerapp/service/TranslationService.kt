package com.example.financialplannerapp.service

import androidx.compose.runtime.*
import com.example.financialplannerapp.utils.Translations
import com.example.financialplannerapp.utils.TranslationState

/**
 * Translation Service for managing app translations
 * 
 * Provides a centralized service for handling language changes and translations.
 * Works with the TranslationState and Translations utilities.
 */
class TranslationService private constructor() {
    
    private val translationState = TranslationState.getInstance()
    
    companion object {
        @Volatile
        private var INSTANCE: TranslationService? = null
        
        fun getInstance(): TranslationService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TranslationService().also { INSTANCE = it }
            }
        }
    }
      /**
     * Set the current language
     */
    fun setLanguage(languageCode: String) {
        translationState.setLanguage(languageCode)
    }
    
    /**
     * Get the current language code (non-Composable version)
     */
    fun getCurrentLanguageCode(): String {
        return translationState.currentLanguage.value
    }
    
    /**
     * Get the current language code (Composable version)
     */
    @Composable
    fun getCurrentLanguageCodeComposable(): String {
        return translationState.currentLanguage.value
    }
    
    /**
     * Get translation for a key with current language
     */
    fun get(key: Translations.Key): String {
        return Translations.get(key, getCurrentLanguageCode())
    }
    
    /**
     * Get translation for a string key with current language
     */
    fun get(key: String): String {
        return Translations.get(key, getCurrentLanguageCode())
    }
    
    /**
     * Get translation with specific language
     */
    fun get(key: Translations.Key, language: String): String {
        return Translations.get(key, language)
    }
    
    /**
     * Get translation with specific language (string key)
     */
    fun get(key: String, language: String): String {
        return Translations.get(key, language)
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
 * Extension function for easy translation access
 * 
 * @param key Translation key
 * @param args Optional formatting arguments
 * @return Translated string
 */
@Composable
fun String.translate(vararg args: Any): String {
    val translator = LocalTranslator.current
    return translator.get(this)
}