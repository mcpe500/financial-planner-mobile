package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.GoalEntity
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.repository.GoalRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class GoalViewModel(
    private val repository: GoalRepository,
    private val tokenManager: TokenManager,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals: StateFlow<List<GoalEntity>> = _goals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedPriorityFilter = MutableStateFlow<String?>(null)
    val selectedPriorityFilter: StateFlow<String?> = _selectedPriorityFilter.asStateFlow()

    private var currentWalletId: String? = null

    val filteredGoals: StateFlow<List<GoalEntity>> = combine(
        _goals,
        _selectedPriorityFilter
    ) { goals, priorityFilter ->
        when (priorityFilter) {
            null -> goals
            else -> goals.filter { it.priority.equals(priorityFilter, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun loadGoals(walletId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            currentWalletId = walletId
            try {
                val user_email = tokenManager.getUserEmail() ?: "guest"
                repository.getGoalsForWallet(walletId, user_email).collect { goalList ->
                    _goals.value = goalList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load goals: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun loadAllGoals() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user_email = tokenManager.getUserEmail() ?: "guest"
                repository.getAllGoalsByUser(user_email).collect { goalList ->
                    _goals.value = goalList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load goals: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun refreshGoals() {
        currentWalletId?.let { walletId ->
            loadGoals(walletId)
        } ?: loadAllGoals()
    }

    fun setPriorityFilter(priority: String?) {
        _selectedPriorityFilter.value = priority
    }

    fun addGoal(
        walletId: String,
        name: String,
        targetAmount: Double,
        currentAmount: Double,
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
                    priority = priority
                )
                repository.insertGoal(goal)
                // Refresh goals after adding
                refreshGoals()
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
                // Refresh goals after deleting
                refreshGoals()
            } catch (e: Exception) {
                _error.value = "Failed to delete goal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editGoal(goal: GoalEntity, newName: String, newTarget: Double, newPriority: String) {
        viewModelScope.launch {
            try {
                val updatedGoal = goal.copy(name = newName, targetAmount = newTarget, priority = newPriority)
                repository.updateGoal(updatedGoal)
                // Refresh goals after editing
                refreshGoals()
            } catch (e: Exception) {
                _error.value = "Failed to edit goal: ${e.message}"
            }
        }
    }

    fun editGoalWithWalletAdjustment(goal: GoalEntity, wallet: WalletEntity, newName: String, newTarget: Double, newPriority: String) {
        viewModelScope.launch {
            try {
                // If new target is smaller than current amount, return excess to wallet
                val excessAmount = if (newTarget < goal.currentAmount) {
                    goal.currentAmount - newTarget
                } else {
                    0.0
                }
                
                val adjustedCurrentAmount = if (excessAmount > 0) {
                    newTarget
                } else {
                    goal.currentAmount
                }
                
                val updatedGoal = goal.copy(
                    name = newName, 
                    targetAmount = newTarget, 
                    currentAmount = adjustedCurrentAmount,
                    priority = newPriority
                )
                
                val updatedWallet = wallet.copy(balance = wallet.balance + excessAmount)
                
                repository.updateGoal(updatedGoal)
                walletRepository.updateWallet(updatedWallet)
                
                // Refresh goals after editing
                refreshGoals()
            } catch (e: Exception) {
                _error.value = "Failed to edit goal: ${e.message}"
            }
        }
    }

    fun addToGoalAmountWithValidation(goal: GoalEntity, wallet: WalletEntity, amount: Double) {
        viewModelScope.launch {
            try {
                if (wallet.balance >= amount) {
                    val neededAmount = goal.targetAmount - goal.currentAmount
                    val actualAmount = if (amount > neededAmount) neededAmount else amount
                    
                    val updatedGoal = goal.copy(currentAmount = goal.currentAmount + actualAmount)
                    val updatedWallet = wallet.copy(balance = wallet.balance - actualAmount)
                    
                    repository.updateGoal(updatedGoal)
                    walletRepository.updateWallet(updatedWallet)
                    
                    // Refresh goals after updating
                    refreshGoals()
                }
            } catch (e: Exception) {
                _error.value = "Failed to add to goal amount: ${e.message}"
            }
        }
    }

    fun deleteGoalAndReturnToWallet(goal: GoalEntity, wallet: WalletEntity) {
        viewModelScope.launch {
            try {
                val updatedWallet = wallet.copy(balance = wallet.balance + goal.currentAmount)
                walletRepository.updateWallet(updatedWallet)
                repository.deleteGoal(goal.id)
                // Refresh goals after deleting
                refreshGoals()
            } catch (e: Exception) {
                _error.value = "Failed to delete goal: ${e.message}"
            }
        }
    }

    fun addToGoalAmount(goal: GoalEntity, wallet: WalletEntity, amount: Double) {
        viewModelScope.launch {
            try {
                if (wallet.balance >= amount) {
                    val updatedGoal = goal.copy(currentAmount = goal.currentAmount + amount)
                    val updatedWallet = wallet.copy(balance = wallet.balance - amount)
                    repository.updateGoal(updatedGoal)
                    walletRepository.updateWallet(updatedWallet)
                    // Refresh goals after updating
                    refreshGoals()
                }
            } catch (e: Exception) {
                _error.value = "Failed to add to goal amount: ${e.message}"
            }
        }
    }

    fun subtractFromGoalAmount(goal: GoalEntity, wallet: WalletEntity, amount: Double) {
        viewModelScope.launch {
            try {
                if (goal.currentAmount >= amount) {
                    val updatedGoal = goal.copy(currentAmount = goal.currentAmount - amount)
                    val updatedWallet = wallet.copy(balance = wallet.balance + amount)
                    repository.updateGoal(updatedGoal)
                    walletRepository.updateWallet(updatedWallet)
                    // Refresh goals after updating
                    refreshGoals()
                }
            } catch (e: Exception) {
                _error.value = "Failed to subtract from goal amount: ${e.message}"
            }
        }
    }

    fun completeGoal(goal: GoalEntity) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId() ?: "local_user"
                
                // Create expense transaction for the goal completion
                val transaction = TransactionEntity(
                    userId = userId,
                    amount = goal.targetAmount,
                    type = "EXPENSE",
                    date = Date(),
                    pocket = "",
                    category = "",
                    note = "Goal completed: ${goal.name}",
                    tags = listOf("goal", "completed"),
                    isFromReceipt = false,
                    walletId = goal.walletId
                )
                
                // Insert the transaction (no wallet balance deduction needed)
                transactionRepository.insertTransaction(transaction)
                
                // Delete the goal since it's completed
                repository.deleteGoal(goal.id)
                
                // Refresh goals after completing
                refreshGoals()
            } catch (e: Exception) {
                _error.value = "Failed to complete goal: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
