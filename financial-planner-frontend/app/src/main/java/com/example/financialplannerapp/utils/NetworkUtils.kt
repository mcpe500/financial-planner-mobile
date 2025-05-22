package com.example.financialplannerapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: SecurityException) {
            Log.e("NetworkUtils", "Permission denied: ${e.message}")
            return false
        } catch (e: NullPointerException) {
            Log.e("NetworkUtils", "Null pointer exception: ${e.message}")
            return false
        } catch (e: ClassCastException) {
            Log.e("NetworkUtils", "Class cast exception: ${e.message}")
            return false
        } catch (e: Throwable) {
            Log.e("NetworkUtils", "Unexpected error: ${e.message}")
            throw e
        }
    }
}