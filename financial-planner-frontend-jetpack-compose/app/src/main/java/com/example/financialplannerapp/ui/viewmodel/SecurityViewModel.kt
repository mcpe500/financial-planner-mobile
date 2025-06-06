package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.local.model.SecurityEntity
import com.example.financialplannerapp.data.repository.SecurityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SecurityViewModel constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _securitySettings = MutableStateFlow<SecurityEntity?>(null)
    val securitySettings: StateFlow<SecurityEntity?> = _securitySettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadSecuritySettings()
    }

    fun loadSecuritySettings(userId: String = "1") { // Assuming a default or single user context for app-wide settings
        viewModelScope.launch {
            _isLoading.value = true
            try {
                securityRepository.getSecuritySettings().collect {
                    _securitySettings.value = it ?: SecuritySettings() // Provide a default if null
                }
            } catch (e: Exception) {
                _error.value = "Failed to load security settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSecuritySettings(settings: SecurityEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                securityRepository.insertOrUpdateSecuritySettings(settings)
                // Refresh the settings flow after update
                securityRepository.getSecuritySettings().collect {
                     _securitySettings.value = it
                }
            } catch (e: Exception) {
                _error.value = "Failed to update security settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updatePinHash(pinHash: String?, currentSettings: SecurityEntity) {
        val newSettings = currentSettings.copy(pinHash = pinHash)
        updateSecuritySettings(newSettings)
    }

    fun clearError() {
        _error.value = null
    }
}
