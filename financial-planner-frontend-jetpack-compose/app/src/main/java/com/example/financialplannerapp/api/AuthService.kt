package com.example.financialplannerapp.api

import com.example.financialplannerapp.models.api.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthService {
    @GET("api/auth/user")
    suspend fun getCurrentUser(@Header("Authorization") authHeader: String): Response<User>
}