package com.example.financialplannerapp.models

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthService {
    @GET("api/auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<AuthResponse>
}