package com.example.financialplannerapp.data

import com.example.financialplannerapp.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * User Profile Repository Interface
 * 
 * Defines contract for user profile data operations
 */
interface UserProfileRepository {
    suspend fun insertUserProfile(userProfile: UserProfile): Long
    suspend fun updateUserProfile(userProfile: UserProfile)
    suspend fun deleteUserProfile(userProfile: UserProfile)
    fun getUserProfileById(id: Long): Flow<UserProfile?>
    fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfile?>
    fun getAllUserProfiles(): Flow<List<UserProfile>>
    fun getModifiedUserProfiles(): Flow<List<UserProfile>>
    suspend fun getProfilesNeedingSync(): List<UserProfile> // Changed from Flow to List based on DAO
    suspend fun deleteAllUserProfiles()
}