package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.model.UserProfileData
import com.example.financialplannerapp.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {    private val _userProfile = MutableStateFlow<UserProfileData?>(null)
    val userProfile: StateFlow<UserProfileData?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()    // userId can be passed if multiple profiles are managed, or a default/current user ID is fetched elsewhere
    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userProfileRepository.getUserProfileFlow(userId).collect { profile ->
                    _userProfile.value = profile
                }
            } catch (e: Exception) {
                _error.value = "Failed to load user profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }    fun saveUserProfile(profile: UserProfileData) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (profile.userId.isEmpty()) { // New profile
                    userProfileRepository.insertUserProfile(profile.copy(isDataModified = true))
                } else { // Existing profile
                    userProfileRepository.updateUserProfile(profile.copy(isDataModified = true))
                }
                // Optionally, reload the profile to reflect changes
                loadUserProfile(profile.userId) 
            } catch (e: Exception) {
                _error.value = "Failed to save user profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
