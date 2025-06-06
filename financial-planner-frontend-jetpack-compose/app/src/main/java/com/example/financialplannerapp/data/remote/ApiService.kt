package com.example.financialplannerapp.data.remote

import com.example.financialplannerapp.data.requests.UserProfileUpdateRequest
import com.example.financialplannerapp.data.responses.ApiResponse
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @PUT("api/profile/update")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profileData: UserProfileUpdateRequest
    ): Response<ApiResponse<UserData>>
    
    @GET("api/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserData>>
    
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<RegisterResponse>
    
    @GET("api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") authHeader: String): Response<UserResponse>

    @GET("api/auth/me_legacy")
    suspend fun getCurrentUserLegacy(
        @Header("Authorization") token: String
    ): Response<LoginResponse>
    
    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") authHeader: String): Response<LogoutResponse>
}

// Data classes for authentication
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserData? = null
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserData? = null
)

@Serializable
data class UserData(
    val id: String,
    val name: String,
    val email: String
)

// Data classes from AuthService.kt
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