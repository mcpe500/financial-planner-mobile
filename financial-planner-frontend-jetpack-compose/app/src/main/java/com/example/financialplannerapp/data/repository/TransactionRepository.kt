package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.Transaction
import com.example.financialplannerapp.data.local.TransactionDao
import com.example.financialplannerapp.data.remote.TransactionApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val transactionApi: TransactionApi
) {

    fun getAllTransactions(userId: String): Flow<List<Transaction>> = flow {
        // First try to get data from local database
        val localTransactions = transactionDao.getAllByUser(userId)
        emit(localTransactions)

        // Then try to sync with remote
        try {
            val remoteTransactions = transactionApi.getUserTransactions(userId)
            transactionDao.insertAll(remoteTransactions)
            emit(remoteTransactions)
        } catch (e: Exception) {
            // If sync fails, just return local data
            emit(localTransactions)
        }
    }

    suspend fun getTransactionById(id: String): Transaction? {
        return transactionDao.getById(id) ?: transactionApi.getTransactionById(id)
    }

    suspend fun createTransaction(transaction: Transaction) {
        try {
            // First save locally
            transactionDao.insert(transaction.copy(syncStatus = "pending"))
            
            // Then try to sync with remote
            val createdTransaction = transactionApi.createTransaction(transaction)
            transactionDao.update(createdTransaction.copy(syncStatus = "synced"))
        } catch (e: Exception) {
            // If sync fails, keep transaction as pending
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        try {
            // First update locally
            transactionDao.update(transaction.copy(syncStatus = "pending"))
            
            // Then try to sync with remote
            val updatedTransaction = transactionApi.updateTransaction(transaction.id, transaction)
            transactionDao.update(updatedTransaction.copy(syncStatus = "synced"))
        } catch (e: Exception) {
            // If sync fails, keep transaction as pending
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        try {
            // First delete locally
            transactionDao.delete(transaction)
            
            // Then try to sync with remote
            transactionApi.deleteTransaction(transaction.id)
        } catch (e: Exception) {
            // If sync fails, mark as deleted locally
            transactionDao.update(transaction.copy(syncStatus = "deleted"))
        }
    }

    suspend fun syncPendingTransactions(userId: String) {
        val pendingTransactions = transactionDao.getPendingSync(userId)
        pendingTransactions.forEach { transaction ->
            when (transaction.syncStatus) {
                "pending" -> {
                    try {
                        val createdTransaction = transactionApi.createTransaction(transaction)
                        transactionDao.update(createdTransaction.copy(syncStatus = "synced"))
                    } catch (e: Exception) {
                        // Keep as pending if sync fails
                    }
                }
                "deleted" -> {
                    try {
                        transactionApi.deleteTransaction(transaction.id)
                        transactionDao.delete(transaction)
                    } catch (e: Exception) {
                        // Keep as deleted if sync fails
                    }
                }
            }
        }
    }
}
