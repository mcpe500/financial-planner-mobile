package com.example.financialplannerapp.data.dao

import androidx.room.*
import com.example.financialplannerapp.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile): Long

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)

    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun getUserProfileById(id: Long): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE firebaseUid = :firebaseUid LIMIT 1")
    fun getUserProfileByFirebaseUid(firebaseUid: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile")
    fun getAllUserProfiles(): Flow<List<UserProfile>>

    // Example: Query for profiles that need syncing or have local modifications
    @Query("SELECT * FROM user_profile WHERE is_data_modified = 1") // Assuming 1 for true
    fun getModifiedUserProfiles(): Flow<List<UserProfile>>
    
    @Query("SELECT * FROM user_profile WHERE is_synced_with_server = 0") // Assuming 0 for false
    suspend fun getProfilesNeedingSync(): List<UserProfile>

    @Query("DELETE FROM user_profile")
    suspend fun deleteAllUserProfiles()
}