package com.example.financialplannerapp.core.util

import androidx.compose.runtime.*
import com.example.financialplannerapp.service.LocalReactiveSettingsService
import androidx.compose.runtime.collectAsState // Corrected import
import java.util.Locale

/**
 * Currency Formatter
 * 
 * Provides reactive currency formatting based on app settings.
 * This ensures currency formatting updates throughout the app when settings change.
 */

/**
 * Composable function to format currency based on current app settings
 */
@Composable
fun formatCurrency(amount: Double): String {
    val settingsService = LocalReactiveSettingsService.current
    val currentSettings by settingsService.currentSettings.collectAsState()
    
    return formatCurrencyWithCode(amount, currentSettings.currency, currentSettings.language)
}

/**
 * Composable function to get current currency symbol
 */
@Composable
fun getCurrentCurrencySymbol(): String {
    val settingsService = LocalReactiveSettingsService.current
    val currentSettings by settingsService.currentSettings.collectAsState()
    
    return CurrencyUtils.getCurrencySymbol(currentSettings.currency)
}

/**
 * Non-composable function to format currency with specific currency code and language
 */
fun formatCurrencyWithCode(amount: Double, currencyCode: String, languageCode: String): String {
    return try {
        val locale = when (languageCode) {
            "id" -> Locale("id", "ID")
            "en" -> Locale.US
            "es" -> Locale("es", "ES")
            else -> Locale.getDefault()
        }
        
        CurrencyUtils.formatCurrency(amount, currencyCode, locale)
    } catch (e: Exception) {
        // Fallback to default formatting
        CurrencyUtils.formatCurrency(amount, "IDR")
    }
}

/**
 * Extension function for Double to make currency formatting easier
 */
@Composable
fun Double.toCurrency(): String = formatCurrency(this)

/**
 * Extension function for Float to make currency formatting easier
 */
@Composable
fun Float.toCurrency(): String = formatCurrency(this.toDouble())

/**
 * Extension function for Int to make currency formatting easier
 */
@Composable
fun Int.toCurrency(): String = formatCurrency(this.toDouble())
