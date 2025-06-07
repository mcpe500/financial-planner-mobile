package com.example.financialplannerapp.core.util

import androidx.compose.runtime.*
import com.example.financialplannerapp.service.LocalReactiveSettingsService
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat
import java.util.*

@Composable
fun formatCurrency(amount: Double): String {
    val settingsService = LocalReactiveSettingsService.current
    val currentSettings by settingsService.currentSettings.collectAsState()
    
    return formatCurrencyWithCode(amount, currentSettings.currency, currentSettings.language)
}

@Composable
fun getCurrentCurrencySymbol(): String {
    val settingsService = LocalReactiveSettingsService.current
    val currentSettings by settingsService.currentSettings.collectAsState()
    
    return getCurrencySymbol(currentSettings.currency)
}

fun formatCurrencyWithCode(amount: Double, currencyCode: String, languageCode: String): String {
    val locale = when (languageCode) {
        "id" -> Locale("id", "ID")
        "en" -> Locale.US
        "es" -> Locale("es", "ES")
        "zh" -> Locale.CHINA
        else -> Locale.US
    }
    
    val currency = try {
        Currency.getInstance(currencyCode)
    } catch (e: Exception) {
        Currency.getInstance("USD")
    }
    
    val formatter = NumberFormat.getCurrencyInstance(locale)
    formatter.currency = currency
    
    return formatter.format(amount)
}

fun getCurrencySymbol(currencyCode: String): String {
    return when (currencyCode) {
        "IDR" -> "Rp"
        "USD" -> "$"
        "EUR" -> "€"
        "JPY" -> "¥"
        else -> currencyCode
    }
}

@Composable
fun Double.toCurrency(): String = formatCurrency(this)

@Composable
fun Float.toCurrency(): String = formatCurrency(this.toDouble())

@Composable
fun Int.toCurrency(): String = formatCurrency(this.toDouble())
