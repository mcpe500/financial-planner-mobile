package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.UserProfileDao
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

// This repository implements the existing UserProfileRepository interface
@Singleton
class UserProfileRoomRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
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

    // Changed from getAllProfiles to getAllUserProfiles to match interface
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