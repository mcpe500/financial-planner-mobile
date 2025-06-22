package com.example.financialplannerapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ScanReceiptScreen() {
    Column {
        Text(text = "Scan Receipt Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun ScanReceiptScreenPreview() {
    ScanReceiptScreen()
}
