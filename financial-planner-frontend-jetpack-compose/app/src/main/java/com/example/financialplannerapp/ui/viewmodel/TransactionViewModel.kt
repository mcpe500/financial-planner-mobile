package com.example.financialplannerapp.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = mutableStateOf(TransactionState())
    val state: State<TransactionState> = _state

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedDateRange = MutableStateFlow(DateRange.ThisMonth)
    val selectedDateRange = _selectedDateRange.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType = _selectedType.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedPocket = MutableStateFlow<String?>(null)
    val selectedPocket = _selectedPocket.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            combine(
                searchQuery,
                selectedDateRange,
                selectedType,
                selectedCategory,
                selectedPocket
            ) { query, dateRange, type, category, pocket ->
                FilterParams(query, dateRange, type, category, pocket)
            }.flatMapLatest { params ->
                getFilteredTransactions(params)
            }.collect { transactions ->
                _state.value = _state.value.copy(
                    transactions = transactions,
                    isLoading = false
                )
            }
        }
    }

    private fun getFilteredTransactions(params: FilterParams): Flow<List<TransactionEntity>> {
        return transactionRepository.getTransactionsByUserId(getCurrentUserId()).map { transactions ->
            transactions.filter { transaction ->
                val matchesSearch = params.query.isEmpty() || 
                    transaction.note?.contains(params.query, ignoreCase = true) == true ||
                    transaction.merchantName?.contains(params.query, ignoreCase = true) == true
                
                val matchesDateRange = when (params.dateRange) {
                    DateRange.Today -> isToday(transaction.date)
                    DateRange.ThisWeek -> isThisWeek(transaction.date)
                    DateRange.ThisMonth -> isThisMonth(transaction.date)
                    DateRange.ThisYear -> isThisYear(transaction.date)
                    DateRange.All -> true
                }

                val matchesType = params.type == null || transaction.type.equals(params.type, ignoreCase = true)
                val matchesCategory = params.category == null || transaction.category == params.category
                // pocket is not in TransactionEntity, so skip matchesPocket

                matchesSearch && matchesDateRange && matchesType && matchesCategory
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDateRange(range: DateRange) {
        _selectedDateRange.value = range
    }

    fun setType(type: String?) {
        _selectedType.value = type
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setPocket(pocket: String?) {
        _selectedPocket.value = pocket
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(transaction)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                transactionRepository.updateTransaction(transaction)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun getCurrentUserId(): String {
        // TODO: Get from UserManager or similar
        return "current_user_id"
    }

    private fun isToday(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        return isSameDay(date, today)
    }

    private fun isThisWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        val thisWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        calendar.time = date
        return calendar.get(Calendar.WEEK_OF_YEAR) == thisWeek
    }

    private fun isThisMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val thisMonth = calendar.get(Calendar.MONTH)
        calendar.time = date
        return calendar.get(Calendar.MONTH) == thisMonth
    }

    private fun isThisYear(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val thisYear = calendar.get(Calendar.YEAR)
        calendar.time = date
        return calendar.get(Calendar.YEAR) == thisYear
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}

data class TransactionState(
    val transactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class FilterParams(
    val query: String = "",
    val dateRange: DateRange = DateRange.ThisMonth,
    val type: String? = null,
    val category: String? = null,
    val pocket: String? = null
)

enum class DateRange {
    Today,
    ThisWeek,
    ThisMonth,
    ThisYear,
    All
}