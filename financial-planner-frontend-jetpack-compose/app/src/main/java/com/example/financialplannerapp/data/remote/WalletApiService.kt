package com.example.financialplannerapp.data.remote

import com.example.financialplannerapp.data.model.WalletData
import retrofit2.Response
import retrofit2.http.*

interface WalletApiService {
    @GET("wallets")
    suspend fun getWallets(): List<WalletData>

    @POST("wallets")
    suspend fun createWallet(@Body wallet: WalletData): WalletData

    @PUT("wallets/{id}")
    suspend fun updateWallet(@Path("id") id: String, @Body wallet: WalletData): WalletData

    @POST("wallets/sync")
    suspend fun syncWallets(@Body wallets: List<WalletData>): Response<List<WalletData>>
}