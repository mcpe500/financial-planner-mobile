package com.example.financialplannerapp.repository

import org.junit.Test
import org.junit.Assert.*

class WalletRepositoryTest {

    @Test
    fun createWallet_shouldReturnWalletWithId() {
        val wallet = createMockWallet()
        val savedWallet = saveWallet(wallet)
        
        assertNotNull(savedWallet.id)
        assertEquals(wallet.name, savedWallet.name)
        assertEquals(wallet.balance, savedWallet.balance, 0.01)
    }

    @Test
    fun updateWalletBalance_shouldUpdateCorrectly() {
        val wallet = MockWallet(1, "My Wallet", "CASH", 1000.0)
        val updatedWallet = updateBalance(wallet, 1500.0)
        
        assertEquals(1500.0, updatedWallet.balance, 0.01)
    }

    @Test
    fun filterWalletsByType_shouldReturnCorrectWallets() {
        val wallets = listOf(
            MockWallet(1, "Cash Wallet", "CASH", 500.0),
            MockWallet(2, "Bank Account", "BANK", 2000.0),
            MockWallet(3, "Credit Card", "CREDIT", 1000.0),
            MockWallet(4, "Savings", "BANK", 5000.0)
        )
        
        val bankWallets = filterByType(wallets, "BANK")
        assertEquals(2, bankWallets.size)
        assertTrue(bankWallets.all { it.type == "BANK" })
    }

    @Test
    fun calculateTotalBalance_shouldReturnCorrectSum() {
        val wallets = listOf(
            MockWallet(1, "Cash", "CASH", 500.0),
            MockWallet(2, "Bank", "BANK", 2000.0),
            MockWallet(3, "Savings", "BANK", 3000.0)
        )
        
        val totalBalance = calculateTotalBalance(wallets)
        assertEquals(5500.0, totalBalance, 0.01)
    }

    @Test
    fun validateWalletData_shouldReturnTrueForValidWallet() {
        val wallet = createMockWallet()
        assertTrue(isValidWallet(wallet))
    }

    @Test
    fun validateWalletData_shouldReturnFalseForInvalidWallet() {
        val wallet = MockWallet(0, "", "", -100.0)
        assertFalse(isValidWallet(wallet))
    }

    @Test
    fun transferBetweenWallets_shouldUpdateBothWallets() {
        val fromWallet = MockWallet(1, "From Wallet", "CASH", 1000.0)
        val toWallet = MockWallet(2, "To Wallet", "BANK", 500.0)
        val amount = 200.0
        
        val result = transferFunds(fromWallet, toWallet, amount)
        
        assertEquals(800.0, result.first.balance, 0.01)
        assertEquals(700.0, result.second.balance, 0.01)
    }

    @Test
    fun getWalletsByBalanceRange_shouldReturnCorrectWallets() {
        val wallets = listOf(
            MockWallet(1, "Low Balance", "CASH", 100.0),
            MockWallet(2, "Medium Balance", "BANK", 1000.0),
            MockWallet(3, "High Balance", "BANK", 5000.0)
        )
        
        val mediumBalanceWallets = filterByBalanceRange(wallets, 500.0, 2000.0)
        assertEquals(1, mediumBalanceWallets.size)
        assertEquals("Medium Balance", mediumBalanceWallets[0].name)
    }

    @Test
    fun archiveWallet_shouldMarkWalletAsArchived() {
        val wallet = MockWallet(1, "Old Wallet", "CASH", 0.0)
        val archivedWallet = archiveWallet(wallet)
        assertTrue(archivedWallet.isArchived)
    }

    private fun createMockWallet(): MockWallet {
        return MockWallet(
            id = 0,
            name = "My Wallet",
            type = "CASH",
            balance = 1000.0,
            isArchived = false
        )
    }

    private fun saveWallet(wallet: MockWallet): MockWallet {
        return wallet.copy(id = 1)
    }

    private fun updateBalance(wallet: MockWallet, newBalance: Double): MockWallet {
        return wallet.copy(balance = newBalance)
    }

    private fun filterByType(wallets: List<MockWallet>, type: String): List<MockWallet> {
        return wallets.filter { it.type == type }
    }

    private fun calculateTotalBalance(wallets: List<MockWallet>): Double {
        return wallets.sumOf { it.balance }
    }

    private fun isValidWallet(wallet: MockWallet): Boolean {
        return wallet.name.isNotEmpty() && wallet.type.isNotEmpty() && wallet.balance >= 0
    }

    private fun transferFunds(from: MockWallet, to: MockWallet, amount: Double): Pair<MockWallet, MockWallet> {
        val updatedFrom = from.copy(balance = from.balance - amount)
        val updatedTo = to.copy(balance = to.balance + amount)
        return Pair(updatedFrom, updatedTo)
    }

    private fun filterByBalanceRange(wallets: List<MockWallet>, min: Double, max: Double): List<MockWallet> {
        return wallets.filter { it.balance >= min && it.balance <= max }
    }

    private fun archiveWallet(wallet: MockWallet): MockWallet {
        return wallet.copy(isArchived = true)
    }

    data class MockWallet(
        val id: Long,
        val name: String,
        val type: String,
        val balance: Double,
        val isArchived: Boolean = false
    )
}