package com.example.financialplannerapp.data.local.dao

import androidx.room.*
import com.example.financialplannerapp.data.local.model.ReceiptTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptTransactionDao {
    
    @Query("SELECT * FROM receipt_transactions ORDER BY createdAt DESC")
    fun getAllReceiptTransactions(): Flow<List<ReceiptTransactionEntity>>
    
    @Query("SELECT * FROM receipt_transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getReceiptTransactionsByUserId(userId: String): Flow<List<ReceiptTransactionEntity>>
    
    @Query("SELECT * FROM receipt_transactions WHERE id = :id")
    suspend fun getReceiptTransactionById(id: Int): ReceiptTransactionEntity?
    
    @Query("SELECT * FROM receipt_transactions WHERE receiptId = :receiptId")
    suspend fun getReceiptTransactionByReceiptId(receiptId: String): ReceiptTransactionEntity?
    
    @Query("SELECT * FROM receipt_transactions WHERE isProcessed = 0 AND userId = :userId")
    suspend fun getUnprocessedReceiptTransactions(userId: String): List<ReceiptTransactionEntity>
    
    @Query("SELECT * FROM receipt_transactions WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedReceiptTransactions(userId: String): List<ReceiptTransactionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceiptTransaction(receiptTransaction: ReceiptTransactionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceiptTransactions(receiptTransactions: List<ReceiptTransactionEntity>): List<Long>
    
    @Update
    suspend fun updateReceiptTransaction(receiptTransaction: ReceiptTransactionEntity)
    
    @Delete
    suspend fun deleteReceiptTransaction(receiptTransaction: ReceiptTransactionEntity)
    
    @Query("DELETE FROM receipt_transactions WHERE id = :id")
    suspend fun deleteReceiptTransactionById(id: Int)
    
    @Query("DELETE FROM receipt_transactions WHERE userId = :userId")
    suspend fun deleteAllUserReceiptTransactions(userId: String)
    
    @Query("UPDATE receipt_transactions SET isProcessed = 1 WHERE id = :id")
    suspend fun markReceiptTransactionAsProcessed(id: Int)
    
    @Query("UPDATE receipt_transactions SET isSynced = 1, backendTransactionId = :backendId WHERE id = :id")
    suspend fun markReceiptTransactionAsSynced(id: Int, backendId: String?)
    
    @Query("DELETE FROM receipt_transactions")
    suspend fun deleteAllReceiptTransactions()
}
