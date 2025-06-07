package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PasscodeViewModel : ViewModel() {

    private var passcode: String? = null

    fun setPasscode(newPasscode: String) {
        viewModelScope.launch {
            passcode = newPasscode
        }
    }

    fun getPasscode(): String? {
        return passcode
    }
}