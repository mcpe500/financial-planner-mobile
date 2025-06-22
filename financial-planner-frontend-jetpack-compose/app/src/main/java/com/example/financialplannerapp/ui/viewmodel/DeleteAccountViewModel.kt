package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.remote.AccountService
import com.example.financialplannerapp.data.remote.DeleteAccountRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DeleteAccountState {
    object Idle : DeleteAccountState()
    object Loading : DeleteAccountState()
    object OtpSent : DeleteAccountState()
    object OtpVerified : DeleteAccountState()
    object AccountDeleted : DeleteAccountState()
    data class Error(val message: String) : DeleteAccountState()
}

class DeleteAccountViewModel(
    private val accountService: AccountService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<DeleteAccountState>(DeleteAccountState.Idle)
    val state: StateFlow<DeleteAccountState> = _state.asStateFlow()

    private var deletionToken: String? = null
    private var verificationToken: String? = null

    fun requestAccountDeletion() {
        viewModelScope.launch {
            try {
                _state.value = DeleteAccountState.Loading
                val response = accountService.requestAccountDeletion()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    deletionToken = response.body()?.token
                    _state.value = DeleteAccountState.OtpSent
                } else {
                    _state.value = DeleteAccountState.Error(
                        response.body()?.message ?: "Failed to request account deletion"
                    )
                }
            } catch (e: Exception) {
                _state.value = DeleteAccountState.Error("Network error: ${e.message}")
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            try {
                _state.value = DeleteAccountState.Loading
                val request = DeleteAccountRequest(token = deletionToken, otp = otp)
                val response = accountService.verifyOtp(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    verificationToken = response.body()?.verificationToken
                    _state.value = DeleteAccountState.OtpVerified
                } else {
                    _state.value = DeleteAccountState.Error(
                        response.body()?.message ?: "Invalid OTP"
                    )
                }
            } catch (e: Exception) {
                _state.value = DeleteAccountState.Error("Network error: ${e.message}")
            }
        }
    }

    fun confirmDeletion() {
        viewModelScope.launch {
            try {
                _state.value = DeleteAccountState.Loading
                val userEmail = tokenManager.getUserEmail()
                val request = DeleteAccountRequest(
                    verificationToken = verificationToken,
                    email = userEmail
                )
                val response = accountService.confirmDeletion(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    // Clear all local data
                    tokenManager.clearTokens()
                    _state.value = DeleteAccountState.AccountDeleted
                } else {
                    _state.value = DeleteAccountState.Error(
                        response.body()?.message ?: "Failed to delete account"
                    )
                }
            } catch (e: Exception) {
                _state.value = DeleteAccountState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = DeleteAccountState.Idle
        deletionToken = null
        verificationToken = null
    }
}