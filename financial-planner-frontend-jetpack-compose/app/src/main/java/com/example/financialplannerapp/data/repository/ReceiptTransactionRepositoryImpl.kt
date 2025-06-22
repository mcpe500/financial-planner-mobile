package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.ReceiptTransactionDao
import com.example.financialplannerapp.data.local.dao.TransactionDao
import com.example.financialplannerapp.data.local.model.ReceiptTransactionEntity
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.model.ReceiptOCRData
import com.example.financialplannerapp.data.remote.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class ReceiptTransactionRepositoryImpl constructor(
    private val receiptTransactionDao: ReceiptTransactionDao,
    private val transactionDao: TransactionDao,
    private val apiService: ApiService
) : ReceiptTransactionRepository {

    private val moshi = Moshi.Builder().build()

    override fun getAllReceiptTransactions(): Flow<List<ReceiptTransactionEntity>> {
        return receiptTransactionDao.getAllReceiptTransactions()
    }

    override fun getReceiptTransactionsByUserId(userId: String): Flow<List<ReceiptTransactionEntity>> {
        return receiptTransactionDao.getReceiptTransactionsByUserId(userId)
    }

    override suspend fun getReceiptTransactionById(id: Int): ReceiptTransactionEntity? {
        return receiptTransactionDao.getReceiptTransactionById(id)
    }

    override suspend fun getReceiptTransactionByReceiptId(receiptId: String): ReceiptTransactionEntity? {
        return receiptTransactionDao.getReceiptTransactionByReceiptId(receiptId)
    }

    override suspend fun getUnprocessedReceiptTransactions(userId: String): List<ReceiptTransactionEntity> {
        return receiptTransactionDao.getUnprocessedReceiptTransactions(userId)
    }

    override suspend fun getUnsyncedReceiptTransactions(userId: String): List<ReceiptTransactionEntity> {
        return receiptTransactionDao.getUnsyncedReceiptTransactions(userId)
    }

    override suspend fun insertReceiptTransaction(receiptTransaction: ReceiptTransactionEntity): Long {
        return receiptTransactionDao.insertReceiptTransaction(receiptTransaction)
    }

    override suspend fun insertReceiptTransactions(receiptTransactions: List<ReceiptTransactionEntity>): List<Long> {
        return receiptTransactionDao.insertReceiptTransactions(receiptTransactions)
    }

    override suspend fun updateReceiptTransaction(receiptTransaction: ReceiptTransactionEntity) {
        receiptTransactionDao.updateReceiptTransaction(receiptTransaction.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteReceiptTransaction(receiptTransaction: ReceiptTransactionEntity) {
        receiptTransactionDao.deleteReceiptTransaction(receiptTransaction)
    }

    override suspend fun deleteReceiptTransactionById(id: Int) {
        receiptTransactionDao.deleteReceiptTransactionById(id)
    }

    override suspend fun deleteAllUserReceiptTransactions(userId: String) {
        receiptTransactionDao.deleteAllUserReceiptTransactions(userId)
    }

    override suspend fun markReceiptTransactionAsProcessed(id: Int) {
        receiptTransactionDao.markReceiptTransactionAsProcessed(id)
    }

    override suspend fun markReceiptTransactionAsSynced(id: Int, backendId: String?) {
        receiptTransactionDao.markReceiptTransactionAsSynced(id, backendId)
    }

    override suspend fun storeReceiptTransactionFromOCR(ocrData: ReceiptOCRData, userId: String): Result<ReceiptTransactionEntity> {
        return try {
            val receiptId = ocrData.receiptId ?: "receipt_${System.currentTimeMillis()}"
            
            // Check if receipt already exists to prevent duplicates
            val existingReceipt = receiptTransactionDao.getReceiptTransactionByReceiptId(receiptId)
            if (existingReceipt != null) {
                return Result.success(existingReceipt)
            }
            
            // Convert items to JSON string
            val itemsJson = if (ocrData.items.isNotEmpty()) {
                val listType = Types.newParameterizedType(List::class.java, Map::class.java)
                val adapter = moshi.adapter<List<Map<String, Any>>>(listType)
                val itemsAsMap = ocrData.items.map { item ->
                    mapOf(
                        "name" to item.name,
                        "price" to item.price,
                        "quantity" to item.quantity,
                        "category" to (item.category ?: "Unknown")
                    )
                }
                adapter.toJson(itemsAsMap)
            } else null

            // Parse date string
            val date = try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(ocrData.date)
                    ?: Date()
            } catch (e: Exception) {
                Date()
            }

            val receiptTransaction = ReceiptTransactionEntity(
                receiptId = receiptId,
                totalAmount = ocrData.totalAmount,
                merchantName = ocrData.merchantName,
                date = date,
                location = ocrData.location,
                confidence = ocrData.confidence,
                items = itemsJson,
                category = inferCategoryFromItems(ocrData.items),
                userId = userId,
                isProcessed = false,
                isSynced = false
            )

            val id = receiptTransactionDao.insertReceiptTransaction(receiptTransaction)
            val storedTransaction = receiptTransaction.copy(id = id.toInt())
            
            Result.success(storedTransaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncReceiptTransactionToBackend(receiptTransaction: ReceiptTransactionEntity): Result<String> {
        return try {
            // Convert to backend format
            val requestBody = mapOf(
                "total_amount" to receiptTransaction.totalAmount,
                "merchant_name" to receiptTransaction.merchantName,
                "date" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(receiptTransaction.date),
                "location" to receiptTransaction.location,
                "receipt_id" to receiptTransaction.receiptId,
                "category" to receiptTransaction.category,
                "notes" to receiptTransaction.notes,
                "items" to receiptTransaction.items?.let { parseItemsJson(it) }
            )

            // Make API call to store transaction
            val response = apiService.storeTransactionFromOCR(requestBody)
            
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val backendTransactionId = responseBody.data?.transaction_id
                
                // Mark as synced
                markReceiptTransactionAsSynced(receiptTransaction.id, backendTransactionId)
                
                Result.success(backendTransactionId ?: "")
            } else {
                Result.failure(Exception("Failed to sync to backend: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun convertReceiptToRegularTransaction(receiptTransactionId: Int): Result<Long> {
        return try {
            val receiptTransaction = getReceiptTransactionById(receiptTransactionId)
                ?: return Result.failure(Exception("Receipt transaction not found"))

            // Parse items JSON to List<ReceiptItem>
            val items: List<com.example.financialplannerapp.data.model.ReceiptItem>? = receiptTransaction.items?.let { itemsJson ->
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val type = com.squareup.moshi.Types.newParameterizedType(
                    List::class.java, com.example.financialplannerapp.data.model.ReceiptItem::class.java
                )
                val adapter = moshi.adapter<List<com.example.financialplannerapp.data.model.ReceiptItem>>(type)
                adapter.fromJson(itemsJson)
            }

            // Create regular transaction from receipt data
            val transaction = TransactionEntity(
                userId = receiptTransaction.userId,
                amount = receiptTransaction.totalAmount,
                type = if (receiptTransaction.totalAmount >= 0) "INCOME" else "EXPENSE",
                date = receiptTransaction.date,
                pocket = "Cash", // Default or infer if you have logic
                category = receiptTransaction.category ?: "Other",
                note = receiptTransaction.notes ?: "${receiptTransaction.merchantName} - Receipt Transaction",
                tags = null, // Could parse from items/category if needed
                isFromReceipt = true,
                receiptId = receiptTransaction.receiptId,
                merchantName = receiptTransaction.merchantName,
                location = receiptTransaction.location,
                receiptImagePath = null, // Could store image path if needed
                receiptConfidence = receiptTransaction.confidence,
                receipt_items = items?.map { item ->
                    com.example.financialplannerapp.data.local.model.ReceiptItem(
                        name = item.name,
                        price = item.price,
                        category = item.category ?: "Unknown",
                        quantity = item.quantity
                    )
                },
                isSynced = receiptTransaction.isSynced,
                backendTransactionId = receiptTransaction.backendTransactionId,
                createdAt = receiptTransaction.createdAt,
                updatedAt = receiptTransaction.updatedAt
            )

            val transactionId = transactionDao.insertTransaction(transaction)
            
            // Mark receipt transaction as processed
            markReceiptTransactionAsProcessed(receiptTransactionId)
            
            Result.success(transactionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun inferCategoryFromItems(items: List<com.example.financialplannerapp.data.model.ReceiptItem>): String {
        if (items.isEmpty()) return "General"
        
        // Simple category inference based on item names/categories
        val categories = items.mapNotNull { it.category }.distinct()
        if (categories.isNotEmpty()) {
            return categories.first()
        }
        
        // Fallback to food if no specific category
        return if (items.any { it.name.contains("food", ignoreCase = true) || 
                                it.name.contains("drink", ignoreCase = true) }) {
            "Food & Drink"
        } else {
            "General"
        }
    }

    private fun parseItemsJson(itemsJson: String): List<Map<String, Any>>? {
        return try {
            val listType = Types.newParameterizedType(List::class.java, Map::class.java)
            val adapter = moshi.adapter<List<Map<String, Any>>>(listType)
            adapter.fromJson(itemsJson)
        } catch (e: Exception) {
            null
        }
    }
}
