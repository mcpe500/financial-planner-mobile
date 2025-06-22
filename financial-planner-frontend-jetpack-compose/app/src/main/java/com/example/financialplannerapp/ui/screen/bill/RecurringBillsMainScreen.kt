package com.example.financialplannerapp.ui.screen.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.model.BillFilter
import com.example.financialplannerapp.data.model.BillStatus
import com.example.financialplannerapp.data.model.RecurringBill
import com.example.financialplannerapp.data.model.RepeatCycle
import com.example.financialplannerapp.ui.viewmodel.BillViewModel
import com.example.financialplannerapp.ui.viewmodel.BillViewModelFactory
import com.example.financialplannerapp.core.util.formatCurrency
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import com.example.financialplannerapp.TokenManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringBillsMainScreen(navController: NavController, tokenManager: TokenManager) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val billViewModel: BillViewModel = viewModel(
        factory = BillViewModelFactory(
            application.appContainer.billRepository,
            application.appContainer.walletRepository,
            application.appContainer.transactionRepository,
            tokenManager
        )
    )
    val isLoading by billViewModel.isLoading.collectAsState()
    val error by billViewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            billViewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        billViewModel.operationSuccess.collect { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_bill") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bill")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            RecurringBillsMainContent(
                navController = navController,
                viewModel = billViewModel,
                modifier = Modifier.padding(paddingValues)
            )
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun RecurringBillsMainContent(
    navController: NavController,
    viewModel: BillViewModel,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(BillFilter.ALL) }
    var billToEdit by remember { mutableStateOf<BillEntity?>(null) }
    var billToDelete by remember { mutableStateOf<BillEntity?>(null) }
    var billToView by remember { mutableStateOf<BillEntity?>(null) }
    var billToPay by remember { mutableStateOf<BillEntity?>(null) }

    val billEntities by viewModel.localBills.collectAsState()

    if (billToView != null) {
        BillDetailDialog(
            bill = RecurringBill.fromEntity(billToView!!),
            onDismiss = { billToView = null },
            onPay = {
                billToPay = billToView
                billToView = null
            }
        )
    }

    if (billToPay != null) {
        val wallets by viewModel.wallets.collectAsState()
        PayBillDialog(
            bill = billToPay!!,
            wallets = wallets,
            onDismiss = { billToPay = null },
            onConfirm = { wallet ->
                viewModel.payBill(billToPay!!, wallet)
                billToPay = null
            }
        )
    }

    if (billToEdit != null) {
        EditBillDialog(
            bill = billToEdit!!,
            onDismiss = { billToEdit = null },
            onSave = { updatedBill ->
                viewModel.updateBill(updatedBill)
                billToEdit = null
            }
        )
    }

    if (billToDelete != null) {
        DeleteConfirmationDialog(
            billName = billToDelete!!.name,
            onDismiss = { billToDelete = null },
            onConfirm = {
                viewModel.deleteBill(billToDelete!!.uuid)
                billToDelete = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant) // Use theme color
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BillHeaderSection(
            onBackClick = { navController.popBackStack() },
            onCalendarClick = { navController.navigate("bill_calendar") }
        )

        val bills = billEntities.map { RecurringBill.fromEntity(it) }
        BillSummaryCards(bills = bills)

        FilterChips(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            bills = bills
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            val filteredEntities = billEntities.filter { entity ->
                val bill = RecurringBill.fromEntity(entity)
                when (selectedFilter) {
                    BillFilter.ALL -> true
                    BillFilter.UPCOMING -> bill.status == BillStatus.UPCOMING || bill.status == BillStatus.DUE_SOON
                    BillFilter.PAID -> bill.status == BillStatus.PAID
                    BillFilter.UNPAID -> bill.status != BillStatus.PAID
                }
            }
            items(filteredEntities, key = { it.uuid }) { entity ->
                BillCard(
                    bill = RecurringBill.fromEntity(entity),
                    onCardClick = { billToView = entity },
                    onEditClick = { billToEdit = entity },
                    onDeleteClick = { billToDelete = entity }
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    billName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Bill") },
        text = { Text("Are you sure you want to delete '$billName'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillDialog(
    bill: BillEntity,
    onDismiss: () -> Unit,
    onSave: (BillEntity) -> Unit
) {
    var name by remember(bill) { mutableStateOf(bill.name) }
    var amount by remember(bill) { mutableStateOf(bill.estimatedAmount.toLong().toString()) }
    val initialDate = remember(bill) { Calendar.getInstance().apply { time = bill.dueDate } }
    var selectedDate by remember(bill) { mutableStateOf(initialDate) }
    val initialCycle = remember(bill) {
        try { RepeatCycle.valueOf(bill.repeatCycle) } catch (e: Exception) { RepeatCycle.MONTHLY }
    }
    var selectedCycle by remember(bill) { mutableStateOf(initialCycle) }

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.timeInMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Calendar.getInstance().apply { timeInMillis = it }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Edit Recurring Bill", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Bill Name") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; amountError = false },
                    label = { Text("Estimated Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    modifier = Modifier.fillMaxWidth()
                )

                val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
                val dateFormatted = remember(selectedDate) {
                    dateFormat.format(selectedDate.time)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true } // klik di luar OutlinedTextField
                ) {
                    OutlinedTextField(
                        value = dateFormatted,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false, // ini penting agar tidak trigger keyboard/focus
                        label = { Text("Next Due Date") },
                        trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }


                CycleDropDown(selectedCycle = selectedCycle, onCycleSelected = { selectedCycle = it })

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        nameError = name.isBlank()
                        val parsedAmount = amount.toDoubleOrNull()
                        amountError = parsedAmount == null || parsedAmount <= 0
                        if (!nameError && !amountError) {
                            val updatedBill = bill.copy(
                                name = name,
                                estimatedAmount = parsedAmount!!,
                                dueDate = selectedDate.time,
                                repeatCycle = selectedCycle.name
                            )
                            onSave(updatedBill)
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun BillHeaderSection(onBackClick: () -> Unit, onCalendarClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
        Text(
            "Recurring Bills",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onCalendarClick) { Icon(Icons.Default.CalendarToday, contentDescription = "Calendar View") }
    }
}

@Composable
fun BillSummaryCards(bills: List<RecurringBill>) {
    val totalBills = bills.size
    val upcomingBills = bills.count { it.status == BillStatus.DUE_SOON || it.status == BillStatus.UPCOMING }
    val overdueBills = bills.count { it.status == BillStatus.OVERDUE }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryItem("Total", totalBills.toString(), MaterialTheme.colorScheme.onSurface)
            SummaryItem("Upcoming", upcomingBills.toString(), MaterialTheme.colorScheme.tertiary)
            SummaryItem("Overdue", overdueBills.toString(), MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium.copy(color = color))
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(selectedFilter: BillFilter, onFilterSelected: (BillFilter) -> Unit, bills: List<RecurringBill>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(BillFilter.values()) { filter ->
            val count = when (filter) {
                BillFilter.ALL -> bills.size
                BillFilter.UPCOMING -> bills.count { it.status == BillStatus.UPCOMING || it.status == BillStatus.DUE_SOON }
                BillFilter.PAID -> bills.count { it.status == BillStatus.PAID }
                BillFilter.UNPAID -> bills.count { it.status != BillStatus.PAID }
            }
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text("${filter.label} ($count)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun BillCard(bill: RecurringBill, onCardClick: () -> Unit, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val statusColor = when (bill.status) {
        BillStatus.PAID -> MaterialTheme.colorScheme.primary
        BillStatus.DUE_SOON -> MaterialTheme.colorScheme.tertiary
        BillStatus.OVERDUE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Receipt,
                    "Bill",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        bill.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Amount: ${formatCurrency(bill.estimatedAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        bill.status.name,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Next Due: ${dateFormat.format(bill.nextDueDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, "Edit Bill", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, "Delete Bill", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun BillDetailDialog(
    bill: RecurringBill,
    onDismiss: () -> Unit,
    onPay: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = bill.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Divider()
                Row {
                    Text("Amount: ", fontWeight = FontWeight.Bold)
                    Text(formatCurrency(bill.estimatedAmount))
                }
                Row {
                    Text("Due Date: ", fontWeight = FontWeight.Bold)
                    Text(dateFormat.format(bill.dueDate))
                }
                Row {
                    Text("Status: ", fontWeight = FontWeight.Bold)
                    Text(bill.status.name, color = bill.status.color)
                }
                Row {
                    Text("Cycle: ", fontWeight = FontWeight.Bold)
                    Text(bill.repeatCycle)
                }
                if (bill.notes.isNotBlank()) {
                    Row {
                        Text("Notes: ", fontWeight = FontWeight.Bold)
                        Text(bill.notes)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onPay,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pay")
                }
            }
        }
    }
}

@Composable
fun PayBillDialog(
    bill: BillEntity,
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onConfirm: (WalletEntity) -> Unit
) {
    var selectedWallet by remember { mutableStateOf<WalletEntity?>(null) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pay Bill: ${bill.name}") },
        text = {
            Column {
                Text("Amount: ${formatCurrency(bill.estimatedAmount)}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select a wallet to pay from:")
                Spacer(modifier = Modifier.height(8.dp))

                if (wallets.isEmpty()) {
                    Text("No wallets available. Please create a wallet first.", color = MaterialTheme.colorScheme.error)
                } else {
                    Box {
                        OutlinedTextField(
                            value = selectedWallet?.name ?: "Select Wallet",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Select Wallet") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            wallets.forEach { wallet ->
                                DropdownMenuItem(
                                    text = { Text("${wallet.name} (${formatCurrency(wallet.balance)})") },
                                    onClick = {
                                        selectedWallet = wallet
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    selectedWallet?.let {
                        if (it.balance < bill.estimatedAmount) {
                            Text(
                                "This wallet has insufficient funds.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedWallet?.let { onConfirm(it) }
                },
                enabled = selectedWallet != null && selectedWallet!!.balance >= bill.estimatedAmount && wallets.isNotEmpty()
            ) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}