package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password)
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(email, password)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}