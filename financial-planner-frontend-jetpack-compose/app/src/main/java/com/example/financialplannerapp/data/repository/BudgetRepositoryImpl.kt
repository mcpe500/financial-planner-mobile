package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.BudgetDao
import com.example.financialplannerapp.data.local.model.BudgetEntity
import kotlinx.coroutines.flow.Flow


class BudgetRepositoryImpl(private val budgetDao: BudgetDao) : BudgetRepository {

    override fun getBudgetsForWallet(walletId: String, user_email: String): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForWallet(walletId, user_email)
    }

    override suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }

    override suspend fun updateBudget(budget: BudgetEntity) {
        budgetDao.updateBudget(budget)
    }

    override suspend fun deleteBudget(budgetId: Int) {
        budgetDao.deleteBudgetById(budgetId)
    }
} 