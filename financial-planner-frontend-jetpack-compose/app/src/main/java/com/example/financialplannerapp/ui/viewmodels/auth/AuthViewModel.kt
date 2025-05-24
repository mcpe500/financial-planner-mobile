package com.example.financialplannerapp.ui.viewmodels.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.repository.AuthRepository
import com.example.financialplannerapp.models.api.AuthResponse
import com.example.financialplannerapp.utils.AppDatabase
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthState {
    IDLE,
    LOADING,
    AUTHENTICATED,
    UNAUTHENTICATED,
    GUEST,
    ERROR,
    NO_ACCOUNT_MODE
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val authRepository = AuthRepository(tokenManager) // Add ApiService if needed directly

    private val _authState = MutableStateFlow(AuthState.IDLE)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkUserLoggedIn()
    }

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.LOADING
            if (authRepository.isNoAccountMode()) {
                _authState.value = AuthState.NO_ACCOUNT_MODE
                _isLoading.value = false
                Log.d("AuthViewModel", "User is in No Account Mode.")
                return@launch
            }

            val token = tokenManager.getToken()
            if (token == null) {
                _authState.value = AuthState.UNAUTHENTICATED
                _isLoading.value = false
                Log.d("AuthViewModel", "No token found, user is unauthenticated.")
                return@launch
            }

            try {
                val response = authRepository.verifyToken()
                if (response != null && response.isSuccessful && response.body() != null) {
                    authRepository.saveTokenAndUserInfo(response.body()) // Save/update user info
                    _authState.value = AuthState.AUTHENTICATED
                    Log.d("AuthViewModel", "Token verified, user is authenticated.")
                } else {
                    tokenManager.clear() // Clear invalid token
                    _authState.value = AuthState.UNAUTHENTICATED
                    _error.value = "Session expired. Please login again."
                    Log.d("AuthViewModel", "Token verification failed or token invalid.")
                }
            } catch (e: Exception) {
                tokenManager.clear() // Clear token on error
                _authState.value = AuthState.UNAUTHENTICATED
                _error.value = "Error verifying session: ${e.message}"
                Log.e("AuthViewModel", "Exception during token verification: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginSuccess(authResponse: AuthResponse) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.saveTokenAndUserInfo(authResponse)
            authRepository.setNoAccountMode(false) // Ensure no-account mode is off
            _authState.value = AuthState.AUTHENTICATED
            _isLoading.value = false
            Log.d("AuthViewModel", "Login successful.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                tokenManager.clearTokenAndUserInfo()
                _authState.value = AuthState.UNAUTHENTICATED
            } catch (e: Exception) {
                _error.value = "Logout failed: ${e.message}"
                _authState.value = AuthState.ERROR
            }
        }
    }
    
    fun enterNoAccountMode() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.setNoAccountMode(true)
            // Optionally clear specific local data if needed when entering no-account mode
            // For now, AuthRepository handles clearing token/user info
            _authState.value = AuthState.NO_ACCOUNT_MODE
            _isLoading.value = false
            Log.d("AuthViewModel", "Entered No Account Mode.")
        }
    }

    fun exitNoAccountModeAndGoToLogin() {
        viewModelScope.launch {
            tokenManager.setNoAccountMode(false)
            _authState.value = AuthState.UNAUTHENTICATED
        }
    }

    fun clearError() {
        _error.value = null
    }
}