package com.example.financialplannerapp.ui.screen.transaction

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.semantics.Role
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.ui.model.Wallet
import com.example.financialplannerapp.ui.viewmodel.AddTransactionViewModel
import com.example.financialplannerapp.ui.viewmodel.AddTransactionViewModelFactory
import com.example.financialplannerapp.ui.viewmodel.toWalletUiModel
import java.util.*

enum class TransactionType {
    INCOME, EXPENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    
    val viewModel: AddTransactionViewModel = viewModel(
        factory = AddTransactionViewModelFactory(
            transactionRepository = application.appContainer.transactionRepository
        )
    )
    
    val state by viewModel.state
    
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedPocket by remember { mutableStateOf("Cash") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var note by remember { mutableStateOf("") }
    var selectedWallet: Wallet? by remember { mutableStateOf<Wallet?>(null) }

    val tokenManager = application.appContainer.tokenManager
    val userId = tokenManager.getUserId() ?: "local_user"
    val userEmail = tokenManager.getUserEmail() ?: "guest"
    val walletEntities by application.appContainer.walletRepository.getWalletsByUserEmail(userEmail).collectAsState(initial = emptyList())
    val wallets = walletEntities.map { it.toWalletUiModel() }

    // Handle state changes
    LaunchedEffect(state) {
        when {
            state.isSuccess -> {
                Toast.makeText(context, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                navController.navigateUp()
            }
            state.error != null -> {
                Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Transaction",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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

            // Wallet Selection Dropdown (insert before Pocket Selection)
            DropdownCard(
                title = "Wallet",
                selectedValue = selectedWallet?.name ?: "Select Wallet",
                options = wallets.map { wallet -> wallet.name },
                onValueChange = { name ->
                    selectedWallet = wallets.find { wallet -> wallet.name == name }
                }
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

            Spacer(modifier = Modifier.height(16.dp))

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
                        viewModel.resetState()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Reset")
                }
                
                Button(
                    onClick = {
                        if (amount.isNotBlank() && selectedWallet != null) {
                            viewModel.createTransaction(
                                userId = userId,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                type = if (transactionType == TransactionType.INCOME) "INCOME" else "EXPENSE",
                                date = Date(),
                                pocket = selectedPocket,
                                category = selectedCategory,
                                note = note.takeIf { it.isNotBlank() },
                                tags = null, // Set to null since we're not using tags for now
                                walletId = selectedWallet!!.id // Use the selected wallet's id
                            )
                        }
                    },
                    enabled = amount.isNotBlank() && !state.isLoading && selectedWallet != null,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save")
                    }
                }
            }
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
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Transaction Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TransactionType.values().forEach { type ->
                    val isSelected = selectedType == type
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = isSelected,
                                onClick = { onTypeChange(type) },
                                role = Role.RadioButton
                            )
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                                       else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (type == TransactionType.INCOME) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = type.name,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = type.name,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Amount",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    // Allow only numbers and a single decimal point
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        onAmountChange(newValue)
                    }
                },
                label = { Text("Enter amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { 
                    Icon(
                        Icons.Default.AttachMoney, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Date",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val dateOptions = listOf("Today", "Yesterday", "Custom")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dateOptions.forEach { date ->
                    val isSelected = selectedDate == date
                    OutlinedButton(
                        onClick = { onDateChange(date) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(date, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

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
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Box {
                OutlinedTextField(
                    value = selectedValue,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Toggle Dropdown",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
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
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Note (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Add a note") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                leadingIcon = { 
                    Icon(
                        Icons.Default.Note, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
