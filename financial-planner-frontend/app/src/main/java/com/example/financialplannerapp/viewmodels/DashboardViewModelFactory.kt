package com.example.financialplannerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialplannerapp.db.UserProfileDao
import com.example.financialplannerapp.utils.TokenManager

class DashboardViewModelFactory(
    private val tokenManager: TokenManager,
    private val userProfileDao: UserProfileDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(tokenManager, userProfileDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
