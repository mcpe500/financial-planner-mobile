package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    fun updateSettings(newSettings: Map<String, Any>) {
        viewModelScope.launch {
            // Logic to update settings
        }
    }
}