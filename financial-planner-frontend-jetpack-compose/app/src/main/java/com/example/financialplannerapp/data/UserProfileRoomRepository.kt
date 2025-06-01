package com.example.financialplannerapp.data

import com.example.financialplannerapp.data.dao.UserProfileDao
import com.example.financialplannerapp.data.model.UserProfile
import com.example.financialplannerapp.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileRoomRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {

    suspend fun insertUserProfile(userProfile: UserProfile): Long {
        return userProfileDao.insertUserProfile(userProfile)
    }

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.updateUserProfile(userProfile)
    }

    suspend fun deleteUserProfile(userProfile: UserProfile) {
        userProfileDao.deleteUserProfile(userProfile)
    }

    fun getUserProfileById(id: Long): Flow<UserProfile?> {
        return userProfileDao.getUserProfileById(id)
    }

    fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfile?> {
        return userProfileDao.getUserProfileByFirebaseUid(firebaseUid)
    }

    fun getAllUserProfiles(): Flow<List<UserProfile>> {
        return userProfileDao.getAllUserProfiles()
    }

    fun getModifiedUserProfiles(): Flow<List<UserProfile>> {
        return userProfileDao.getModifiedUserProfiles()
    }
    
    suspend fun getProfilesNeedingSync(): List<UserProfile> {
        return userProfileDao.getProfilesNeedingSync()
    }

    suspend fun deleteAllUserProfiles() {
        userProfileDao.deleteAllUserProfiles()
    }
}