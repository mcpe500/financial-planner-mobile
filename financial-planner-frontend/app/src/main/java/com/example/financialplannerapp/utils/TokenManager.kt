package com.example.financialplannerapp.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("FinancialPlannerPrefs", Context.MODE_PRIVATE)
    private val TOKEN_KEY = "auth_token"
    private val NO_ACCOUNT_MODE = "no_account_mode"

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

    // Clear all preferences
    fun clear() {
        prefs.edit().clear().apply()
    }
}