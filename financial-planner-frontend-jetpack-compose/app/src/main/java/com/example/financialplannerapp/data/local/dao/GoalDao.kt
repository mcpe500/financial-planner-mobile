package com.example.financialplannerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financialplannerapp.data.local.model.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE walletId = :walletId")
    fun getGoalsForWallet(walletId: String): Flow<List<GoalEntity>>

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Int)

    @Query("SELECT * FROM goals WHERE user_email = :userEmail")
    fun getAllGoalsByUser(userEmail: String): Flow<List<GoalEntity>>
} 