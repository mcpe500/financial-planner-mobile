package com.example.financialplannerapp.data.remote

import com.example.financialplannerapp.config.Config
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import com.example.financialplannerapp.TokenManager
import android.content.Context

object RetrofitClient {
    
    // Timeout constants
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    // Moshi instance
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    // TokenManager instance (must be set by the app)
    private var tokenManager: TokenManager? = null

    fun setTokenManager(manager: TokenManager) {
        tokenManager = manager
    }
    
    // OkHttp client with timeouts, logging, and auth
    fun getOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()
                val manager = tokenManager ?: TokenManager(context)
                val token = manager.getToken()
                if (!token.isNullOrEmpty()) {
                    builder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(builder.build())
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    // Retrofit instance (must be created with context)
    fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Config.BASE_URL + "/")
            .client(getOkHttpClient(context))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    // API service instance (must be created with context)
    fun getApiService(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }
    
    fun getAccountService(context: Context): AccountService {
        return getRetrofit(context).create(AccountService::class.java)
    }
}