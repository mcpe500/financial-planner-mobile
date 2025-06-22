package com.example.financialplannerapp.data.remote

import com.example.financialplannerapp.data.model.HealthResponse
import com.example.financialplannerapp.data.model.LoginRequest
import com.example.financialplannerapp.data.model.LoginResponse
import com.example.financialplannerapp.data.model.RegisterRequest
import com.example.financialplannerapp.data.model.RegisterResponse
import com.example.financialplannerapp.data.model.StoreTransactionResponse
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
    @GET("api/transactions")
    suspend fun getUserTransactions(): Response<com.example.financialplannerapp.data.responses.ApiResponse<List<com.example.financialplannerapp.data.model.TransactionData>>>

    @GET("api/transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Response<com.example.financialplannerapp.data.responses.ApiResponse<com.example.financialplannerapp.data.model.TransactionData>>
    
    @POST("api/transactions/upload")
    suspend fun uploadTransactions(@Body transactions: List<com.example.financialplannerapp.data.model.TransactionData>): Response<Unit>
    
    @POST("api/transactions")
    suspend fun createTransaction(@Body transaction: com.example.financialplannerapp.data.model.TransactionData): Response<com.example.financialplannerapp.data.responses.ApiResponse<com.example.financialplannerapp.data.model.TransactionData>>
    
    // Receipt OCR endpoints
    @POST("api/receipts/receipt-ocr")
    suspend fun processReceiptOCR(
        @Header("Authorization") authHeader: String,
        @Body request: ReceiptOCRRequest
    ): Response<ReceiptOCRResponse>
    
    // Store transaction from OCR
    @POST("api/transactions/store")
    suspend fun storeTransactionFromOCR(
        @Body request: Map<String, Any?>
    ): Response<StoreTransactionResponse>

    // Health check endpoint
    @GET("api/health")
    suspend fun healthCheck(): Response<HealthResponse>
}