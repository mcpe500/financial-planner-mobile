package com.example.financialplannerapp.config

object Config {
    // Base URL for the API - update this to match your backend server
    const val BASE_URL = "http://banachs.duckdns.org:9001" // For Android emulator
    // const val BASE_URL = "http://10.0.2.2:3000" // For Android emulator
    // const val BASE_URL = "http://localhost:3000" // For local development
    // const val BASE_URL = "https://your-production-server.com" // For production
    
    // API endpoints
    const val AUTH_LOGIN = "$BASE_URL/api/auth/login"
    const val AUTH_REGISTER = "$BASE_URL/api/auth/register"
    const val PROFILE_UPDATE = "$BASE_URL/api/profile/update"
    const val PROFILE_GET = "$BASE_URL/api/profile"
    
    // Request timeouts
    const val CONNECT_TIMEOUT = 10000L
    const val READ_TIMEOUT = 10000L
}