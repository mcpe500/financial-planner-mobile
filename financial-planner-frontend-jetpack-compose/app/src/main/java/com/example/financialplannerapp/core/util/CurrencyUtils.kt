package com.example.financialplannerapp.core.util

import java.text.NumberFormat
import java.util.*

/**
 * Currency Utilities
 * 
 * Provides currency formatting and conversion utilities for the Financial Planner app.
 * Supports multiple currencies with proper localization.
 */
object CurrencyUtils {
    
    /**
     * Get currency symbol for currency code
     */
    fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode) {
            "IDR" -> "Rp"
            "USD" -> "$"
            "EUR" -> "€"
            "JPY" -> "¥"
            "CNY" -> "¥"
            else -> currencyCode
        }
    }
    
    /**
     * Format amount with currency
     */
    fun formatCurrency(amount: Double, currencyCode: String, locale: Locale = Locale.getDefault()): String {
        return try {
            val currency = Currency.getInstance(currencyCode)
            val formatter = NumberFormat.getCurrencyInstance(locale)
            formatter.currency = currency
            formatter.format(amount)
        } catch (e: Exception) {
            "${getCurrencySymbol(currencyCode)} ${NumberFormat.getNumberInstance(locale).format(amount)}"
        }
    }
    
    /**
     * Format amount with custom formatting for Indonesian Rupiah
     */
    fun formatIDR(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp ${formatter.format(amount)}"
    }
    
    /**
     * Get supported currencies with their display names
     */
    fun getSupportedCurrencies(): Map<String, String> {
        return mapOf(
            "IDR" to "Indonesian Rupiah (Rp)",
            "USD" to "US Dollar ($)",
            "EUR" to "Euro (€)",
            "JPY" to "Japanese Yen (¥)",
            "CNY" to "Chinese Yuan (¥)"
        )
    }
    
    /**
     * Parse currency string to double
     */
    fun parseCurrency(currencyString: String): Double? {
        return try {
            val cleanString = currencyString.replace(Regex("[^\\d.,]"), "")
                .replace(",", ".")
            cleanString.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }
}