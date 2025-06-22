package com.example.financialplannerapp.viewmodel

import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    private lateinit var viewModel: TransactionViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = TransactionViewModel(transactionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTransactions should update state with transactions`() = runTest {
        // Given
        val mockTransactions = listOf(
            TransactionEntity(
                id = 1,
                amount = 100.0,
                type = "EXPENSE",
                category = "Food",
                note = "Lunch",
                date = Date(),
                merchantName = "Restaurant"
            )
        )
        whenever(transactionRepository.getAllTransactions()).thenReturn(flowOf(mockTransactions))

        // When
        viewModel.loadTransactions()

        // Then
        assertEquals(mockTransactions, viewModel.state.value.transactions)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `initial state should be loading`() {
        // Then
        assertTrue(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.transactions.isEmpty())
    }
}