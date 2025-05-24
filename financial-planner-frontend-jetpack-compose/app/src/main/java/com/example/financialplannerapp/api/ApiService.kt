package com.example.financialplannerapp.api

import com.example.financialplannerapp.models.api.AuthResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("api/auth/google/callback") // This might be handled by WebView/Custom Tabs, not direct API call
    suspend fun googleLoginCallback(): Response<AuthResponse> // Placeholder

    @GET("api/auth/verify")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit> // Or some simple response

    // Add other API endpoints here
}