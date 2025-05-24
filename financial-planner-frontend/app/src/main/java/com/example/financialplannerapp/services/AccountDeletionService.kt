package com.example.financialplannerapp.services

import com.example.financialplannerapp.network.ApiClient
import com.example.financialplannerapp.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class AccountDeletionRequest(
    val email: String? = null // For web flow, null for authenticated mobile flow
)

data class OtpVerificationRequest(
    val token: String,
    val otp: String
)

data class DeletionConfirmationRequest(
    val verificationToken: String,
    val email: String
)

data class AccountDeletionResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null // For web flow
)

data class OtpVerificationResponse(
    val success: Boolean,
    val message: String,
    val verificationToken: String? = null
)

data class DeletionConfirmationResponse(
    val success: Boolean,
    val message: String
)

interface AccountDeletionApiService {
    @POST("api/account/request-deletion")
    suspend fun requestAccountDeletion(
        @Header("Authorization") token: String,
        @Body request: AccountDeletionRequest = AccountDeletionRequest()
    ): Response<AccountDeletionResponse>
    
    @POST("api/account/verify-otp")
    suspend fun verifyOtp(
        @Body request: OtpVerificationRequest
    ): Response<OtpVerificationResponse>
    
    @POST("api/account/confirm-deletion")
    suspend fun confirmDeletion(
        @Body request: DeletionConfirmationRequest
    ): Response<DeletionConfirmationResponse>
}

class AccountDeletionService {
    private val apiService = ApiClient.getClient().create(AccountDeletionApiService::class.java)
    
    suspend fun requestAccountDeletion(authToken: String): ApiResponse<AccountDeletionResponse> {
        return try {
            val response = apiService.requestAccountDeletion("Bearer $authToken")
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error("Failed to request account deletion: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResponse.Error("Network error: ${e.message}")
        }
    }
    
    suspend fun verifyOtp(token: String, otp: String): ApiResponse<OtpVerificationResponse> {
        return try {
            val response = apiService.verifyOtp(OtpVerificationRequest(token, otp))
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error("Failed to verify OTP: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResponse.Error("Network error: ${e.message}")
        }
    }
    
    suspend fun confirmDeletion(verificationToken: String, email: String): ApiResponse<DeletionConfirmationResponse> {
        return try {
            val response = apiService.confirmDeletion(DeletionConfirmationRequest(verificationToken, email))
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error("Failed to confirm deletion: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResponse.Error("Network error: ${e.message}")
        }
    }
}