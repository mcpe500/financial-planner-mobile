package com.example.financialplannerapp.data.local.dao

import androidx.room.*
import com.example.financialplannerapp.data.local.model.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUserId(userId: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Int): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getUserTransactionsByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<TransactionEntity>>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income' AND userId = :userId")
    suspend fun getTotalIncomeByUser(userId: String): Double?
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND userId = :userId")
    suspend fun getTotalExpenseByUser(userId: String): Double?
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate AND userId = :userId")
    suspend fun getTotalAmountByTypeAndDateRange(userId: String, type: String, startDate: Date, endDate: Date): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long>
    
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)
    
    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllUserTransactions(userId: String)
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
