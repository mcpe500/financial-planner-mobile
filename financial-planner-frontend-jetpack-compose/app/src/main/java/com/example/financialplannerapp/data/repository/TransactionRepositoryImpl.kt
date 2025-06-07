package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.TransactionDao
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.model.TransactionData
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TransactionRepositoryImpl constructor(
    private val transactionDao: TransactionDao,
    private val apiService: ApiService
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override fun getTransactionsByUserId(userId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByUserId(userId)
    }

    override suspend fun getTransactionById(id: Int): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }

    override fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(type)
    }

    override fun getTransactionsByCategory(categoryId: Int): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategory(categoryId)
    }

    override fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    override fun getUserTransactionsByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<TransactionEntity>> {
        return transactionDao.getUserTransactionsByDateRange(userId, startDate, endDate)
    }

    override suspend fun getTotalIncomeByUser(userId: String): Double? {
        return transactionDao.getTotalIncomeByUser(userId)
    }

    override suspend fun getTotalExpenseByUser(userId: String): Double? {
        return transactionDao.getTotalExpenseByUser(userId)
    }

    override suspend fun getTotalAmountByTypeAndDateRange(userId: String, type: String, startDate: Date, endDate: Date): Double? {
        return transactionDao.getTotalAmountByTypeAndDateRange(userId, type, startDate, endDate)
    }

    override suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    override suspend fun insertTransactions(transactions: List<TransactionEntity>): List<Long> {
        return transactionDao.insertTransactions(transactions)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    override suspend fun deleteTransactionById(id: Int) {
        transactionDao.deleteTransactionById(id)
    }

    override suspend fun deleteAllUserTransactions(userId: String) {
        transactionDao.deleteAllUserTransactions(userId)
    }

    override suspend fun syncTransactionsFromRemote(userId: String): Result<List<TransactionData>> {
        return try {
            val response = apiService.getUserTransactions(userId)
            if (response.isSuccessful && response.body() != null) {
                val transactions = response.body()!!
                // Convert TransactionData to TransactionEntity and save to local database
                val entities = transactions.map { transactionData ->
                    TransactionEntity(
                        id = transactionData.id,
                        amount = transactionData.amount,
                        date = transactionData.date,
                        description = transactionData.description,
                        type = if (transactionData.amount >= 0) "income" else "expense",
                        userId = userId,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }
                transactionDao.insertTransactions(entities)
                Result.success(transactions)
            } else {
                Result.failure(Exception("Failed to sync transactions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadTransactionsToRemote(transactions: List<TransactionEntity>): Result<Unit> {
        return try {
            // Convert TransactionEntity to TransactionData for API
            val transactionDataList = transactions.map { entity ->
                TransactionData(
                    id = entity.id,
                    amount = entity.amount,
                    date = entity.date,
                    description = entity.description
                )
            }
            
            val response = apiService.uploadTransactions(transactionDataList)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to upload transactions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
