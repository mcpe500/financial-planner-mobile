package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.TransactionDao
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.model.TransactionData
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val apiService: ApiService
) : TransactionRepository {
    override suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    override suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)
    override suspend fun deleteTransaction(transaction: TransactionEntity) = transactionDao.deleteTransaction(transaction)
    override suspend fun getTransactionById(id: Long) = transactionDao.getTransactionById(id)
    override fun getTransactionsByUserId(userId: String) = transactionDao.getTransactionsByUserId(userId)
    override fun getTransactionsByType(userId: String, type: String) = transactionDao.getTransactionsByType(userId, type)
    override fun getTransactionsByCategory(userId: String, category: String) = transactionDao.getTransactionsByCategory(userId, category)
    override fun getReceiptTransactions(userId: String) = transactionDao.getReceiptTransactions(userId)

    override suspend fun getTransactionsFromBackend(): List<TransactionData> {
        val response = apiService.getUserTransactions()
        return if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
    }

    override suspend fun getTransactionDetailFromBackend(id: String): TransactionData? {
        val response = apiService.getTransactionById(id)
        return if (response.isSuccessful) response.body()?.data else null
    }

    override suspend fun uploadTransactionsToBackend(transactions: List<TransactionData>): Boolean {
        val response = apiService.uploadTransactions(transactions)
        return response.isSuccessful
    }

    override suspend fun getUnsyncedTransactions(userId: String): List<TransactionEntity> {
        return transactionDao.getUnsyncedTransactions(userId)
    }

    override suspend fun markTransactionsAsSynced(ids: List<Long>) {
        transactionDao.markTransactionsAsSynced(ids)
    }

    override suspend fun insertTransactions(transactions: List<TransactionEntity>) {
        transactions.forEach { transactionDao.insertTransaction(it) }
    }
}
