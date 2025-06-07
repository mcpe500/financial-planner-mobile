package com.example.financialplannerapp.data.model

import kotlinx.serialization.Serializable

/**
 * Login Request Model
 * 
 * Data class for user login requests containing email and password.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
