package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserPreferencesViewModel : ViewModel() {

    private val _userPreferences = MutableStateFlow<UserPreferences?>(null)
    val userPreferences: StateFlow<UserPreferences?> = _userPreferences

    fun updateUserPreferences(preferences: UserPreferences) {
        viewModelScope.launch {
            _userPreferences.value = preferences
        }
    }
}

data class UserPreferences(
    val theme: String,
    val notificationsEnabled: Boolean
)