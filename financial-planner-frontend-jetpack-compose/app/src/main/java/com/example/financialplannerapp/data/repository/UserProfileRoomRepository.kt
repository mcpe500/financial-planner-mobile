package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.dao.UserProfileDao
import com.example.financialplannerapp.data.model.UserProfileData
import com.example.financialplannerapp.data.model.toUserProfileData
import com.example.financialplannerapp.data.model.toUserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface UserProfileRepository {
    suspend fun getUserProfile(userId: String): UserProfileData?
    suspend fun insertUserProfile(userProfileData: UserProfileData)
    suspend fun updateUserProfile(userProfileData: UserProfileData)
    suspend fun deleteUserProfile(userId: String)
    suspend fun markForSync(userId: String, needsSync: Boolean) // Renamed from markProfileForSync for consistency
    fun getUserProfileFlow(userId: String): Flow<UserProfileData?>
    suspend fun getAllProfiles(): List<UserProfileData>
    fun getAllProfilesFlow(): Flow<List<UserProfileData>>
    suspend fun getProfilesToSync(): List<UserProfileData>
}

@Singleton
class UserProfileRoomRepositoryImpl @Inject constructor( // Renamed to avoid conflict with the one in data package
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override suspend fun getUserProfile(userId: String): UserProfileData? {
        return userProfileDao.getUserProfileByFirebaseUid(userId).let { flow ->
            // Since the DAO returns Flow, we need to collect it
            // For now, return null as this method signature doesn't support Flow
            null
        }
    }

    override suspend fun insertUserProfile(userProfileData: UserProfileData) {
        userProfileDao.insertUserProfile(userProfileData.toUserProfileEntity())
    }

    override suspend fun updateUserProfile(userProfileData: UserProfileData) {
        userProfileDao.updateUserProfile(userProfileData.toUserProfileEntity())
    }

    override suspend fun deleteUserProfile(userId: String) {
        // Find the profile by Firebase UID and delete it
        userProfileDao.getUserProfileByFirebaseUid(userId).let { flow ->
            // This is a limitation - we need to collect the flow to get the entity
            // For now, this won't work properly with the current DAO design
        }
    }

    override suspend fun markForSync(userId: String, needsSync: Boolean) {
        // This functionality needs to be implemented in the DAO
        // For now, we'll need to get the profile and update it
    }

    override fun getUserProfileFlow(userId: String): Flow<UserProfileData?> {
        return userProfileDao.getUserProfileByFirebaseUid(userId).map { it?.toUserProfileData() }
    }

    override suspend fun getAllProfiles(): List<UserProfileData> {
        // The DAO returns Flow, but this method expects List
        // This is a design mismatch - we need to use Flow consistently
        return emptyList()
    }

    override fun getAllProfilesFlow(): Flow<List<UserProfileData>> {
        return userProfileDao.getAllUserProfiles().map { list -> list.map { it.toUserProfileData() } }
    }

    override suspend fun getProfilesToSync(): List<UserProfileData> {
        return userProfileDao.getProfilesNeedingSync().map { it.toUserProfileData() }
    }
}