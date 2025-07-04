package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.TransactionDao
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.model.TransactionData
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import android.util.Log

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
        Log.d("TransactionRepo", "Inserting ${transactions.size} transactions to RoomDB")
        transactions.forEach { transaction ->
            // Prevent duplicate by backendTransactionId
            val backendId = transaction.backendTransactionId
            if (backendId != null) {
                val existing = transactionDao.getTransactionByBackendId(backendId)
                if (existing == null) {
                    Log.d("TransactionRepo", "Inserting transaction: $transaction")
                    transactionDao.insertTransaction(transaction)
                } else {
                    Log.d("TransactionRepo", "Skipping duplicate transaction with backendId: $backendId")
                }
            } else {
                // Fallback: insert if no backendId (local only)
                Log.d("TransactionRepo", "Inserting transaction (no backendId): $transaction")
                transactionDao.insertTransaction(transaction)
            }
        }
    }

    override suspend fun createTransactionRemote(transaction: TransactionData): TransactionData? {
        val response = apiService.createTransaction(transaction)
        return if (response.isSuccessful) response.body()?.data else null
    }

    override fun getCurrentUserId(): String? {
        // TODO: Implement this method to securely fetch the current user's ID.
        // This ID is crucial for associating transactions with the correct user,
        // especially for local database operations initiated within the repository
        // or by services that don't explicitly pass the userId.
        //
        // Option 1: Inject TokenManager into this repository.
        //   - Ensure TokenManager is provided via DI (e.g., Hilt, Koin).
        //   - Call a method on TokenManager that returns the user ID.
        //   - Example:
        //     // constructor(..., private val tokenManager: TokenManager)
        //     // return tokenManager.getUserId() // Or whatever method provides the ID
        //
        // Option 2: Access TokenManager via a singleton or service locator if DI is not used here.
        //   - Example:
        //     // return AppTokenManager.getInstance().getUserId()
        //
        // Option 3: If user ID is available synchronously from a DataStore flow used by TokenManager.
        //   - This would likely need to be a suspend function or change to return Flow<String?>
        //     to be collected appropriately.
        //     // kotlinx.coroutines.runBlocking { tokenManager.getUserIdFlow().firstOrNull() } (Use with caution)

        // Placeholder implementation:
        Log.w("TransactionRepositoryImpl", "getCurrentUserId() is not fully implemented. Returning null.")
        return try {
            // Replace this with actual TokenManager usage.
            // For example, if TokenManager was a constructor parameter:
            // return tokenManager.getUserId()
            null
        } catch (e: Exception) {
            Log.e("TransactionRepositoryImpl", "Error in getCurrentUserId placeholder", e)
            null
        }
    }
}
