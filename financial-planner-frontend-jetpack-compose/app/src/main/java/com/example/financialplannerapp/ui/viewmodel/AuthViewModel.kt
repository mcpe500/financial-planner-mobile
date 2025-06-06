package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.repository.AuthRepository
import com.example.financialplannerapp.data.model.LoginRequest
import com.example.financialplannerapp.data.model.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(LoginRequest(email, password))
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(RegisterRequest(email, password))
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}