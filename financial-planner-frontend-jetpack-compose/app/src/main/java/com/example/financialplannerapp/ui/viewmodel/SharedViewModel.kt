package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _data = mutableListOf<String>()
    val data: List<String> get() = _data

    fun addData(item: String) {
        viewModelScope.launch {
            _data.add(item)
        }
    }

    fun removeData(item: String) {
        viewModelScope.launch {
            _data.remove(item)
        }
    }
}