package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.model.RecurringBill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class CalendarViewModel : ViewModel() {

    private val _currentMonth = MutableStateFlow(Calendar.getInstance())
    val currentMonth: StateFlow<Calendar> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _bills = MutableStateFlow<List<RecurringBill>>(emptyList())
    val bills: StateFlow<List<RecurringBill>> = _bills.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setSelectedDate(date: Date?) {
        _selectedDate.value = date
    }

    fun setCurrentMonth(month: Calendar) {
        _currentMonth.value = month
    }

    fun navigateToNextMonth() {
        viewModelScope.launch {
            val nextMonth = Calendar.getInstance().apply {
                time = _currentMonth.value.time
                add(Calendar.MONTH, 1)
            }
            _currentMonth.value = nextMonth
        }
    }

    fun navigateToPreviousMonth() {
        viewModelScope.launch {
            val prevMonth = Calendar.getInstance().apply {
                time = _currentMonth.value.time
                add(Calendar.MONTH, -1)
            }
            _currentMonth.value = prevMonth
        }
    }

    fun loadBills() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Replace with actual repository call
                _bills.value = getMockBills()
            } catch (e: Exception) {
                _error.value = "Failed to load bills: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getMockBills(): List<RecurringBill> {
        val currentMonth = _currentMonth.value
        return listOf(
            RecurringBill(
                id = "1",
                name = "Listrik PLN",
                estimatedAmount = 450000.0,
                dueDate = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 5)
                    set(Calendar.MONTH, currentMonth.get(Calendar.MONTH))
                }.time,
                repeatCycle = "MONTHLY"
            ),
            RecurringBill(
                id = "2",
                name = "Internet IndiHome",
                estimatedAmount = 350000.0,
                dueDate = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 10)
                    set(Calendar.MONTH, currentMonth.get(Calendar.MONTH))
                }.time,
                repeatCycle = "MONTHLY"
            ),
            RecurringBill(
                id = "3",
                name = "BPJS Kesehatan",
                estimatedAmount = 150000.0,
                dueDate = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 15)
                    set(Calendar.MONTH, currentMonth.get(Calendar.MONTH))
                }.time,
                repeatCycle = "MONTHLY"
            )
        )
    }

    fun clearError() {
        _error.value = null
    }
}
