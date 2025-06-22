package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.BudgetDao
import com.example.financialplannerapp.data.local.model.BudgetEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsForWallet(walletId: String): Flow<List<BudgetEntity>>
    suspend fun insertBudget(budget: BudgetEntity)
    suspend fun updateBudget(budget: BudgetEntity)
    suspend fun deleteBudget(budgetId: Int)
}
