package com.example.financialplannerapp.data.remote

import com.example.financialplannerapp.data.responses.ApiResponse
import retrofit2.Response
import retrofit2.http.*

data class DeleteAccountRequest(
    val token: String? = null,
    val otp: String? = null,
    val verificationToken: String? = null,
    val email: String? = null
)

data class DeleteAccountResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val verificationToken: String? = null
)

interface AccountService {
    @POST("api/v1/account/delete/request")
    suspend fun requestAccountDeletion(): Response<DeleteAccountResponse>
    
    @POST("api/v1/account/delete/verify")
    suspend fun verifyOtp(@Body request: DeleteAccountRequest): Response<DeleteAccountResponse>
    
    @POST("api/v1/account/delete/confirm")
    suspend fun confirmDeletion(@Body request: DeleteAccountRequest): Response<DeleteAccountResponse>
}