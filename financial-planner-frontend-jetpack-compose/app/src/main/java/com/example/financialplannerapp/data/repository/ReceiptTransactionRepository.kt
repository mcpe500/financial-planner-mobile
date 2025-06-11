package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.ReceiptTransactionEntity
import com.example.financialplannerapp.data.model.ReceiptOCRData
import kotlinx.coroutines.flow.Flow

interface ReceiptTransactionRepository {
    fun getAllReceiptTransactions(): Flow<List<ReceiptTransactionEntity>>
    fun getReceiptTransactionsByUserId(userId: String): Flow<List<ReceiptTransactionEntity>>
    suspend fun getReceiptTransactionById(id: Int): ReceiptTransactionEntity?
    suspend fun getReceiptTransactionByReceiptId(receiptId: String): ReceiptTransactionEntity?
    suspend fun getUnprocessedReceiptTransactions(userId: String): List<ReceiptTransactionEntity>
    suspend fun getUnsyncedReceiptTransactions(userId: String): List<ReceiptTransactionEntity>
    suspend fun insertReceiptTransaction(receiptTransaction: ReceiptTransactionEntity): Long
    suspend fun insertReceiptTransactions(receiptTransactions: List<ReceiptTransactionEntity>): List<Long>
    suspend fun updateReceiptTransaction(receiptTransaction: ReceiptTransactionEntity)
    suspend fun deleteReceiptTransaction(receiptTransaction: ReceiptTransactionEntity)
    suspend fun deleteReceiptTransactionById(id: Int)
    suspend fun deleteAllUserReceiptTransactions(userId: String)
    suspend fun markReceiptTransactionAsProcessed(id: Int)
    suspend fun markReceiptTransactionAsSynced(id: Int, backendId: String?)
    suspend fun storeReceiptTransactionFromOCR(ocrData: ReceiptOCRData, userId: String): Result<ReceiptTransactionEntity>
    suspend fun syncReceiptTransactionToBackend(receiptTransaction: ReceiptTransactionEntity): Result<String>
    suspend fun convertReceiptToRegularTransaction(receiptTransactionId: Int): Result<Long>
}
