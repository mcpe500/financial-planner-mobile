package com.example.financialplannerapp.data.local

import androidx.room.*
import java.util.Date

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): Transaction?

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllByUser(userId: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND syncStatus = 'pending'")
    suspend fun getPendingSync(userId: String): List<Transaction>

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)
}