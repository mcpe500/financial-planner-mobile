package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.UserProfileDao
import com.example.financialplannerapp.data.local.model.UserProfileEntity
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

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val apiService: ApiService
) : UserProfileRepository {

    override suspend fun insertUserProfile(userProfile: UserProfileEntity): Long {
        return userProfileDao.insertUserProfile(userProfile)
    }

    override suspend fun updateUserProfile(userProfile: UserProfileEntity) {
        userProfileDao.updateUserProfile(userProfile)
    }

    override suspend fun deleteUserProfile(userProfile: UserProfileEntity) {
        userProfileDao.deleteUserProfile(userProfile)
    }

    override fun getUserProfileById(id: Long): Flow<UserProfileEntity?> {
        return userProfileDao.getUserProfileById(id)
    }

    override fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfileEntity?> {
        return userProfileDao.getUserProfileByFirebaseUid(firebaseUid)
    }

    override fun getAllUserProfiles(): Flow<List<UserProfileEntity>> {
        return userProfileDao.getAllUserProfiles()
    }

    override fun getModifiedUserProfiles(): Flow<List<UserProfileEntity>> {
        return userProfileDao.getModifiedUserProfiles()
    }
    
    override suspend fun getProfilesNeedingSync(): List<UserProfileEntity> {
        return userProfileDao.getProfilesNeedingSync()
    }

    override suspend fun deleteAllUserProfiles() {
        userProfileDao.deleteAllUserProfiles()
    }
}