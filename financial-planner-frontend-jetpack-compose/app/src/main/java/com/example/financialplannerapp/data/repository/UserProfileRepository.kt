package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.dao.UserProfileDao
import com.example.financialplannerapp.data.model.UserProfile
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

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
    suspend fun getProfilesNeedingSync(): List<UserProfile>
    suspend fun deleteAllUserProfiles()
}

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val apiService: ApiService
) : UserProfileRepository {

    override suspend fun insertUserProfile(userProfile: UserProfile): Long {
        return userProfileDao.insertUserProfile(userProfile)
    }

    override suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.updateUserProfile(userProfile)
    }

    override suspend fun deleteUserProfile(userProfile: UserProfile) {
        userProfileDao.deleteUserProfile(userProfile)
    }

    override fun getUserProfileById(id: Long): Flow<UserProfile?> {
        return userProfileDao.getUserProfileById(id)
    }

    override fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfile?> {
        return userProfileDao.getUserProfileByFirebaseUid(firebaseUid)
    }

    override fun getAllUserProfiles(): Flow<List<UserProfile>> {
        return userProfileDao.getAllUserProfiles()
    }

    override fun getModifiedUserProfiles(): Flow<List<UserProfile>> {
        return userProfileDao.getModifiedUserProfiles()
    }
    
    override suspend fun getProfilesNeedingSync(): List<UserProfile> {
        return userProfileDao.getProfilesNeedingSync()
    }

    override suspend fun deleteAllUserProfiles() {
        userProfileDao.deleteAllUserProfiles()
    }
}