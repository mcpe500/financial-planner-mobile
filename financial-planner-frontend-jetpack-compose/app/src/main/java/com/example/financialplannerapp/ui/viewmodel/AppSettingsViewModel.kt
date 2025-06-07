package com.example.financialplannerapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSettingsViewModel(private val context: Context) : ViewModel() {

    // Theme setting
    private val _theme = MutableStateFlow("system")
    val theme: StateFlow<String> = _theme.asStateFlow()

    // Language setting
    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    // Currency setting
    private val _currency = MutableStateFlow("USD")
    val currency: StateFlow<String> = _currency.asStateFlow()

    // Notifications setting
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val sharedPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        _theme.value = sharedPrefs.getString("theme", "system") ?: "system"
        _language.value = sharedPrefs.getString("language", "en") ?: "en"
        _currency.value = sharedPrefs.getString("currency", "USD") ?: "USD"
        _notificationsEnabled.value = sharedPrefs.getBoolean("notifications_enabled", true)
    }

    fun setTheme(theme: String) {
        _theme.value = theme
        saveToPreferences("theme", theme)
    }

    fun setLanguage(language: String) {
        _language.value = language
        saveToPreferences("language", language)
    }

    fun setCurrency(currency: String) {
        _currency.value = currency
        saveToPreferences("currency", currency)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        saveToPreferences("notifications_enabled", enabled)
    }

    private fun saveToPreferences(key: String, value: Any) {
        val sharedPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
            }
            apply()
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppSettingsViewModel::class.java)) {
            return AppSettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}