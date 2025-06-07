package com.example.financialplannerapp.data.model

import kotlinx.serialization.Serializable

/**
 * Login Response Model
 * 
 * Data class for login API responses containing success status, message, token, and user data.
 */
@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserData? = null
)

/**
 * User Data Model
 * 
 * Basic user information returned in authentication responses.
 */
@Serializable
data class UserData(
    val id: String,
    val name: String,
    val email: String
)
