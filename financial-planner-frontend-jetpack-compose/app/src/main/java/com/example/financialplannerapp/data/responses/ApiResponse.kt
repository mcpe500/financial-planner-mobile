package com.example.financialplannerapp.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errorCode: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ErrorResponse(
    val error: String,
    val details: String? = null,
    val code: Int? = null
)