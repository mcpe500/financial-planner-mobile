// <!-- filepath: app/src/main/java/com/example/financialplannerapp/ui/viewmodels/auth/LoginViewModel.kt -->
package com.example.financialplannerapp.ui.viewmodels.auth

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.api.RetrofitClient
import com.example.financialplannerapp.data.repository.AuthRepository
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val TAG = "LoginViewModel"

    fun verifyTokenAndNavigate() {
        if (tokenManager.getToken() != null) {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    val response = authRepository.verifyToken()
                    if (response != null && response.isSuccessful) {
                        Log.d(TAG, "Token verified successfully")
                        authRepository.saveUserInfoFromResponse(response.body())
                        _loginSuccess.value = true
                    } else {
                        Log.e(TAG, "Token verification failed: ${response?.code()}")
                        tokenManager.clearToken() // Clear invalid token
                        if (response?.code() == 401) {
                            _error.value = "Your session has expired. Please login again."
                        }
                        _loginSuccess.value = false // Explicitly set to false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error verifying token: ${e.message}")
                    _error.value = "Could not verify login status. Please try again."
                    _loginSuccess.value = false // Explicitly set to false
                } finally {
                    _isLoading.value = false
                }
            }
        } else if (tokenManager.isNoAccountMode()) {
             Log.d(TAG, "No-account mode enabled, navigating to dashboard")
            _loginSuccess.value = true
        }
    }

    fun signInWithGoogle() {
        val googleAuthUrl = "${RetrofitClient.retrofit.baseUrl()}api/auth/google"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleAuthUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            _error.value = "Could not open browser for Google Sign-In"
            Log.e(TAG, "Error starting Google Sign-In intent: ${e.message}")
        }
    }

    fun continueWithoutAccount() {
        authRepository.setNoAccountMode(true)
        _loginSuccess.value = true
    }
    
    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }


    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val tokenManager: TokenManager,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                val authRepository = AuthRepository(tokenManager)
                return LoginViewModel(tokenManager, authRepository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}