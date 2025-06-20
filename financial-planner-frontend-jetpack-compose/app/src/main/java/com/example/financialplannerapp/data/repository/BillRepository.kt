 package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.BillEntity
import kotlinx.coroutines.flow.Flow

interface BillRepository {
    fun getAllBills(): Flow<List<BillEntity>>
    suspend fun getBillById(id: Int): BillEntity?
    suspend fun insertBill(bill: BillEntity): Long
    suspend fun updateBill(bill: BillEntity)
    suspend fun deleteBill(bill: BillEntity)
    suspend fun deleteAllBills()
}