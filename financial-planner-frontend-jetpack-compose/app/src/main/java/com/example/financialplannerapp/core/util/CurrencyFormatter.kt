package com.example.financialplannerapp.core.util

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialplannerapp.ui.viewmodel.AppSettingsViewModel
import com.example.financialplannerapp.ui.viewmodel.SettingsViewModelFactory
import java.text.NumberFormat
import java.util.*

@Composable
fun formatCurrency(amount: Double): String {
    val context = LocalContext.current
    val appSettingsViewModel: AppSettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val currentCurrency by appSettingsViewModel.currency.collectAsState()
    val currentLanguage by appSettingsViewModel.language.collectAsState()
    
    return formatCurrencyWithCode(amount, currentCurrency, currentLanguage)
}

@Composable
fun getCurrentCurrencySymbol(): String {
    val context = LocalContext.current
    val appSettingsViewModel: AppSettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val currentCurrency by appSettingsViewModel.currency.collectAsState()
    
    return getCurrencySymbol(currentCurrency)
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
