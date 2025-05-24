package com.example.financialplannerapp.ui.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasscodeViewModel @Inject constructor() : ViewModel() {
    
    private val _isPasscodeValid = MutableStateFlow<Boolean?>(null)
    val isPasscodeValid: StateFlow<Boolean?> = _isPasscodeValid
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    fun validatePasscode(passcode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Implement actual passcode validation logic
                // For now, just simulate validation
                _isPasscodeValid.value = passcode.length == 6
            } catch (e: Exception) {
                _isPasscodeValid.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetValidation() {
        _isPasscodeValid.value = null
    }
}