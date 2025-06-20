package com.example.financialplannerapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.financialplannerapp.data.Converters
import java.util.Date

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String, // UUID dari RecurringBill
    val name: String,
    val estimatedAmount: Double,
    val dueDate: Date,
    val repeatCycle: String,
    val category: String?,
    val notes: String,
    val isActive: Boolean,
    val paymentsJson: String, // List<BillPayment> dalam bentuk JSON
    val autoPay: Boolean,
    val notificationEnabled: Boolean,
    val lastPaymentDate: Date?,
    val creationDate: Date
) 