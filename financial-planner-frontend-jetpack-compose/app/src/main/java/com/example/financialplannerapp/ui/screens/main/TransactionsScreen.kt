package com.example.financialplannerapp.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme

@Composable
fun TransactionsScreenContent(
    // Add parameters for ViewModel interactions and navigation later
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Transactions Screen", style = MaterialTheme.typography.headlineMedium)
        // TODO: Add transaction list, add transaction button, filters, search, etc.
        Text(text = "View and manage your transactions here")
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionsScreenPreview() {
    FinancialPlannerAppTheme {
        TransactionsScreenContent()
    }
}