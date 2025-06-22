package com.example.financialplannerapp.data.model

import java.util.Date

data class TransactionData(
    val id: Int,
    val amount: Double,
    val date: Date,
    val description: String
)

data class TransactionPayload(
    val amount: Double,
    val type: String, // "expense" atau "income"
    val category: String,
    val description: String?,
    val date: String, // ISO string
    val merchant_name: String? = null,
    val location: String? = null,
    val receipt_id: String? = null,
    val items: List<Any>? = null,
    val notes: String? = null
)

data class TransactionTag(
    val id: String,
    val name: String,
    val color: String? = null,
    val isDefault: Boolean = false
)

object TransactionTags {
    val defaultTags = listOf(
        TransactionTag("1", "Food", "#FF6B6B", true),
        TransactionTag("2", "Transport", "#4ECDC4", true),
        TransactionTag("3", "Shopping", "#45B7D1", true),
        TransactionTag("4", "Bills", "#96CEB4", true),
        TransactionTag("5", "Entertainment", "#FFEAA7", true),
        TransactionTag("6", "Health", "#DDA0DD", true),
        TransactionTag("7", "Education", "#98D8C8", true),
        TransactionTag("8", "Travel", "#F7DC6F", true),
        TransactionTag("9", "Salary", "#52C41A", true),
        TransactionTag("10", "Investment", "#1890FF", true),
        TransactionTag("11", "Gift", "#722ED1", true),
        TransactionTag("12", "Other", "#8C8C8C", true)
    )
    
    fun getTagsByType(type: String): List<TransactionTag> {
        return when (type.uppercase()) {
            "INCOME" -> defaultTags.filter { it.name in listOf("Salary", "Investment", "Gift", "Other") }
            "EXPENSE" -> defaultTags.filter { it.name in listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Education", "Travel", "Other") }
            else -> defaultTags
        }
    }
}
