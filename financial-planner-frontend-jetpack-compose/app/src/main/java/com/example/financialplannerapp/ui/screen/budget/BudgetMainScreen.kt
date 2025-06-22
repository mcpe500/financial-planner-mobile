package com.example.financialplannerapp.ui.screen.budget

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.BudgetEntity
import com.example.financialplannerapp.ui.viewmodel.BudgetViewModel
import com.example.financialplannerapp.ui.viewmodel.BudgetViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetMainScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(context) }
    
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(application.appContainer.budgetRepository, tokenManager)
    )

    val budgets by budgetViewModel.budgets.collectAsState()
    val error by budgetViewModel.error.collectAsState()
    var budgetToEdit by remember { mutableStateOf<BudgetEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_budget") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Budget")
            }
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }
            ManageBudgetTab(
                navController = navController,
                budgets = budgets,
                onEdit = { budget ->
                    budgetToEdit = budget
                    showEditDialog = true
                },
                onDelete = { budget ->
                    budgetViewModel.deleteBudget(budget.id)
                }
            )
        }
    }

    if (showEditDialog && budgetToEdit != null) {
        EditBudgetDialog(
            budget = budgetToEdit!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, amount, category, startDate, endDate, isRecurring ->
                budgetViewModel.editBudget(
                    budgetId = budgetToEdit!!.id,
                    name = name,
                    amount = amount,
                    category = category,
                    startDate = startDate,
                    endDate = endDate,
                    isRecurring = isRecurring
                )
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun ManageBudgetTab(
    navController: NavController,
    budgets: List<BudgetEntity>,
    onEdit: (BudgetEntity) -> Unit,
    onDelete: (BudgetEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (budgets.isEmpty()){
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center){
                    Text("No budgets created yet.")
                }
            }
        } else {
            items(budgets, key = { it.id }) { budget ->
                BudgetProgressCard(
                    budget = budget,
                    spentAmount = 0.0,
                    onClick = {},
                    onEdit = { onEdit(budget) },
                    onDelete = { onDelete(budget) }
                )
            }
        }
    }
}

@Composable
fun BudgetProgressCard(
    budget: BudgetEntity,
    spentAmount: Double,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val progress = (spentAmount / budget.amount).toFloat().coerceIn(0f, 1f)
    val remainingAmount = (budget.amount - spentAmount).coerceAtLeast(0.0)
    
    val progressColor = when {
        progress > 1.0f -> MaterialTheme.colorScheme.error
        progress > 0.8f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(budget.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(budget.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Spent: ${currencyFormat.format(spentAmount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Limit: ${currencyFormat.format(budget.amount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                "Remaining: ${currencyFormat.format(remainingAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (remainingAmount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
                fontWeight = if (remainingAmount <= 0) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetDialog(
    budget: BudgetEntity,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, Date, Date, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(budget.name) }
    var amount by remember { mutableStateOf(budget.amount.toString()) }
    var category by remember { mutableStateOf(budget.category) }
    var startDate by remember { mutableStateOf(budget.startDate) }
    var endDate by remember { mutableStateOf(budget.endDate) }
    var isRecurring by remember { mutableStateOf(budget.isRecurring) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate.time)
    val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate.time)
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }

    if (showStartDatePicker) {
        DatePickerDialog(onDismissRequest = { showStartDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                startDatePickerState.selectedDateMillis?.let { startDate = Date(it) }
                showStartDatePicker = false
            }) { Text("OK") }
        }) { DatePicker(state = startDatePickerState) }
    }
    if (showEndDatePicker) {
        DatePickerDialog(onDismissRequest = { showEndDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                endDatePickerState.selectedDateMillis?.let { endDate = Date(it) }
                showEndDatePicker = false
            }) { Text("OK") }
        }) { DatePicker(state = endDatePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Budget") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Budget Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateFormat.format(startDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start Date") },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    modifier = Modifier.fillMaxWidth().clickable { showStartDatePicker = true }
                )
                OutlinedTextField(
                    value = dateFormat.format(endDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("End Date") },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    modifier = Modifier.fillMaxWidth().clickable { showEndDatePicker = true }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Recurring Budget")
                    Spacer(Modifier.width(8.dp))
                    Switch(checked = isRecurring, onCheckedChange = { isRecurring = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amt = amount.toDoubleOrNull() ?: budget.amount
                onSave(name, amt, category, startDate, endDate, isRecurring)
            }) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
