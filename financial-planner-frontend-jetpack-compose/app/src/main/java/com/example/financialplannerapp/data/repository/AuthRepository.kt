package com.example.financialplannerapp.data.repository

import android.util.Log
import com.example.financialplannerapp.api.ApiService
import com.example.financialplannerapp.api.RetrofitClient
import com.example.financialplannerapp.models.api.AuthResponse
import com.example.financialplannerapp.models.api.UserData
import com.example.financialplannerapp.utils.TokenManager
import retrofit2.Response

class AuthRepository(
    private val tokenManager: TokenManager,
    private val apiService: ApiService = RetrofitClient.apiService
) {

    private val TAG = "AuthRepository"

    suspend fun verifyToken(): Response<AuthResponse>? {
        val token = tokenManager.getAuthHeader()
        return if (token != null) {
            try {
                apiService.verifyToken(token)
            } catch (e: Exception) {
                Log.e(TAG, "Error verifying token: ${e.message}")
                null
            }
        } else {
            Log.d(TAG, "No token found for verification.")
            null
        }
    }

    suspend fun logout(): Boolean {
        val token = tokenManager.getAuthHeader()
        return if (token != null) {
            try {
                val response = apiService.logout(token)
                if (response.isSuccessful) {
                    tokenManager.clear()
                    true
                } else {
                    Log.e(TAG, "Logout failed: ${response.code()} - ${response.message()}")
                    // Optionally clear token even if server logout fails,
                    // depending on desired behavior for robustness.
                    tokenManager.clear() // Clear local session anyway
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout: ${e.message}")
                tokenManager.clear() // Clear local session on error
                false
            }
        } else {
            Log.d(TAG, "No token found, already logged out or no-account mode.")
            tokenManager.clear() // Ensure clean state
            true // Consider this a success as the user is effectively logged out locally
        }
    }


    fun saveTokenAndUserInfo(authResponse: AuthResponse?) {
        authResponse?.token?.let { tokenManager.saveToken(it) }
        authResponse?.user?.let { saveUserInfoFromResponse(it) }
    }

    fun saveUserInfoFromResponse(userData: UserData?) {
        userData?.let {
            tokenManager.saveUserInfo(it.id, it.email, it.name)
            // Here you would also save/update UserProfile in Room database
            // For now, just saving to SharedPreferences via TokenManager
        }
    }
    
    fun setNoAccountMode(enabled: Boolean) {
        tokenManager.setNoAccountMode(enabled)
        if (enabled) {
            // Clear any existing user data if switching to no-account mode
            tokenManager.clearToken()
            tokenManager.clearUserInfo()
        }
    }

    fun isNoAccountMode(): Boolean {
        return tokenManager.isNoAccountMode()
    }

    fun getUserId(): String? {
        return tokenManager.getUserId()
    }
}