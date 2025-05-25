package com.example.financialplannerapp.api

import com.example.financialplannerapp.config.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(Config.REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(Config.REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(Config.REQUEST_TIMEOUT, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Config.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
}