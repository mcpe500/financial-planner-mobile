package com.example.financialplannerapp.data.local.dao

import androidx.room.*
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity): Long
    
    @Update
    suspend fun updateUserProfile(userProfile: UserProfileEntity)
    
    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfileEntity)
    
    @Query("SELECT * FROM user_profile WHERE id = :id")
    fun getUserProfileById(id: Long): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profile WHERE firebaseUid = :firebaseUid")
    fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profile")
    fun getAllUserProfiles(): Flow<List<UserProfileEntity>>
    
    @Query("SELECT * FROM user_profile WHERE is_data_modified = 1")
    fun getModifiedUserProfiles(): Flow<List<UserProfileEntity>>
    
    @Query("SELECT * FROM user_profile WHERE is_synced_with_server = 0 OR is_data_modified = 1")
    suspend fun getProfilesNeedingSync(): List<UserProfileEntity>
    
    @Query("DELETE FROM user_profile")
    suspend fun deleteAllUserProfiles()
}