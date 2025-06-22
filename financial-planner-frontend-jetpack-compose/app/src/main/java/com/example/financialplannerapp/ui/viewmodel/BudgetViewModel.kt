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
            repository.getBudgetsForWallet(walletId).collect { budgetList ->
                _budgets.value = budgetList
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
            try {
                val userEmail = tokenManager.getUserEmail() ?: "guest"
                val budget = BudgetEntity(
                    walletId = walletId,
                    userEmail = userEmail,
                    name = name,
                    amount = amount,
                    category = category,
                    startDate = startDate,
                    endDate = endDate,
                    isRecurring = isRecurring
                )
                repository.insertBudget(budget)
            } catch (e: Exception) {
                _error.value = "Failed to add budget: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBudget(budgetId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteBudget(budgetId)
            } catch (e: Exception) {
                _error.value = "Failed to delete budget: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 