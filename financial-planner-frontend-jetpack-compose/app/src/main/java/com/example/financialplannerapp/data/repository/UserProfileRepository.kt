package com.example.financialplannerapp.data.repository

import androidx.lifecycle.LiveData
import com.example.financialplannerapp.models.dao.UserProfileDao
import com.example.financialplannerapp.models.roomdb.UserProfile

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    suspend fun insert(userProfile: UserProfile) {
        userProfileDao.insert(userProfile)
    }

    suspend fun update(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }

    suspend fun getUserProfileById(id: String): UserProfile? {
        return userProfileDao.getUserProfileById(id)
    }

    fun getUserProfileLiveDataById(id: String): LiveData<UserProfile?> {
        return userProfileDao.getUserProfileLiveDataById(id)
    }

    suspend fun deleteUserProfileById(id: String) {
        userProfileDao.deleteUserProfileById(id)
    }

    suspend fun clearAllUserProfiles() {
        userProfileDao.clearAllUserProfiles()
    }
}