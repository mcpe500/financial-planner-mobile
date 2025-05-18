package com.example.financialplannerapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.db.UserProfileDao
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val tokenManager: TokenManager,
    private val userProfileDao: UserProfileDao
) : ViewModel() {

    private val _userName = MutableLiveData("Guest")
    val userName: LiveData<String> = _userName

    fun loadUserName() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: "guest_user"
            val profile = userProfileDao.getUserProfile(userId)
            _userName.value = profile?.name ?: "Guest"
        }
    }
}
