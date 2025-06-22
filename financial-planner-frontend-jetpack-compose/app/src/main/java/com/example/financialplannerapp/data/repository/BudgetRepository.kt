package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.BudgetDao
import com.example.financialplannerapp.data.local.model.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getBudgetsForWallet(walletId: String): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForWallet(walletId)
    }

    suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budgetId: Int) {
        budgetDao.deleteBudgetById(budgetId)
    }
} 