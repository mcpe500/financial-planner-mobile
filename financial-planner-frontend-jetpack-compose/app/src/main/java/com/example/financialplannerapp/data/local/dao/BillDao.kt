package com.example.financialplannerapp.data.local.dao

import androidx.room.*
import com.example.financialplannerapp.data.local.model.BillEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE uuid = :uuid")
    suspend fun getBillByUuid(uuid: String): BillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<BillEntity>): List<Long>

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("DELETE FROM bills WHERE uuid = :uuid")
    suspend fun deleteBillByUuid(uuid: String)

    @Query("DELETE FROM bills")
    suspend fun deleteAllBills()
} 