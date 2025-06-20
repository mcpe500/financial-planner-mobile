package com.example.financialplannerapp.data.remote

import com.example.financialplannerapp.data.local.Transaction
import retrofit2.http.*

interface TransactionApi {

    @GET("transactions")
    suspend fun getUserTransactions(@Query("userId") userId: String): List<Transaction>

    @GET("transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Transaction

    @POST("transactions")
    suspend fun createTransaction(@Body transaction: Transaction): Transaction

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body transaction: Transaction
    ): Transaction

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String)
}