package com.example.financialplannerapp.data.model

import kotlinx.serialization.Serializable

/**
 * Register Response Model
 * 
 * Data class for registration API responses containing success status, message, and optional token.
 */
@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserData? = null
)
