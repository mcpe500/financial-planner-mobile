package com.example.financialplannerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    // TODO: Implement register logic, state management for name, email, password, confirmPassword,
    // loading state, error messages, navigation events.

    fun onRegisterClicked(name: String, email: String, password: String, confirmPassword: String) {
        // TODO: Validate inputs (e.g., password match)
        // TODO: Call repository/usecase for registration
        println("Register Clicked: Name - $name, Email - $email") // Placeholder
    }

    fun onNavigateToLoginClicked() {
        // TODO: Emit navigation event
        println("Navigate to Login Clicked from Register") // Placeholder
    }
}