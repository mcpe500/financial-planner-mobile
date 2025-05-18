package com.example.financialplannerapp.db

import androidx.room.*
import com.example.financialplannerapp.models.roomdb.UserProfile

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getUserProfile(userId: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Query("SELECT * FROM user_profiles WHERE needsSync = 1")
    suspend fun getProfilesNeedingSync(): List<UserProfile>

    @Delete
    suspend fun delete(profile: UserProfile)
}