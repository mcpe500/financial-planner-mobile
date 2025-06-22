package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.GoalEntity
import com.example.financialplannerapp.data.repository.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class GoalViewModel(
    private val repository: GoalRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals: StateFlow<List<GoalEntity>> = _goals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadGoals(walletId: String) {
        viewModelScope.launch {
            repository.getGoalsForWallet(walletId).collect { goalList ->
                _goals.value = goalList
            }
        }
    }

    fun addGoal(
        walletId: String,
        name: String,
        targetAmount: Double,
        currentAmount: Double,
        targetDate: Date,
        priority: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userEmail = tokenManager.getUserEmail() ?: "guest"
                val goal = GoalEntity(
                    walletId = walletId,
                    userEmail = userEmail,
                    name = name,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount,
                    targetDate = targetDate,
                    priority = priority
                )
                repository.insertGoal(goal)
            } catch (e: Exception) {
                _error.value = "Failed to add goal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteGoal(goalId)
            } catch (e: Exception) {
                _error.value = "Failed to delete goal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
