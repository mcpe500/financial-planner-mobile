package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.model.TransactionPayload
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModel
import java.util.Date

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val ExpenseRed = Color(0xFFFF7043)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedPocket by remember { mutableStateOf("Cash") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var note by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var hasAttachment by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Transaction",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = { navController.navigate("scan_receipt") }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scan Receipt")
                        }
                        IconButton(onClick = { navController.navigate("voice_input") }) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice Input")
                        }
                        IconButton(onClick = { navController.navigate("qr_scan") }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Scan")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Selection
            TransactionTypeSelector(
                selectedType = transactionType,
                onTypeChange = { transactionType = it }
            )

            // Amount Input
            AmountInputCard(
                amount = amount,
                onAmountChange = { amount = it },
                transactionType = transactionType
            )

            // Date Selection
            DateSelectionCard(
                selectedDate = selectedDate,
                onDateChange = { selectedDate = it }
            )

            // Pocket Selection
            DropdownCard(
                title = "Pocket",
                selectedValue = selectedPocket,
                options = listOf("Cash", "Bank", "E-Wallet", "Credit Card"),
                onValueChange = { selectedPocket = it }
            )

            // Category Selection
            DropdownCard(
                title = "Category",
                selectedValue = selectedCategory,
                options = if (transactionType == TransactionType.INCOME)
                    listOf("Salary", "Freelance", "Investment", "Gift", "Other")
                else
                    listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Other"),
                onValueChange = { selectedCategory = it }
            )

            // Note Input
            NoteInputCard(
                note = note,
                onNoteChange = { note = it }
            )

            // Tags Selection
            TagSelectionCard(
                selectedTags = selectedTags,
                onTagsChange = { selectedTags = it }
            )

            // Attachment
            AttachmentCard(
                hasAttachment = hasAttachment,
                onAttachmentChange = { hasAttachment = it }
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Reset form
                        amount = ""
                        note = ""
                        selectedTags = setOf()
                        hasAttachment = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BibitGreen
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.foundation.BorderStroke(1.dp, BibitGreen).brush
                    )
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = {
                        // Buat entity dan payload dari input user
                        val now = Date()
                        val entity = TransactionEntity(
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            date = now,
                            description = note,
                            categoryId = null, // mapping sesuai kebutuhan
                            type = if (transactionType == TransactionType.INCOME) "income" else "expense",
                            userId = "user_id", // ganti dengan userIdProvider jika ada
                            accountId = null,
                            tags = selectedTags.joinToString(","),
                            location = null,
                            receiptImagePath = null,
                            isRecurring = false,
                            recurringType = null
                        )
                        val payload = TransactionPayload(
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            type = if (transactionType == TransactionType.INCOME) "income" else "expense",
                            category = selectedCategory,
                            description = note,
                            date = now.toInstant().toString(),
                            merchant_name = null,
                            location = null,
                            receipt_id = null,
                            items = null,
                            notes = note
                        )
                        transactionViewModel.addTransactionLocalAndRemote(entity, payload)
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save Transaction")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeChange: (TransactionType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Transaction Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TransactionType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = selectedType == type,
                                onClick = { onTypeChange(type) },
                                role = Role.RadioButton
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { onTypeChange(type) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BibitGreen
                            )
                        )
                        Text(
                            text = if (type == TransactionType.INCOME) "Income" else "Expense",
                            modifier = Modifier.padding(start = 8.dp),
                            color = DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AmountInputCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    transactionType: TransactionType
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Amount",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Enter amount") },
                leadingIcon = {
                    Text(
                        "$",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (transactionType == TransactionType.INCOME) BibitGreen else ExpenseRed
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BibitGreen,
                    focusedLabelColor = BibitGreen
                )
            )
        }
    }
}

@Composable
private fun DateSelectionCard(
    selectedDate: String,
    onDateChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Date",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = selectedDate,
                onValueChange = { },
                label = { Text("Select date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { /* Open date picker */ }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BibitGreen,
                    focusedLabelColor = BibitGreen
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownCard(
    title: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedValue,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select $title") },
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
    }
}

@Composable
private fun NoteInputCard(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Note",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Add a note (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BibitGreen,
                    focusedLabelColor = BibitGreen
                )
            )
        }
    }
}

@Composable
private fun TagSelectionCard(
    selectedTags: Set<String>,
    onTagsChange: (Set<String>) -> Unit
) {
    val availableTags = listOf("work", "personal", "emergency", "monthly", "weekly", "food", "transport")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tags",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Tag chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableTags.chunked(3).forEach { rowTags ->
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTags.forEach { tag ->
                            FilterChip(
                                selected = selectedTags.contains(tag),
                                onClick = {
                                    if (selectedTags.contains(tag)) {
                                        onTagsChange(selectedTags - tag)
                                    } else {
                                        onTagsChange(selectedTags + tag)
                                    }
                                },
                                label = { Text(tag) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BibitLightGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentCard(
    hasAttachment: Boolean,
    onAttachmentChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Attachment",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedButton(
                onClick = { onAttachmentChange(!hasAttachment) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (hasAttachment) BibitGreen else MediumGray
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (hasAttachment) BibitGreen else MediumGray
                    ).brush
                )
            ) {
                Icon(
                    if (hasAttachment) Icons.Default.CheckCircle else Icons.Default.AttachFile,
                    contentDescription = "Attach File",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (hasAttachment) "File Attached" else "Attach Photo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    AddTransactionScreen(rememberNavController())
}
