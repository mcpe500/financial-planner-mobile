package com.example.financialplannerapp.data.remote

import com.example.financialplannerapp.data.model.LoginRequest
import com.example.financialplannerapp.data.model.LoginResponse
import com.example.financialplannerapp.data.model.RegisterRequest
import com.example.financialplannerapp.data.model.RegisterResponse
import com.example.financialplannerapp.data.model.UserData
import com.example.financialplannerapp.data.model.ReceiptOCRRequest
import com.example.financialplannerapp.data.model.ReceiptOCRResponse
import com.example.financialplannerapp.data.requests.UserProfileUpdateRequest
import com.example.financialplannerapp.data.responses.ApiResponse
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
    suspend fun logout(@Header("Authorization") authHeader: String): Response<com.example.financialplannerapp.data.remote.LogoutResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Header("Authorization") authHeader: String): Response<LoginResponse>

    // Category endpoints
    @GET("api/categories")
    suspend fun getCategories(): Response<List<com.example.financialplannerapp.data.model.CategoryData>>
    
    @POST("api/categories/upload")
    suspend fun uploadCategories(@Body categories: List<com.example.financialplannerapp.data.model.CategoryData>): Response<Unit>
    
    // Transaction endpoints
    @GET("api/transactions/{userId}")
    suspend fun getUserTransactions(@Path("userId") userId: String): Response<List<com.example.financialplannerapp.data.model.TransactionData>>
    
    @POST("api/transactions/upload")
    suspend fun uploadTransactions(@Body transactions: List<com.example.financialplannerapp.data.model.TransactionData>): Response<Unit>
    
    // Receipt OCR endpoints
    @POST("api/receipts/process")
    suspend fun processReceiptOCR(
        @Header("Authorization") authHeader: String,
        @Body request: ReceiptOCRRequest
    ): Response<ReceiptOCRResponse>
}