package com.example.financialplannerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    // TODO: Implement login logic, state management (e.g., using StateFlow)
    // for email, password, loading state, error messages, navigation events.

    fun onLoginClicked(email: String, password: String) {
        // TODO: Validate inputs
        // TODO: Call repository/usecase for login
        println("Login Clicked: Email - $email, Password - $password") // Placeholder
    }

    fun onNavigateToRegisterClicked() {
        // TODO: Emit navigation event
        println("Navigate to Register Clicked") // Placeholder
    }

    fun onNavigateToForgotPasswordClicked() {
        // TODO: Emit navigation event
        println("Navigate to Forgot Password Clicked") // Placeholder
    }
}