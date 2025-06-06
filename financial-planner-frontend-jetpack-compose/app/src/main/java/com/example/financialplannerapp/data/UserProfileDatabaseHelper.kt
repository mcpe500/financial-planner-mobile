package com.example.financialplannerapp.data

import android.content.Context
import com.example.financialplannerapp.data.local.AppDatabase
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import com.example.financialplannerapp.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
/**
 * Helper class for UserProfile operations using Room database
 * This replaces the old SQLiteOpenHelper approach
 */
@Singleton
class UserProfileDatabaseHelper @Inject constructor(
    private val context: Context
) {

    private val database: AppDatabase by lazy {
        DatabaseManager.getDatabase(context)
    }

    private val userProfileDao by lazy {
        database.userProfileDao()
    }

    suspend fun addUserProfile(userProfile: UserProfileEntity): Long {
        return userProfileDao.insertUserProfile(userProfile)
    }

    fun getUserProfile(userId: Long): Flow<UserProfileEntity?> {
        return userProfileDao.getUserProfileById(userId)
    }

    fun getAllUserProfiles(): Flow<List<UserProfileEntity>> {
        return userProfileDao.getAllUserProfiles()
    }

    suspend fun updateUserProfile(userProfile: UserProfileEntity) {
        userProfileDao.updateUserProfile(userProfile)
    }

    suspend fun deleteUserProfile(userProfile: UserProfileEntity) {
        userProfileDao.deleteUserProfile(userProfile)
    }
}