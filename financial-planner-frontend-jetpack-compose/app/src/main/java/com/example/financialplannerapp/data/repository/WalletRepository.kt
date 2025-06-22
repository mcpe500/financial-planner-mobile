package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.WalletDao
import com.example.financialplannerapp.data.local.model.WalletEntity
import kotlinx.coroutines.flow.Flow

// WalletRepository.kt
interface WalletRepository {
    fun getWalletsByUserEmail(userEmail: String): Flow<List<WalletEntity>>
    fun getAllWallets(): Flow<List<WalletEntity>>
    suspend fun getWalletById(walletId: String): WalletEntity?
    suspend fun insertWallet(wallet: WalletEntity): Long
    suspend fun updateWallet(wallet: WalletEntity)
}