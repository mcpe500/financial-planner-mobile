package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.WalletDao
import com.example.financialplannerapp.data.local.model.WalletEntity
import kotlinx.coroutines.flow.Flow

class WalletRepositoryImpl(private val walletDao: WalletDao) : WalletRepository {
    override fun getWalletsByUserEmail(userEmail: String): Flow<List<WalletEntity>> {
        return walletDao.getWalletsByUserEmail(userEmail)
    }

    override fun getAllWallets(): Flow<List<WalletEntity>> {
        return walletDao.getAllWallets()
    }

    override suspend fun getWalletById(walletId: String): WalletEntity? {
        return walletDao.getWalletById(walletId)
    }

    override suspend fun insertWallet(wallet: WalletEntity):Long {
        return walletDao.insertWallet(wallet)
    }

    override suspend fun updateWallet(wallet: WalletEntity) {
        walletDao.updateWallet(wallet)
    }
}
