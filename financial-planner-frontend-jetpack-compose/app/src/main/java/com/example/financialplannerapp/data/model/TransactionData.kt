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
