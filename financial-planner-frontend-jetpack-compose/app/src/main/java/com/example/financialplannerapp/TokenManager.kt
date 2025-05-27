package com.example.financialplannerapp

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("FinancialPlannerPrefs", Context.MODE_PRIVATE)
    private val TOKEN_KEY = "auth_token"
    private val NO_ACCOUNT_MODE = "no_account_mode"
    private val USER_ID_KEY = "user_id"
    private val USER_EMAIL_KEY = "user_email"
    private val USER_NAME_KEY = "user_name"

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    fun setNoAccountMode(enabled: Boolean) {
        prefs.edit().putBoolean(NO_ACCOUNT_MODE, enabled).apply()
    }

    fun isNoAccountMode(): Boolean {
        return prefs.getBoolean(NO_ACCOUNT_MODE, false)
    }

    fun getAuthHeader(): String? {
        val token = getToken()
        return if (token != null) "Bearer $token" else null
    }
    
    fun saveUserInfo(userId: String, email: String?, name: String?) {
        prefs.edit()
            .putString(USER_ID_KEY, userId)
            .putString(USER_EMAIL_KEY, email)
            .putString(USER_NAME_KEY, name)
            .apply()
    }
    
    fun getUserId(): String? {
        return prefs.getString(USER_ID_KEY, null)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL_KEY, null)
    }
    
    fun getUserName(): String? {
        return prefs.getString(USER_NAME_KEY, null)
    }

    // Check if user is fully authenticated (has token and user info)
    fun isAuthenticated(): Boolean {
        return getToken() != null && getUserId() != null
    }

    // Clear all preferences
    fun clear() {
        prefs.edit().clear().apply()
    }
}
