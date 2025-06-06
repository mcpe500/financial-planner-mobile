package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.core.datastore.DataStoreHelper
import com.example.financialplannerapp.data.model.LoginRequest
import com.example.financialplannerapp.data.model.LoginResponse
import com.example.financialplannerapp.data.model.RegisterRequest
import com.example.financialplannerapp.data.model.RegisterResponse
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreHelper: DataStoreHelper
) : AuthRepository {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {        return try {
            val response = apiService.login(loginRequest)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                loginResponse.token?.let { token ->
                    saveAuthToken(token)
                }
                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = apiService.register(registerRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            clearAuthToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }    override suspend fun refreshToken(): Result<String> {
        return try {
            val currentToken = getAuthToken().first()
            if (currentToken != null) {
                val response = apiService.refreshToken("Bearer $currentToken")
                if (response.isSuccessful && response.body() != null) {
                    val newToken = response.body()!!.token
                    if (newToken != null) {
                        saveAuthToken(newToken)
                        Result.success(newToken)
                    } else {
                        Result.failure(Exception("Token refresh failed: No token in response"))
                    }
                } else {
                    Result.failure(Exception("Token refresh failed"))
                }
            } else {
                Result.failure(Exception("No token to refresh"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            getAuthToken().first() != null
        } catch (e: Exception) {
            false
        }
    }

    override fun getAuthToken(): Flow<String?> {
        return dataStoreHelper.getAuthToken()
    }

    override suspend fun saveAuthToken(token: String) {
        dataStoreHelper.saveAuthToken(token)
    }

    override suspend fun clearAuthToken() {
        dataStoreHelper.clearAuthToken()
    }
}
