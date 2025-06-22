package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.local.model.GoalEntity
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.repository.BillRepository
import com.example.financialplannerapp.data.repository.GoalRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class DashboardState(
    val transactions: List<TransactionEntity> = emptyList(),
    val goals: List<GoalEntity> = emptyList(),
    val bills: List<BillEntity> = emptyList(),
    val wallets: List<WalletEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class DashboardViewModel(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val billRepository: BillRepository,
    private val walletRepository: WalletRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val userId = tokenManager.getUserId() ?: "local_user"
                val userEmail = tokenManager.getUserEmail() ?: "guest"

                // Load all data in parallel
                val transactionsFlow = transactionRepository.getTransactionsByUserId(userId)
                val goalsFlow = goalRepository.getAllGoalsByUser(userEmail)
                val billsFlow = billRepository.getAllBills()
                val walletsFlow = walletRepository.getWalletsByUserEmail(userEmail)

                // Combine all flows
                combine(
                    transactionsFlow,
                    goalsFlow,
                    billsFlow,
                    walletsFlow
                ) { transactions, goals, bills, wallets ->
                    DashboardState(
                        transactions = transactions,
                        goals = goals,
                        bills = bills,
                        wallets = wallets,
                        isLoading = false,
                        error = null
                    )
                }.collect { dashboardState ->
                    _state.value = dashboardState
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load dashboard data: ${e.message}"
                )
            }
        }
    }

    // Get recent transactions (5 latest)
    fun getRecentTransactions(): List<TransactionEntity> {
        return state.value.transactions
            .sortedByDescending { it.date }
            .take(5)
    }

    // Get high priority goals
    fun getHighPriorityGoals(): List<GoalEntity> {
        return state.value.goals
            .filter { it.priority.equals("High", true) }
            .sortedByDescending { it.currentAmount / it.targetAmount }
            .take(3)
    }

    // Get upcoming bills (due soon or overdue)
    fun getUpcomingBills(): List<BillEntity> {
        val now = Calendar.getInstance()
        return state.value.bills
            .filter { bill ->
                val dueDate = Calendar.getInstance().apply { time = bill.dueDate }
                val diffInMillis = dueDate.timeInMillis - now.timeInMillis
                val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)
                diffInDays <= 7 // Show bills due within 7 days or overdue
            }
            .sortedBy { it.dueDate }
            .take(3)
    }

    // Calculate monthly income
    fun getMonthlyIncome(): Double {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        return state.value.transactions
            .filter { transaction ->
                calendar.time = transaction.date
                transaction.type.equals("INCOME", true) &&
                calendar.get(Calendar.MONTH) == currentMonth &&
                calendar.get(Calendar.YEAR) == currentYear
            }
            .sumOf { it.amount }
    }

    // Calculate monthly expenses
    fun getMonthlyExpenses(): Double {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        return state.value.transactions
            .filter { transaction ->
                calendar.time = transaction.date
                transaction.type.equals("EXPENSE", true) &&
                calendar.get(Calendar.MONTH) == currentMonth &&
                calendar.get(Calendar.YEAR) == currentYear
            }
            .sumOf { it.amount }
    }

    // Get total wallet balance
    fun getTotalWalletBalance(): Double {
        return state.value.wallets.sumOf { it.balance }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
} 