 package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.GoalDao
import com.example.financialplannerapp.data.local.model.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {

    fun getGoalsForWallet(walletId: String): Flow<List<GoalEntity>> {
        return goalDao.getGoalsForWallet(walletId)
    }

    suspend fun insertGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goalId: Int) {
        goalDao.deleteGoalById(goalId)
    }
}