package com.example.financialplannerapp.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.core.util.formatCurrency
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModel
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign

enum class TransactionFilter {
    ALL, INCOME, EXPENSE
}

data class TransactionGroup(
    val month: String,
    val year: Int,
    val transactions: List<TransactionEntity>,
    val totalIncome: Double,
    val totalExpense: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(
            transactionRepository = application.appContainer.transactionRepository,
            userId = application.appContainer.tokenManager.getUserId() ?: "local_user"
        )
    )
    val state by viewModel.state
    
    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var showMonthPicker by remember { mutableStateOf(false) }

    val filteredTransactions = remember(state.transactions, selectedFilter) {
        when (selectedFilter) {
            TransactionFilter.ALL -> state.transactions
            TransactionFilter.INCOME -> state.transactions.filter { it.type.equals("INCOME", true) }
            TransactionFilter.EXPENSE -> state.transactions.filter { it.type.equals("EXPENSE", true) }
        }
    }

    val groupedTransactions = remember(filteredTransactions, selectedMonth) {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        
        // Filter transactions for selected month and year
        val selectedYear = selectedMonth.get(Calendar.YEAR)
        val selectedMonthValue = selectedMonth.get(Calendar.MONTH)
        
        val monthFilteredTransactions = filteredTransactions.filter { transaction ->
            calendar.time = transaction.date
            val transactionYear = calendar.get(Calendar.YEAR)
            val transactionMonth = calendar.get(Calendar.MONTH)
            transactionYear == selectedYear && transactionMonth == selectedMonthValue
        }
        
        monthFilteredTransactions
            .groupBy { transaction ->
                calendar.time = transaction.date
                val month = monthFormat.format(transaction.date)
                val year = calendar.get(Calendar.YEAR)
                "$month $year"
            }
            .map { (monthYear, transactions) ->
                val calendar = Calendar.getInstance()
                calendar.time = transactions.first().date
                val year = calendar.get(Calendar.YEAR)
                
                val totalIncome = transactions
                    .filter { it.type.equals("INCOME", true) }
                    .sumOf { it.amount }
                val totalExpense = transactions
                    .filter { it.type.equals("EXPENSE", true) }
                    .sumOf { it.amount }
                
                TransactionGroup(
                    month = monthYear,
                    year = year,
                    transactions = transactions.sortedByDescending { it.date },
                    totalIncome = totalIncome,
                    totalExpense = totalExpense
                )
            }
            .sortedByDescending { it.transactions.first().date }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transaction History",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Month Selector
            MonthSelectorCard(
                selectedMonth = selectedMonth,
                onMonthChange = { selectedMonth = it },
                showMonthPicker = showMonthPicker,
                onShowMonthPickerChange = { showMonthPicker = it }
            )
            
            // Filter Section
            FilterSection(
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it }
            )
            
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (groupedTransactions.isEmpty()) {
                EmptyTransactionsView(selectedFilter, selectedMonth)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(groupedTransactions) { group ->
                        TransactionGroupCard(
                            group = group,
                            onTransactionClick = { transaction ->
                                navController.navigate("transaction_detail/${transaction.id}")
                            }
                        )
                    }
                }
            }
        }
    }

    // Month Picker Dialog
    if (showMonthPicker) {
        AlertDialog(
            onDismissRequest = { showMonthPicker = false },
            title = {
                Text("Select Month")
            },
            text = {
                Text("Month picker functionality will be implemented here. For now, you can view transactions from the current month.")
            },
            confirmButton = {
                TextButton(onClick = { showMonthPicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMonthPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MonthSelectorCard(
    selectedMonth: Calendar,
    onMonthChange: (Calendar) -> Unit,
    showMonthPicker: Boolean,
    onShowMonthPickerChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Select Month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedMonth.time),
                onValueChange = {},
                readOnly = true,
                label = { Text("Month") },
                trailingIcon = {
                    IconButton(onClick = { onShowMonthPickerChange(true) }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Select Month")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun FilterSection(
    selectedFilter: TransactionFilter,
    onFilterChange: (TransactionFilter) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Filter Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChange(filter) },
                        label = {
                            Text(
                                text = when (filter) {
                                    TransactionFilter.ALL -> "All"
                                    TransactionFilter.INCOME -> "Income"
                                    TransactionFilter.EXPENSE -> "Expense"
                                }
                            )
                        },
                        leadingIcon = {
                            if (selectedFilter == filter) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTransactionsView(selectedFilter: TransactionFilter, selectedMonth: Calendar) {
    val monthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedMonth.time)
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Receipt,
                contentDescription = "No transactions",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = when (selectedFilter) {
                    TransactionFilter.ALL -> "No transactions in $monthYear"
                    TransactionFilter.INCOME -> "No income transactions in $monthYear"
                    TransactionFilter.EXPENSE -> "No expense transactions in $monthYear"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (selectedFilter) {
                    TransactionFilter.ALL -> "Try selecting a different month or add new transactions"
                    TransactionFilter.INCOME -> "Income transactions will appear here"
                    TransactionFilter.EXPENSE -> "Expense transactions will appear here"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TransactionGroupCard(
    group: TransactionGroup,
    onTransactionClick: (TransactionEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.month,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    when (selectedFilter) {
                        TransactionFilter.ALL -> {
                            Text(
                                text = "Income: ${formatCurrency(group.totalIncome)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Expense: ${formatCurrency(group.totalExpense)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        TransactionFilter.INCOME -> {
                            Text(
                                text = "Income: ${formatCurrency(group.totalIncome)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        TransactionFilter.EXPENSE -> {
                            Text(
                                text = "Expense: ${formatCurrency(group.totalExpense)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Transactions List
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                group.transactions.forEach { transaction ->
                    TransactionHistoryItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryItem(transaction: TransactionEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (transaction.type.equals("INCOME", true)) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type.equals("INCOME", true)) {
                        Icons.Filled.TrendingUp
                    } else {
                        Icons.Filled.TrendingDown
                    },
                    contentDescription = transaction.type,
                    tint = if (transaction.type.equals("INCOME", true)) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.merchantName ?: transaction.note ?: "Transaction",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (transaction.note != null && transaction.note.isNotBlank() && transaction.merchantName != null) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row {
            // Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (transaction.type.equals("INCOME", true)) {
                        "+${formatCurrency(transaction.amount)}"
                    } else {
                        "-${formatCurrency(transaction.amount)}"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type.equals("INCOME", true)) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                
                // Receipt indicator
                if (transaction.isFromReceipt) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "From Receipt",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Receipt",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
} 