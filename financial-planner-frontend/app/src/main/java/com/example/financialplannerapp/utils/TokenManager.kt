package com.example.financialplannerapp.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "FinancialPlannerPrefs", Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun getAuthHeader(): String? {
        return getToken()?.let { "Bearer $it" }
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
    }
}