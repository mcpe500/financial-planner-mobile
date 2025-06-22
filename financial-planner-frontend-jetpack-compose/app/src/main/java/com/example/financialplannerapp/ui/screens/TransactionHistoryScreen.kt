package com.example.financialplannerapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TransactionHistoryScreen() {
    Column {
        Text(text = "Transaction History Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionHistoryScreenPreview() {
    TransactionHistoryScreen()
} 