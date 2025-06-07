package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserProfileRoomRepository Interface
 * 
 * Defines contract for UserProfile operations using Room database
 */
interface UserProfileRoomRepository {
    suspend fun insertUserProfile(userProfile: UserProfileEntity): Long
    suspend fun updateUserProfile(userProfile: UserProfileEntity)
    suspend fun deleteUserProfile(userProfile: UserProfileEntity)
    fun getUserProfileById(id: Long): Flow<UserProfileEntity?>
    fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfileEntity?>
    fun getAllUserProfiles(): Flow<List<UserProfileEntity>>
    fun getModifiedUserProfiles(): Flow<List<UserProfileEntity>>
    suspend fun getProfilesNeedingSync(): List<UserProfileEntity>
    suspend fun deleteAllUserProfiles()
}