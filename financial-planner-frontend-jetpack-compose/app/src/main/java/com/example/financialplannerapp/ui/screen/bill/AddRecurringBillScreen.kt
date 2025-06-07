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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.data.model.RecurringBill
import com.example.financialplannerapp.data.model.RepeatCycle

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringBillScreen(
    billId: NavHostController? = null,
    onNavigateBack: () -> Unit = {},
    onSave: (RecurringBill) -> Unit = {}
) {
    var billName by remember { mutableStateOf("") }
    var estimatedAmount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var selectedCycle by remember { mutableStateOf(RepeatCycle.MONTHLY) }
    var notes by remember { mutableStateOf("") }
    var isReminderEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf("09:00") }
    var reminderDaysBefore by remember { mutableStateOf(1) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCycleDropdown by remember { mutableStateOf(false) }

    // Validation states
    var nameError by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }

    // Colors
    val BibitGreen = Color(0xFF4CAF50)
    val BibitDarkGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFFE8F5E8)

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    // Validation function
    fun validateForm(): Boolean {
        var isValid = true

        if (billName.isBlank()) {
            nameError = "Nama tagihan tidak boleh kosong"
            isValid = false
        } else {
            nameError = ""
        }

        if (estimatedAmount.isBlank()) {
            amountError = "Jumlah estimasi tidak boleh kosong"
            isValid = false
        } else {
            try {
                val amount = estimatedAmount.replace(",", "").replace(".", "").toDouble()
                if (amount <= 0) {
                    amountError = "Jumlah harus lebih dari 0"
                    isValid = false
                } else {
                    amountError = ""
                }
            } catch (e: NumberFormatException) {
                amountError = "Format jumlah tidak valid"
                isValid = false
            }
        }

        return isValid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (billId == null) "Tambah Tagihan" else "Edit Tagihan",
                        fontWeight = FontWeight.Bold,
                        color = BibitDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BibitGreen
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (validateForm()) {
                                val bill = RecurringBill(
                                    id = (billId ?: UUID.randomUUID().toString()) as String,
                                    name = billName,
                                    estimatedAmount = estimatedAmount.replace(",", "").replace(".", "").toDouble(),
                                    dueDate = selectedDate,
                                    repeatCycle = selectedCycle,
                                    notes = notes,
                                    isReminderEnabled = isReminderEnabled,
                                    reminderTime = reminderTime,
                                    reminderDaysBefore = reminderDaysBefore
                                )
                                onSave(bill)
                                onNavigateBack()
                            }
                        }
                    ) {
                        Text(
                            "Simpan",
                            color = BibitGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview Card
            if (billName.isNotEmpty() || estimatedAmount.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Preview",
                            fontSize = 12.sp,
                            color = BibitDarkGreen,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = billName.ifEmpty { "Nama Tagihan" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (billName.isEmpty()) Color(0xFF999999) else Color(0xFF333333)
                        )
                        Text(
                            text = if (estimatedAmount.isNotEmpty()) {
                                try {
                                    currencyFormat.format(estimatedAmount.replace(",", "").replace(".", "").toDouble())
                                } catch (e: Exception) {
                                    "Rp 0"
                                }
                            } else "Rp 0",
                            fontSize = 14.sp,
                            color = if (estimatedAmount.isEmpty()) Color(0xFF999999) else BibitDarkGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${selectedCycle.icon} ${selectedCycle.label} • ${dateFormat.format(selectedDate)}",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            // Form Fields
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bill Name
                OutlinedTextField(
                    value = billName,
                    onValueChange = {
                        billName = it
                        nameError = ""
                    },
                    label = { Text("Nama Tagihan") },
                    placeholder = { Text("Contoh: Listrik PLN") },
                    leadingIcon = {
                        Icon(Icons.Default.Receipt, contentDescription = null)
                    },
                    isError = nameError.isNotEmpty(),
                    supportingText = if (nameError.isNotEmpty()) {
                        { Text(nameError, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    )
                )

                // Estimated Amount
                OutlinedTextField(
                    value = estimatedAmount,
                    onValueChange = {
                        // Only allow numbers
                        val filtered = it.filter { char -> char.isDigit() }
                        estimatedAmount = filtered
                        amountError = ""
                    },
                    label = { Text("Estimasi Jumlah") },
                    placeholder = { Text("450000") },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError.isNotEmpty(),
                    supportingText = if (amountError.isNotEmpty()) {
                        { Text(amountError, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    )
                )

                // Due Date
                OutlinedTextField(
                    value = dateFormat.format(selectedDate),
                    onValueChange = { },
                    label = { Text("Tanggal Jatuh Tempo") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    )
                )

                // Repeat Cycle
                ExposedDropdownMenuBox(
                    expanded = showCycleDropdown,
                    onExpandedChange = { showCycleDropdown = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedCycle.icon} ${selectedCycle.label}",
                        onValueChange = { },
                        label = { Text("Siklus Pengulangan") },
                        leadingIcon = {
                            Icon(Icons.Default.Repeat, contentDescription = null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCycleDropdown)
                        },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BibitGreen,
                            focusedLabelColor = BibitGreen
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showCycleDropdown,
                        onDismissRequest = { showCycleDropdown = false }
                    ) {
                        RepeatCycle.values().forEach { cycle ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(cycle.icon)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(cycle.label)
                                    }
                                },
                                onClick = {
                                    selectedCycle = cycle
                                    showCycleDropdown = false
                                }
                            )
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan (Opsional)") },
                    placeholder = { Text("Tambahkan catatan...") },
                    leadingIcon = {
                        Icon(Icons.Default.Notes, contentDescription = null)
                    },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    )
                )

                // Reminder Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Pengaturan Pengingat",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BibitDarkGreen
                        )

                        // Enable Reminder Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Aktifkan Pengingat",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Notifikasi untuk mengingatkan tagihan",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                            Switch(
                                checked = isReminderEnabled,
                                onCheckedChange = { isReminderEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BibitGreen
                                )
                            )
                        }

                        if (isReminderEnabled) {
                            // Reminder Time
                            OutlinedTextField(
                                value = reminderTime,
                                onValueChange = { },
                                label = { Text("Waktu Pengingat") },
                                leadingIcon = {
                                    Icon(Icons.Default.Schedule, contentDescription = null)
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTimePicker = true },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BibitGreen,
                                    focusedLabelColor = BibitGreen
                                )
                            )

                            // Days Before
                            OutlinedTextField(
                                value = reminderDaysBefore.toString(),
                                onValueChange = {
                                    it.toIntOrNull()?.let { days ->
                                        if (days in 0..30) reminderDaysBefore = days
                                    }
                                },
                                label = { Text("Hari Sebelum Jatuh Tempo") },
                                leadingIcon = {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null)
                                },
                                suffix = { Text("hari") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BibitGreen,
                                    focusedLabelColor = BibitGreen
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Date(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = BibitGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = reminderTime.split(":")[0].toInt(),
            initialMinute = reminderTime.split(":")[1].toInt()
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Pilih Waktu") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        reminderTime = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = BibitGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AddRecurringBillScreenPreview() {
    AddRecurringBillScreen(rememberNavController())
}