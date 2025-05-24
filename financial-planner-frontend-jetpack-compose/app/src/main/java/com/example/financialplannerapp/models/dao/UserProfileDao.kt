package com.example.financialplannerapp.models.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financialplannerapp.models.roomdb.UserProfile

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile)

    @Update
    suspend fun update(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun getUserProfileById(id: String): UserProfile?

    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun getUserProfileLiveDataById(id: String): LiveData<UserProfile?>

    @Query("DELETE FROM user_profile WHERE id = :id")
    suspend fun deleteUserProfileById(id: String)

    @Query("DELETE FROM user_profile")
    suspend fun clearAllUserProfiles()
}