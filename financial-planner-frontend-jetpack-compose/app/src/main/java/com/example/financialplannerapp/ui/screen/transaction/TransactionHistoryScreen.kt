package com.example.financialplannerapp.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModel
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val ExpenseRed = Color(0xFFFF7043)

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val date: Date,
    val pocket: String,
    val category: String,
    val tags: List<String>,
    val note: String,
    val isRecurring: Boolean = false,
    val isSplit: Boolean = false
)

enum class TransactionType {
    INCOME, EXPENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(
            transactionRepository = application.appContainer.transactionRepository
        )
    )
    val state by viewModel.state

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
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        },
        containerColor = SoftGray
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftGray)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.transactions) { transaction ->
                    TransactionHistoryItem(transaction)
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(transaction.date),
                        fontSize = 14.sp,
                        color = DarkGray
                    )
                    Text(
                        text = transaction.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    transaction.note?.let {
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
                Text(
                    text = (if (transaction.type.equals("INCOME", true)) "+" else "-") + transaction.amount.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type.equals("INCOME", true)) BibitGreen else Color.Red
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    selectedDateRange: String,
    selectedPocket: String,
    selectedCategory: String,
    selectedTag: String,
    onDateRangeChange: (String) -> Unit,
    onPocketChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onTagChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filters",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterDropdown(
                    label = "Date",
                    selectedValue = selectedDateRange,
                    options = listOf("All Time", "This Month", "Last Month", "This Year"),
                    onValueChange = onDateRangeChange,
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    label = "Pocket",
                    selectedValue = selectedPocket,
                    options = listOf("All Pockets", "Cash", "Bank", "E-Wallet"),
                    onValueChange = onPocketChange,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterDropdown(
                    label = "Category",
                    selectedValue = selectedCategory,
                    options = listOf("All Categories", "Food", "Transport", "Shopping", "Bills"),
                    onValueChange = onCategoryChange,
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    label = "Tag",
                    selectedValue = selectedTag,
                    options = listOf("All Tags", "Work", "Personal", "Emergency"),
                    onValueChange = onTagChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BibitGreen,
                focusedLabelColor = BibitGreen
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (transaction.type == TransactionType.INCOME) "+" else "-",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.type == TransactionType.INCOME) BibitGreen else ExpenseRed
                        )
                        Text(
                            text = "$${transaction.amount}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.type == TransactionType.INCOME) BibitGreen else ExpenseRed
                        )
                        if (transaction.isRecurring) {
                            Icon(
                                Icons.Default.Repeat,
                                contentDescription = "Recurring",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp),
                                tint = MediumGray
                            )
                        }
                        if (transaction.isSplit) {
                            Icon(
                                Icons.Default.CallSplit,
                                contentDescription = "Split",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp),
                                tint = MediumGray
                            )
                        }
                    }

                    Text(
                        text = transaction.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(transaction.date),
                        fontSize = 12.sp,
                        color = MediumGray
                    )

                    Text(
                        text = "Pocket: ${transaction.pocket}",
                        fontSize = 12.sp,
                        color = MediumGray
                    )

                    if (transaction.note.isNotEmpty()) {
                        Text(
                            text = transaction.note,
                            fontSize = 12.sp,
                            color = MediumGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (transaction.tags.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            transaction.tags.take(2).forEach { tag ->
                                Text(
                                    text = "#$tag",
                                    fontSize = 10.sp,
                                    color = BibitGreen,
                                    modifier = Modifier
                                        .background(
                                            BibitLightGreen.copy(alpha = 0.2f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (transaction.tags.size > 2) {
                                Text(
                                    text = "+${transaction.tags.size - 2}",
                                    fontSize = 10.sp,
                                    color = MediumGray
                                )
                            }
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = BibitGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ExpenseRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun generateMockTransactions(): List<Transaction> {
    val calendar = Calendar.getInstance()
    return listOf(
        Transaction(
            id = "1",
            type = TransactionType.EXPENSE,
            amount = 25.50,
            date = calendar.time,
            pocket = "Cash",
            category = "Food",
            tags = listOf("lunch", "work"),
            note = "Lunch at office cafeteria",
            isRecurring = false,
            isSplit = false
        ),
        Transaction(
            id = "2",
            type = TransactionType.INCOME,
            amount = 3000.00,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            pocket = "Bank",
            category = "Salary",
            tags = listOf("work", "monthly"),
            note = "Monthly salary",
            isRecurring = true,
            isSplit = false
        ),
        Transaction(
            id = "3",
            type = TransactionType.EXPENSE,
            amount = 120.00,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
            pocket = "E-Wallet",
            category = "Shopping",
            tags = listOf("clothes", "personal"),
            note = "New shirt from online store",
            isRecurring = false,
            isSplit = true
        ),
        Transaction(
            id = "4",
            type = TransactionType.EXPENSE,
            amount = 15.00,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
            pocket = "Cash",
            category = "Transport",
            tags = listOf("commute"),
            note = "Bus fare to work",
            isRecurring = true,
            isSplit = false
        ),
        Transaction(
            id = "5",
            type = TransactionType.EXPENSE,
            amount = 85.00,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -4) }.time,
            pocket = "Bank",
            category = "Bills",
            tags = listOf("utilities", "monthly"),
            note = "Electricity bill",
            isRecurring = true,
            isSplit = false
        )
    )
}