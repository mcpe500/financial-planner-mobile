package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.model.LoginRequest
import com.example.financialplannerapp.data.model.LoginResponse
import com.example.financialplannerapp.data.model.RegisterRequest
import com.example.financialplannerapp.data.model.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>
    suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(): Result<String>
    suspend fun isLoggedIn(): Boolean
    fun getAuthToken(): Flow<String?>
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
}
