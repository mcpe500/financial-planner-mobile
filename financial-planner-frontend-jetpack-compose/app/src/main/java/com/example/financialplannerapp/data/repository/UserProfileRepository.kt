package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.UserProfileRepository
import com.example.financialplannerapp.data.dao.UserProfileDao
import com.example.financialplannerapp.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
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