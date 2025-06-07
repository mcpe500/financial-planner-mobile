package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.model.TransactionData
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    fun getTransactionsByUserId(userId: String): Flow<List<TransactionEntity>>
    suspend fun getTransactionById(id: Int): TransactionEntity?
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>
    fun getTransactionsByCategory(categoryId: Int): Flow<List<TransactionEntity>>
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<TransactionEntity>>
    fun getUserTransactionsByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<TransactionEntity>>
    suspend fun getTotalIncomeByUser(userId: String): Double?
    suspend fun getTotalExpenseByUser(userId: String): Double?
    suspend fun getTotalAmountByTypeAndDateRange(userId: String, type: String, startDate: Date, endDate: Date): Double?
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long>
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)
    suspend fun deleteTransactionById(id: Int)
    suspend fun deleteAllUserTransactions(userId: String)
    suspend fun syncTransactionsFromRemote(userId: String): Result<List<TransactionData>>
    suspend fun uploadTransactionsToRemote(transactions: List<TransactionEntity>): Result<Unit>
}
