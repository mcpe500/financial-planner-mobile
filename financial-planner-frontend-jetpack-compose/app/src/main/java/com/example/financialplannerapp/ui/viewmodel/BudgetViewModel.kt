package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.BudgetEntity
import com.example.financialplannerapp.data.repository.BudgetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class BudgetViewModel(
    private val repository: BudgetRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _budgets = MutableStateFlow<List<BudgetEntity>>(emptyList())
    val budgets: StateFlow<List<BudgetEntity>> = _budgets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBudgets(walletId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userEmail = tokenManager.getUserEmail() ?: "guest"
                repository.getBudgetsForWallet(walletId).collect { budgetList ->
                    // Filter budgets by user email
                    val userBudgets = budgetList.filter { it.user_email == userEmail }
                    _budgets.value = userBudgets
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Failed to load budgets: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun addBudget(
        walletId: String,
        name: String,
        amount: Double,
        category: String,
        startDate: Date,
        endDate: Date,
        isRecurring: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userEmail = tokenManager.getUserEmail() ?: "guest"

                // Optional: Check if a budget with the same name already exists for this user and wallet
                // Remove this check if you want to allow multiple budgets per wallet
                /*
                val existingBudgets = repository.getBudgetsForWallet(walletId).firstOrNull() ?: emptyList()
                val existing = existingBudgets.find {
                    it.user_email == userEmail && it.name.equals(name, ignoreCase = true)
                }
                if (existing != null) {
                    _error.value = "A budget with this name already exists for this wallet."
                    _isLoading.value = false
                    return@launch
                }
                */

                val budget = BudgetEntity(
                    walletId = walletId,
                    user_email = userEmail,
                    name = name,
                    amount = amount,
                    category = category,
                    startDate = startDate,
                    endDate = endDate,
                    isRecurring = isRecurring
                )
                repository.insertBudget(budget)

                // Refresh the budgets list after adding
                loadBudgets(walletId)

            } catch (e: Exception) {
                _error.value = "Failed to add budget: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editBudget(
        budgetId: Int,
        name: String,
        amount: Double,
        category: String,
        startDate: Date,
        endDate: Date,
        isRecurring: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val budget = _budgets.value.find { it.id == budgetId }
                if (budget != null) {
                    val updated = budget.copy(
                        name = name,
                        amount = amount,
                        category = category,
                        startDate = startDate,
                        endDate = endDate,
                        isRecurring = isRecurring
                    )
                    repository.updateBudget(updated)

                    // Refresh the budgets list after editing
                    loadBudgets(budget.walletId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to edit budget: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBudget(budgetId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val budget = _budgets.value.find { it.id == budgetId }
                if (budget != null) {
                    repository.deleteBudget(budgetId)

                    // Refresh the budgets list after deleting
                    loadBudgets(budget.walletId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete budget: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}