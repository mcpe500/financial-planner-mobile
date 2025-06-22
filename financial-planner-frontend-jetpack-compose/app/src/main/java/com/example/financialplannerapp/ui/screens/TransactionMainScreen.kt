package com.example.financialplannerapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TransactionMainScreen() {
    Column {
        Text(text = "Transaction Main Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionMainScreenPreview() {
    TransactionMainScreen()
} 