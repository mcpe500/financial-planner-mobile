package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.GoalEntity
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.repository.GoalRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class GoalViewModel(
    private val repository: GoalRepository,
    private val tokenManager: TokenManager,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals: StateFlow<List<GoalEntity>> = _goals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadGoals(walletId: String) {
        viewModelScope.launch {
            val user_email = tokenManager.getUserEmail() ?: "guest"
            repository.getGoalsForWallet(walletId, user_email).collect { goalList ->
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
                val user_email = tokenManager.getUserEmail() ?: "guest"
                val goal = GoalEntity(
                    walletId = walletId,
                    user_email = user_email,
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

    fun editGoal(goal: GoalEntity, newName: String, newTarget: Double, newPriority: String) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(name = newName, targetAmount = newTarget, priority = newPriority)
            repository.updateGoal(updatedGoal)
        }
    }

    fun deleteGoalAndReturnToWallet(goal: GoalEntity, wallet: WalletEntity) {
        viewModelScope.launch {
            val updatedWallet = wallet.copy(balance = wallet.balance + goal.currentAmount)
            walletRepository.updateWallet(updatedWallet)
            repository.deleteGoal(goal.id)
        }
    }

    fun addToGoalAmount(goal: GoalEntity, wallet: WalletEntity, amount: Double) {
        viewModelScope.launch {
            if (wallet.balance >= amount) {
                val updatedGoal = goal.copy(currentAmount = goal.currentAmount + amount)
                val updatedWallet = wallet.copy(balance = wallet.balance - amount)
                repository.updateGoal(updatedGoal)
                walletRepository.updateWallet(updatedWallet)
            }
        }
    }

    fun subtractFromGoalAmount(goal: GoalEntity, wallet: WalletEntity, amount: Double) {
        viewModelScope.launch {
            if (goal.currentAmount >= amount) {
                val updatedGoal = goal.copy(currentAmount = goal.currentAmount - amount)
                val updatedWallet = wallet.copy(balance = wallet.balance + amount)
                repository.updateGoal(updatedGoal)
                walletRepository.updateWallet(updatedWallet)
            }
        }
    }
}
