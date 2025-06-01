package com.example.financialplannerapp.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileUpdateRequest(
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val currencyPreference: String? = "USD",
    val notificationEnabled: Boolean = true,
    val firebaseUid: String? = null
)