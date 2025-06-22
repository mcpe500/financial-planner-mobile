package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.model.WalletData
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    suspend fun getWalletsFromBackend(): List<WalletData>
    suspend fun uploadWalletsToBackend(wallets: List<WalletData>): Boolean
    suspend fun insertWallets(wallets: List<WalletEntity>)
    suspend fun insertWallet(wallet: WalletEntity): Long
    suspend fun updateWallet(wallet: WalletEntity)
    fun getWalletsByUserEmail(email: String): Flow<List<WalletEntity>>
}