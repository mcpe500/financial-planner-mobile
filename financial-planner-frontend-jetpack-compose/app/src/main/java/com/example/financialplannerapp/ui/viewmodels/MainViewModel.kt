// <!-- filepath: app/src/main/java/com/example/financialplannerapp/ui/viewmodels/MainViewModel.kt -->
package com.example.financialplannerapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.repository.AuthRepository
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository // Add if needed for token verification here
) : ViewModel() {

    enum class DeepLinkAuthState { IDLE, AUTHENTICATED, FAILED }

    private val _deepLinkAuthState = MutableStateFlow(DeepLinkAuthState.IDLE)
    val deepLinkAuthState: StateFlow<DeepLinkAuthState> = _deepLinkAuthState.asStateFlow()

    fun handleDeepLinkToken(token: String) {
        viewModelScope.launch {
            tokenManager.saveToken(token)
            // Optionally verify token here if needed immediately, or let LoginScreen handle it
            // For simplicity, assume token is good and LoginScreen will verify
            val response = authRepository.verifyToken()
            if (response != null && response.isSuccessful) {
                authRepository.saveUserInfoFromResponse(response.body())
                tokenManager.setNoAccountMode(false)
                _deepLinkAuthState.value = DeepLinkAuthState.AUTHENTICATED
            } else {
                tokenManager.clearToken()
                _deepLinkAuthState.value = DeepLinkAuthState.FAILED
            }
        }
    }

    fun resetDeepLinkState() {
        _deepLinkAuthState.value = DeepLinkAuthState.IDLE
    }

    fun logout() {
        authRepository.clearTokenAndUserInfo()
        // Any other logout related tasks
    }


    // Factory for creating MainViewModel
    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val tokenManager: TokenManager,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                // Initialize repositories or other dependencies here if needed by MainViewModel
                val authRepository = AuthRepository(tokenManager)
                return MainViewModel(tokenManager, authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}