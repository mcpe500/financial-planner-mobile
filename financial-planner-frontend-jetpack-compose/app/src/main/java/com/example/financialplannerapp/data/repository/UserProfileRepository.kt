package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.UserProfileDao
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import com.example.financialplannerapp.data.model.UserData
import com.example.financialplannerapp.data.requests.UserProfileUpdateRequest
import com.example.financialplannerapp.TokenManager // Added for fetching token, though interceptor should handle it.
import android.content.Context // For TokenManager, if needed directly.

/**
 * User Profile Repository Interface
 * 
 * Defines contract for user profile data operations
 */
interface UserProfileRepository {
    // Local operations
    suspend fun insertUserProfile(userProfile: UserProfileEntity): Long
    suspend fun updateUserProfile(userProfile: UserProfileEntity)
    suspend fun deleteUserProfile(userProfile: UserProfileEntity)
    fun getUserProfileById(id: Long): Flow<UserProfileEntity?>
    fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfileEntity?>
    fun getAllUserProfiles(): Flow<List<UserProfileEntity>>
    fun getModifiedUserProfiles(): Flow<List<UserProfileEntity>>
    suspend fun getProfilesNeedingSync(): List<UserProfileEntity>
    suspend fun deleteAllUserProfiles()

    // Remote operations
    suspend fun fetchRemoteUserProfile(): Result<UserData>
    suspend fun updateRemoteUserProfile(profileData: UserProfileUpdateRequest): Result<UserData>
}

class UserProfileRepositoryImpl constructor(
    private val userProfileDao: UserProfileDao,
    private val apiService: ApiService,
    private val tokenManager: TokenManager // Assuming TokenManager is injected or accessible
    // private val context: Context // Alternative if TokenManager needs context and is created here
) : UserProfileRepository {

    // Local operations implementation
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

    // Remote operations implementation
    override suspend fun fetchRemoteUserProfile(): Result<UserData> {
        // Token is assumed to be added by RetrofitClient's interceptor using TokenManager
        // If specific error handling or token checks are needed here, TokenManager can be used.
        // val token = tokenManager.getToken().first() // Example if token needed explicitly
        // if (token.isNullOrBlank()) return Result.failure(Exception("User not authenticated"))

        return try {
            val response = apiService.getProfile("Bearer ${tokenManager.getToken().first() ?: ""}") // Pass dummy token if interceptor handles it, or actual token
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("User data not found in response"))
            } else {
                Result.failure(Exception("Failed to fetch profile: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRemoteUserProfile(profileData: UserProfileUpdateRequest): Result<UserData> {
        // Token is assumed to be added by RetrofitClient's interceptor
        // val token = tokenManager.getToken().first()  // Example if token needed explicitly
        // if (token.isNullOrBlank()) return Result.failure(Exception("User not authenticated"))

        return try {
            val response = apiService.updateProfile("Bearer ${tokenManager.getToken().first() ?: ""}", profileData) // Pass dummy token if interceptor handles it
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("User data not found in response after update"))
            } else {
                Result.failure(Exception("Failed to update profile: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}