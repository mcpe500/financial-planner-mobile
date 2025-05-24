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
fun HomeScreenContent(
    // Add parameters for ViewModel interactions and navigation later
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
        // TODO: Add more UI elements like summary cards, charts, quick actions etc.
        Text(text = "Welcome to your Financial Planner!")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FinancialPlannerAppTheme {
        HomeScreenContent()
    }
}