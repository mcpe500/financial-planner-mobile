package com.example.financialplannerapp.ui.screen.bill

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.model.RepeatCycle
import com.example.financialplannerapp.ui.viewmodel.BillViewModel
import com.example.financialplannerapp.ui.viewmodel.BillViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringBillScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val application = context.applicationContext as MainApplication
    val billViewModel: BillViewModel = viewModel(
        factory = BillViewModelFactory(application.appContainer.billRepository)
    )

    val isLoading by billViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        billViewModel.operationSuccess.collectLatest { success ->
            if (success) {
                navController.popBackStack()
            }
        }
    }

    var billName by remember { mutableStateOf("") }
    var estimatedAmount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedCycle by remember { mutableStateOf(RepeatCycle.MONTHLY) }
    var notes by remember { mutableStateOf("") }

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
                        val newDate = Calendar.getInstance().apply { timeInMillis = it }
                        if (!newDate.before(Calendar.getInstance())) {
                            selectedDate = newDate
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Recurring Bill", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = billName,
                onValueChange = { billName = it; nameError = false },
                label = { Text("Bill Name (e.g., Netflix)") },
                isError = nameError,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estimatedAmount,
                onValueChange = { estimatedAmount = it; amountError = false },
                label = { Text("Estimated Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = amountError,
                modifier = Modifier.fillMaxWidth()
            )

            val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
            OutlinedTextField(
                value = dateFormat.format(selectedDate.time),
                onValueChange = {},
                readOnly = true,
                label = { Text("First Due Date") },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Select Date") }
            )

            CycleDropDown(selectedCycle = selectedCycle, onCycleSelected = { selectedCycle = it })

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    nameError = billName.isBlank()
                    val parsedAmount = estimatedAmount.toDoubleOrNull()
                    amountError = parsedAmount == null || parsedAmount <= 0
                    if (!nameError && !amountError) {
                        val userEmail = if (tokenManager.isNoAccountMode()) {
                            "guest"
                        } else {
                            tokenManager.getUserEmail() ?: "guest"
                        }
                        val billEntity = BillEntity(
                            uuid = UUID.randomUUID().toString(),
                            name = billName,
                            estimatedAmount = parsedAmount!!,
                            dueDate = selectedDate.time,
                            repeatCycle = selectedCycle.name,
                            category = "Default",
                            notes = notes,
                            isActive = true,
                            paymentsJson = Gson().toJson(emptyList<Any>()),
                            autoPay = false,
                            notificationEnabled = true,
                            lastPaymentDate = null,
                            creationDate = Date(),
                            userEmail = userEmail
                        )
                        billViewModel.addBill(billEntity)
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Bill")
                }
            }
        }
    }
}