package com.example.financialplannerapp.repository

import com.example.financialplannerapp.data.local.dao.BillDao
import com.example.financialplannerapp.data.local.dao.WalletDao
import com.example.financialplannerapp.data.local.model.Bill
import com.example.financialplannerapp.data.local.model.BillCategory
import com.example.financialplannerapp.data.local.model.Wallet
import com.example.financialplannerapp.data.repository.BillRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import com.example.financialplannerapp.service.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class BillRepositoryTest {

    private lateinit var billRepository: BillRepository
    private lateinit var walletRepository: WalletRepository
    private lateinit var mockBillDao: MockBillDao
    private lateinit var mockWalletDao: MockWalletDao
    private lateinit var tokenManager: TokenManager

    // Mock implementation of BillDao for testing purposes
    class MockBillDao : BillDao {
        private val bills = mutableListOf<Bill>()
        private var nextId = 1

        override suspend fun insert(bill: Bill) {
            bills.add(bill.copy(id = nextId++))
        }

        override suspend fun update(bill: Bill) {
            val index = bills.indexOfFirst { it.id == bill.id }
            if (index != -1) {
                bills[index] = bill
            }
        }

        override suspend fun delete(bill: Bill) {
            bills.removeIf { it.id == bill.id }
        }

        override fun getBillsForMonth(year: Int, month: Int): Flow<List<Bill>> {
            return flowOf(bills.filter { it.dueDate.year == year && it.dueDate.monthValue == month })
        }

        override fun getBillById(id: Int): Flow<Bill?> {
            return flowOf(bills.find { it.id == id })
        }

        override fun getRecurringBills(): Flow<List<Bill>> {
            return flowOf(bills.filter { it.isRecurring })
        }

        override fun getUpcomingBills(
            startDate: LocalDate,
            endDate: LocalDate
        ): Flow<List<Bill>> {
            return flowOf(bills.filter { !it.dueDate.isBefore(startDate) && !it.dueDate.isAfter(endDate) })
        }

        fun clear() {
            bills.clear()
            nextId = 1
        }
    }

    // Mock implementation of WalletDao
    class MockWalletDao : WalletDao {
        private val wallets = mutableListOf<Wallet>()
        private var nextId = 1

        override suspend fun insert(wallet: Wallet) {
            wallets.add(wallet.copy(id = nextId++))
        }

        override suspend fun update(wallet: Wallet) {
            val index = wallets.indexOfFirst { it.id == wallet.id }
            if (index != -1) {
                wallets[index] = wallet
            }
        }

        override suspend fun delete(wallet: Wallet) {
            wallets.removeIf { it.id == wallet.id }
        }

        override fun getAllWallets(): Flow<List<Wallet>> = flowOf(wallets)

        override fun getWalletById(id: Int): Flow<Wallet?> = flowOf(wallets.find { it.id == id })

        fun clear() {
            wallets.clear()
            nextId = 1
        }
    }

    @Before
    fun setup() {
        mockBillDao = MockBillDao()
        mockWalletDao = MockWalletDao()
        tokenManager = object : TokenManager {
            override fun getEmail(): String = "test@example.com"
            override fun getToken(): String? = "fake-token"
            override fun saveToken(token: String) {}
            override fun clearToken() {}
            override fun saveEmail(email: String) {}
            override fun clearEmail() {}
        }
        walletRepository = WalletRepository(mockWalletDao, tokenManager)
        billRepository = BillRepository(mockBillDao, walletRepository)
    }

    private fun createMockBill(isRecurring: Boolean): Bill {
        return Bill(
            id = 0, // ID is set by Mock DAO
            walletId = 1,
            userEmail = "test@example.com",
            amount = 100.0,
            dueDate = LocalDate.now().plusDays(10),
            category = BillCategory.UTILITIES,
            isRecurring = isRecurring,
            isPaid = false,
            notes = "Test bill"
        )
    }

    @Test
    fun insertBill_shouldAddBillToDao() = runBlocking {
        val bill = createMockBill(false)
        billRepository.insert(bill)

        val bills = billRepository.getUpcomingBills().first()
        assertEquals(1, bills.size)
        assertEquals("Test bill", bills[0].notes)
    }

    @Test
    fun getRecurringBills_shouldReturnOnlyRecurring() = runBlocking {
        val recurringBill = createMockBill(true)
        val oneTimeBill = createMockBill(false)

        billRepository.insert(recurringBill)
        billRepository.insert(oneTimeBill)

        val recurring = billRepository.getRecurringBills().first()
        assertEquals(1, recurring.size)
        assertTrue(recurring[0].isRecurring)
    }

    @Test
    fun updateBill_shouldReflectChanges() = runBlocking {
        val bill = createMockBill(false)
        billRepository.insert(bill)
        val insertedBill = billRepository.getUpcomingBills().first().first()

        val updatedBill = insertedBill.copy(isPaid = true, notes = "Updated Note")
        billRepository.update(updatedBill)

        val resultBill = billRepository.getBillById(insertedBill.id).first()
        assertNotNull(resultBill)
        assertTrue(resultBill!!.isPaid)
        assertEquals("Updated Note", resultBill.notes)
    }

    @Test
    fun deleteBill_shouldRemoveBillFromDao() = runBlocking {
        val bill = createMockBill(false)
        billRepository.insert(bill)
        val insertedBill = billRepository.getUpcomingBills().first().first()

        billRepository.delete(insertedBill)

        val resultBill = billRepository.getBillById(insertedBill.id).first()
        assertNull(resultBill)
    }

    @Test
    fun payBill_shouldUpdateBillAndWallet() = runBlocking {
        // 1. Setup initial state: a wallet and a bill
        val wallet = Wallet(id = 1, name = "Test Wallet", balance = 500.0, userEmail = "test@example.com", type = "Cash", icon = "")
        mockWalletDao.insert(wallet)

        val billToPay = createMockBill(false).copy(amount = 100.0)
        billRepository.insert(billToPay)
        val insertedBill = billRepository.getUpcomingBills().first().first()

        // 2. Perform the action
        val success = billRepository.payBill(insertedBill.id)

        // 3. Assert the results
        assertTrue(success)

        // Check if bill is marked as paid
        val paidBill = billRepository.getBillById(insertedBill.id).first()
        assertNotNull(paidBill)
        assertTrue(paidBill!!.isPaid)

        // Check if wallet balance is reduced
        val updatedWallet = walletRepository.getWalletById(1).first()
        assertNotNull(updatedWallet)
        assertEquals(400.0, updatedWallet!!.balance, 0.01)
    }

    @Test
    fun payBill_withInsufficientFunds_shouldFail() = runBlocking {
        // 1. Setup initial state: a wallet with low balance and a bill
        val wallet = Wallet(id = 1, name = "Test Wallet", balance = 50.0, userEmail = "test@example.com", type = "Cash", icon = "")
        mockWalletDao.insert(wallet)

        val billToPay = createMockBill(false).copy(amount = 100.0)
        billRepository.insert(billToPay)
        val insertedBill = billRepository.getUpcomingBills().first().first()

        // 2. Perform the action
        val success = billRepository.payBill(insertedBill.id)

        // 3. Assert the results
        assertFalse(success)

        // Check that bill is NOT marked as paid
        val notPaidBill = billRepository.getBillById(insertedBill.id).first()
        assertNotNull(notPaidBill)
        assertFalse(notPaidBill!!.isPaid)

        // Check that wallet balance is unchanged
        val sameWallet = walletRepository.getWalletById(1).first()
        assertNotNull(sameWallet)
        assertEquals(50.0, sameWallet!!.balance, 0.01)
    }
}
