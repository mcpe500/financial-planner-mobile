package com.example.financialplannerapp.ui.screen.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.ui.model.Wallet
import com.example.financialplannerapp.ui.viewmodel.BudgetViewModel
import com.example.financialplannerapp.ui.viewmodel.BudgetViewModelFactory
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId() ?: "guest"

    // ViewModels
    val walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(application.appContainer.walletRepository, tokenManager)
    )
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(application.appContainer.budgetRepository, tokenManager)
    )

    // State
    val wallets by walletViewModel.wallets.collectAsState()
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var budgetName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }
    var isRecurring by remember { mutableStateOf(false) }

    var isWalletDropdownExpanded by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate.time)
    val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate.time)

    LaunchedEffect(Unit) {
        walletViewModel.loadWallets()
    }

    // Dialogs
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Budget") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = isWalletDropdownExpanded,
                onExpandedChange = { isWalletDropdownExpanded = !isWalletDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedWallet?.name ?: "Select a Wallet",
                    onValueChange = {}, readOnly = true, label = { Text("Wallet") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isWalletDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isWalletDropdownExpanded,
                    onDismissRequest = { isWalletDropdownExpanded = false }
                ) {
                    wallets.forEach { wallet ->
                        DropdownMenuItem(
                            text = { Text(wallet.name) },
                            onClick = {
                                selectedWallet = wallet
                                isWalletDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(value = budgetName, onValueChange = { budgetName = it }, label = { Text("Budget Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (e.g., Food, Transport)") }, modifier = Modifier.fillMaxWidth())

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            OutlinedTextField(value = dateFormat.format(startDate), onValueChange = {}, readOnly = true, label = { Text("Start Date") }, trailingIcon = { Icon(Icons.Default.DateRange, null) }, modifier = Modifier.fillMaxWidth().clickable { showStartDatePicker = true })
            OutlinedTextField(value = dateFormat.format(endDate), onValueChange = {}, readOnly = true, label = { Text("End Date") }, trailingIcon = { Icon(Icons.Default.DateRange, null) }, modifier = Modifier.fillMaxWidth().clickable { showEndDatePicker = true })
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recurring Budget")
                Spacer(Modifier.width(8.dp))
                Switch(checked = isRecurring, onCheckedChange = { isRecurring = it })
            }

            Button(
                onClick = {
                    val budgetAmount = amount.toDoubleOrNull()
                    if (selectedWallet != null && budgetName.isNotBlank() && budgetAmount != null) {
                        budgetViewModel.addBudget(
                            walletId = selectedWallet!!.id,
                            name = budgetName,
                            amount = budgetAmount,
                            category = category,
                            startDate = startDate,
                            endDate = endDate,
                            isRecurring = isRecurring
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedWallet != null && budgetName.isNotBlank() && amount.isNotBlank()
            ) {
                Text("Save Budget")
            }
        }
    }
}