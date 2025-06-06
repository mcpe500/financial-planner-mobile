package com.example.financialplannerapp.data.model

import java.util.Date

data class TransactionData(
    val id: Int,
    val amount: Double,
    val date: Date,
    val description: String
)
