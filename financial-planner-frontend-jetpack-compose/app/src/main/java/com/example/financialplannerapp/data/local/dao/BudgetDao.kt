package com.example.financialplannerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financialplannerapp.data.local.model.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE walletId = :walletId")
    fun getBudgetsForWallet(walletId: String): Flow<List<BudgetEntity>>

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteBudgetById(budgetId: Int)
} 