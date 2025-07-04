package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import com.example.financialplannerapp.data.model.UserData
import com.example.financialplannerapp.data.repository.UserProfileRepository
import com.example.financialplannerapp.data.requests.UserProfileUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// Helper function to map UserData (from remote) to UserProfileEntity (local)
// This should ideally be in a dedicated mapper file or part of the model definitions.
fun UserData.toEntity(firebaseUid: String): UserProfileEntity {
    // Helper to parse date strings like "dd/MM/yyyy" or "yyyy-MM-dd"
    fun parseDateString(dateStr: String?): java.util.Date? {
        if (dateStr.isNullOrBlank()) return null
        val formats = listOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        for (format in formats) {
            try {
                return format.parse(dateStr)
            } catch (e: java.text.ParseException) {
                // Try next format
            }
        }
        return null // Could not parse
    }

    return UserProfileEntity(
        firebaseUid = firebaseUid, // Assuming firebaseUid is the primary key for local lookup
        name = this.name,
        email = this.email,
        phone = this.phone,
        // dateOfBirth needs conversion from String to Date if your entity stores Date
        dateOfBirth = parseDateString(this.dateOfBirth),
        occupation = this.occupation,
        monthlyIncome = this.monthlyIncome?.toDoubleOrNull(), // Assuming entity stores Double
        financialGoals = this.financialGoals,
        // backendUpdatedAt can be a new field in UserProfileEntity if needed for sync logic
        // For now, we assume `isDataModified` handles local changes.
        isDataModified = false // Data from remote is considered synced
    )
}

// Helper function to map UserProfileEntity (local) to UserProfileUpdateRequest (for remote)
fun UserProfileEntity.toUpdateRequest(): UserProfileUpdateRequest {
    // Helper to format Date to "dd/MM/yyyy" string, or backend preferred format
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return UserProfileUpdateRequest(
        name = this.name,
        // email is typically not updatable or handled via separate auth flows
        phone = this.phone,
        dateOfBirth = this.dateOfBirth?.let { dateFormat.format(it) },
        occupation = this.occupation,
        monthlyIncome = this.monthlyIncome?.toString(),
        financialGoals = this.financialGoals
    )
}


class UserProfileViewModel constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    
    private val _userProfile = MutableStateFlow<UserProfileEntity?>(null)
    val userProfile: StateFlow<UserProfileEntity?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun loadUserProfile(firebaseUid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Attempt to fetch from remote
                val remoteResult = userProfileRepository.fetchRemoteUserProfile()
                if (remoteResult.isSuccess) {
                    val remoteUserData = remoteResult.getOrNull()
                    if (remoteUserData != null) {
                        val entity = remoteUserData.toEntity(firebaseUid)
                        // Save/update local cache
                        if (userProfileRepository.getUserProfileByFirebaseUid(firebaseUid).firstOrNull() != null) {
                            userProfileRepository.updateUserProfile(entity.copy(id = userProfileRepository.getUserProfileByFirebaseUid(firebaseUid).firstOrNull()?.id ?: 0L))
                        } else {
                            userProfileRepository.insertUserProfile(entity)
                        }
                        _userProfile.value = entity
                        _successMessage.value = "Profile loaded from server."
                    } else {
                        // Remote fetch succeeded but no data, try local
                        loadProfileFromLocal(firebaseUid, "Profile not found on server, loading local.")
                    }
                } else {
                    // Remote fetch failed, try local
                    val exception = remoteResult.exceptionOrNull()
                    loadProfileFromLocal(firebaseUid, "Failed to load from server (${exception?.message}), loading local.")
                }
            } catch (e: Exception) {
                _error.value = "Failed to load user profile: ${e.message}"
                loadProfileFromLocal(firebaseUid, "Error occurred, loading local profile.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadProfileFromLocal(firebaseUid: String, reason: String) {
        userProfileRepository.getUserProfileByFirebaseUid(firebaseUid).collect { profile: UserProfileEntity? ->
            _userProfile.value = profile
            if (profile == null) {
                _error.value = "$reason Local profile also not found."
            } else {
                 _successMessage.value = reason
            }
        }
    }

    fun saveUserProfile(profileEntity: UserProfileEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            try {
                // 1. Save to local DB first, mark as modified
                val localProfileToSave = profileEntity.copy(isDataModified = true)
                if (localProfileToSave.id == 0L && localProfileToSave.firebaseUid != null) { // Check if it's a new entity not yet in DB
                     val existing = userProfileRepository.getUserProfileByFirebaseUid(localProfileToSave.firebaseUid!!).firstOrNull()
                     if (existing != null) {
                         userProfileRepository.updateUserProfile(localProfileToSave.copy(id = existing.id))
                     } else {
                         userProfileRepository.insertUserProfile(localProfileToSave)
                     }
                } else {
                     userProfileRepository.updateUserProfile(localProfileToSave)
                }
                _userProfile.value = localProfileToSave // Update UI immediately with local changes

                // 2. Attempt to push to backend
                val updateRequest = profileEntity.toUpdateRequest()
                val remoteResult = userProfileRepository.updateRemoteUserProfile(updateRequest)

                if (remoteResult.isSuccess) {
                    val updatedRemoteData = remoteResult.getOrNull()
                    if (updatedRemoteData != null) {
                        // If backend sends back updated data (e.g., new updated_at), update local entity
                        val syncedEntity = updatedRemoteData.toEntity(profileEntity.firebaseUid!!)
                                            .copy(id = localProfileToSave.id, isDataModified = false) // Mark as synced
                        userProfileRepository.updateUserProfile(syncedEntity)
                        _userProfile.value = syncedEntity
                        _successMessage.value = "Profile saved successfully to server."
                    } else {
                         _successMessage.value = "Profile saved locally. Server response was empty."
                    }
                } else {
                    // Remote update failed, local data is already saved but marked as modified.
                    _error.value = "Profile saved locally, but failed to sync to server: ${remoteResult.exceptionOrNull()?.message}"
                }

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
