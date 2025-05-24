package com.example.financialplannerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor() : ViewModel() {

    // TODO: Implement logic for sending reset link, state management for email,
    // loading state, success/error messages, navigation events.

    fun onSendResetLinkClicked(email: String) {
        // TODO: Validate email
        // TODO: Call repository/usecase for sending reset link
        println("Send Reset Link Clicked: Email - $email") // Placeholder
    }

    fun onNavigateBackToLoginClicked() {
        // TODO: Emit navigation event
        println("Navigate to Login Clicked from Forgot Password") // Placeholder
    }
}