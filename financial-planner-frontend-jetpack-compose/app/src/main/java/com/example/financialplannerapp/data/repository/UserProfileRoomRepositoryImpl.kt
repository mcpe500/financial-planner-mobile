package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.UserProfileDao
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserProfileRoomRepositoryImpl
 * 
 * Implementation of UserProfileRoomRepository interface using Room database
 */
class UserProfileRoomRepositoryImpl constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRoomRepository {

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
