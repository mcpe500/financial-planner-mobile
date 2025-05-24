package com.example.financialplannerapp.ui.viewmodels.auth

enum class AuthState {
    IDLE,
    LOADING,
    AUTHENTICATED,
    UNAUTHENTICATED,
    GUEST,
    ERROR,
    NO_ACCOUNT_MODE
}