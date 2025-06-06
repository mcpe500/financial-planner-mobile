package com.example.financialplannerapp.data.model

import kotlinx.serialization.Serializable

/**
 * Register Request Model
 * 
 * Data class for user registration requests containing name, email, and password.
 */
@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
