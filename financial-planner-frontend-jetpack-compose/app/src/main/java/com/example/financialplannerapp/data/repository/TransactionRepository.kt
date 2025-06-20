package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)
    suspend fun getTransactionById(id: Long): TransactionEntity?
    fun getTransactionsByUserId(userId: String): Flow<List<TransactionEntity>>
    fun getTransactionsByType(userId: String, type: String): Flow<List<TransactionEntity>>
    fun getTransactionsByCategory(userId: String, category: String): Flow<List<TransactionEntity>>
    fun getReceiptTransactions(userId: String): Flow<List<TransactionEntity>>
}