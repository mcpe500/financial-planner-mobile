package com.example.financialplannerapp.ui.viewmodels.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.models.api.UserData
import com.example.financialplannerapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserData?>()
    val userProfile: LiveData<UserData?> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _logoutState = MutableLiveData<Boolean>()
    val logoutState: LiveData<Boolean> = _logoutState

    fun isNoAccountMode(): Boolean {
        return tokenManager.isNoAccountMode()
    }

    fun loadUserProfile() {
        if (isNoAccountMode()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Load actual user profile from repository
                // For now, set a placeholder
                _userProfile.value = UserData(
                    id = "1",
                    name = "User Name",
                    email = "user@example.com",
                    profileImageUrl = null
                )
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                tokenManager.clearTokenAndUserInfo()
                _logoutState.value = true
            } catch (e: Exception) {
                _error.value = "Failed to logout: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetLogoutState() {
        _logoutState.value = false
    }
}