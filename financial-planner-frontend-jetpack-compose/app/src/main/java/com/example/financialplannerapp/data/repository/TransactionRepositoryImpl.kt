package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.TransactionDao
import com.example.financialplannerapp.data.local.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    override suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    override suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)
    override suspend fun getTransactionById(id: Long) = transactionDao.getTransactionById(id)
    override fun getTransactionsByUserId(userId: String) = transactionDao.getTransactionsByUserId(userId)
    override fun getTransactionsByType(userId: String, type: String) = transactionDao.getTransactionsByType(userId, type)
    override fun getTransactionsByCategory(userId: String, category: String) = transactionDao.getTransactionsByCategory(userId, category)
    override fun getReceiptTransactions(userId: String) = transactionDao.getReceiptTransactions(userId)
}
