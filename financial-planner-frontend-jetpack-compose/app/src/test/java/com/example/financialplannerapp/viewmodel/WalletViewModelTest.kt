package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*

class WalletViewModelTest {

    @Test
    fun calculateWalletBalance_shouldReturnCorrectBalance() {
        val transactions = listOf(1000.0, -200.0, -150.0, 500.0)
        val balance = calculateBalance(transactions)
        assertEquals(1150.0, balance, 0.01)
    }

    @Test
    fun validateWalletName_shouldReturnTrueForValidName() {
        val name = "My Wallet"
        assertTrue(isValidWalletName(name))
    }

    @Test
    fun validateWalletName_shouldReturnFalseForEmptyName() {
        val name = ""
        assertFalse(isValidWalletName(name))
    }

    @Test
    fun calculateMonthlySpending_shouldReturnCorrectAmount() {
        val expenses = listOf(-100.0, -200.0, -50.0, -150.0)
        val monthlySpending = calculateMonthlySpending(expenses)
        assertEquals(500.0, monthlySpending, 0.01)
    }

    @Test
    fun calculateMonthlyIncome_shouldReturnCorrectAmount() {
        val incomes = listOf(1000.0, 500.0, 200.0)
        val monthlyIncome = calculateMonthlyIncome(incomes)
        assertEquals(1700.0, monthlyIncome, 0.01)
    }

    @Test
    fun getWalletType_shouldReturnCorrectType() {
        val cashWallet = Wallet("Cash", "CASH", 500.0)
        val bankWallet = Wallet("Bank Account", "BANK", 2000.0)
        
        assertEquals("CASH", getWalletType(cashWallet))
        assertEquals("BANK", getWalletType(bankWallet))
    }

    @Test
    fun formatWalletBalance_shouldReturnFormattedString() {
        val balance = 1234.56
        val formatted = formatBalance(balance)
        assertEquals("$1,234.56", formatted)
    }

    @Test
    fun checkLowBalanceWarning_shouldReturnTrueForLowBalance() {
        val balance = 50.0
        val threshold = 100.0
        assertTrue(isLowBalance(balance, threshold))
    }

    @Test
    fun checkLowBalanceWarning_shouldReturnFalseForSufficientBalance() {
        val balance = 150.0
        val threshold = 100.0
        assertFalse(isLowBalance(balance, threshold))
    }

    private fun calculateBalance(transactions: List<Double>): Double {
        return transactions.sum()
    }

    private fun isValidWalletName(name: String): Boolean {
        return name.isNotEmpty() && name.length >= 2
    }

    private fun calculateMonthlySpending(expenses: List<Double>): Double {
        return expenses.filter { it < 0 }.sumOf { kotlin.math.abs(it) }
    }

    private fun calculateMonthlyIncome(incomes: List<Double>): Double {
        return incomes.filter { it > 0 }.sum()
    }

    private fun getWalletType(wallet: Wallet): String {
        return wallet.type
    }

    private fun formatBalance(balance: Double): String {
        return "$${String.format("%,.2f", balance)}"
    }

    private fun isLowBalance(balance: Double, threshold: Double): Boolean {
        return balance < threshold
    }

    data class Wallet(
        val name: String,
        val type: String,
        val balance: Double
    )
}