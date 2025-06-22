package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.WalletDao
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.model.WalletData
import com.example.financialplannerapp.data.remote.WalletApiService
import kotlinx.coroutines.flow.Flow
import com.example.financialplannerapp.data.model.toWalletData
import com.example.financialplannerapp.data.model.toEntity

class WalletRepositoryImpl(
    private val walletDao: WalletDao,
    private val walletApiService: WalletApiService
) : WalletRepository {

    override suspend fun getWalletsFromBackend(): List<WalletData> {
        return try {
            walletApiService.getWallets()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun uploadWalletsToBackend(wallets: List<WalletData>): Boolean {
        return try {
            val response = walletApiService.syncWallets(wallets)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun insertWallets(wallets: List<WalletEntity>) {
        walletDao.insertAll(wallets)
    }

    override suspend fun insertWallet(wallet: WalletEntity): Long {
        return try {
            // Try to save to backend first
            val backendWallet = walletApiService.createWallet(wallet.toWalletData())
            // Insert backend wallet (with backend id) to RoomDB
            walletDao.insertWallet(backendWallet.toEntity())
        } catch (e: Exception) {
            // If backend fails, insert locally only
            walletDao.insertWallet(wallet)
        }
    }

    override suspend fun updateWallet(wallet: WalletEntity) {
        walletDao.updateWallet(wallet)
    }

    override fun getWalletsByUserEmail(email: String): Flow<List<WalletEntity>> {
        return walletDao.getWalletsByUserEmail(email)
    }
}
