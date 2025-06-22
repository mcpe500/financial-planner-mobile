package com.example.financialplannerapp.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

    val filteredTransactions = remember(state.transactions, selectedFilter) {
        when (selectedFilter) {
            TransactionFilter.ALL -> state.transactions
            TransactionFilter.INCOME -> state.transactions.filter { it.type.equals("INCOME", true) }
            TransactionFilter.EXPENSE -> state.transactions.filter { it.type.equals("EXPENSE", true) }
        }
    }

    val groupedTransactions = remember(filteredTransactions) {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        
        filteredTransactions
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                EmptyTransactionsView(selectedFilter)
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
                                    imageVector = Icons.Default.Check,
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
private fun EmptyTransactionsView(selectedFilter: TransactionFilter) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = "No transactions",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = when (selectedFilter) {
                    TransactionFilter.ALL -> "No transactions found"
                    TransactionFilter.INCOME -> "No income transactions"
                    TransactionFilter.EXPENSE -> "No expense transactions"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (selectedFilter) {
                    TransactionFilter.ALL -> "Your transaction history will appear here"
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
                        Icons.Default.TrendingUp
                    } else {
                        Icons.Default.TrendingDown
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
                            imageVector = Icons.Default.CameraAlt,
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