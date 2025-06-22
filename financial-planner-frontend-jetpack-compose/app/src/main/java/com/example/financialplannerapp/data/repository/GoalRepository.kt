package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.GoalDao
import com.example.financialplannerapp.data.local.model.GoalEntity
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoalsForWallet(walletId: String, user_email: String): Flow<List<GoalEntity>>
    suspend fun insertGoal(goal: GoalEntity)
    suspend fun updateGoal(goal: GoalEntity)
    suspend fun deleteGoal(goalId: Int)
}

