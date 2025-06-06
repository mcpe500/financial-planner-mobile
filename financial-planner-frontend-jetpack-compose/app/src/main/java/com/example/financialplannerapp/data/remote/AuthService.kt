package com.example.financialplannerapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @GET("api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") authHeader: String): Response<UserResponse>
    
    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") authHeader: String): Response<LogoutResponse>
}

data class UserResponse(
    val user: User
)

data class User(
    val id: String,
    val email: String?,
    val name: String?,
    val role: String? = null
)

data class LogoutResponse(
    val message: String
)