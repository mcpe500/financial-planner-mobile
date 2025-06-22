package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.BillDao
import com.example.financialplannerapp.data.local.model.BillEntity
import kotlinx.coroutines.flow.Flow

class BillRepositoryImpl(private val billDao: BillDao) : BillRepository {
    override fun getAllBills(): Flow<List<BillEntity>> = billDao.getAllBills()
    override suspend fun getBillByUuid(uuid: String): BillEntity? = billDao.getBillByUuid(uuid)
    override suspend fun insertBill(bill: BillEntity): Long = billDao.insertBill(bill)
    override suspend fun updateBill(bill: BillEntity) = billDao.updateBill(bill)
    override suspend fun deleteBill(bill: BillEntity) = billDao.deleteBill(bill)
    override suspend fun deleteBillByUuid(uuid: String) = billDao.deleteBillByUuid(uuid)
    override suspend fun deleteAllBills() = billDao.deleteAllBills()
}