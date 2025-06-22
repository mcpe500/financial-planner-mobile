package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.GoalDao
import com.example.financialplannerapp.data.local.model.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepositoryImpl(private val goalDao: GoalDao) : GoalRepository {

    override fun getGoalsForWallet(walletId: String, user_email: String): Flow<List<GoalEntity>> {
        return goalDao.getGoalsForWallet(walletId)
    }

    override suspend fun insertGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    override suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    override suspend fun deleteGoal(goalId: Int) {
        goalDao.deleteGoalById(goalId)
    }

    override suspend fun getAllGoalsByUser(userEmail: String): Flow<List<GoalEntity>> {
        return goalDao.getAllGoalsByUser(userEmail)
    }
}