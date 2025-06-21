package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Needed for Icons.Default
import androidx.compose.ui.platform.LocalContext // Needed for ViewModelFactory
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // For ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// --- IMPORTANT IMPORTS FROM YOUR CENTRALIZED UI MODEL FILE ---
import com.example.financialplannerapp.ui.model.Wallet // Import the Wallet UI data class
import com.example.financialplannerapp.ui.model.WalletType // Import WalletType enum
// --- END IMPORTANT IMPORTS ---

import com.example.financialplannerapp.data.local.AppDatabase // Import your database
import com.example.financialplannerapp.data.repository.WalletRepositoryImpl // Import your repository implementation
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory


// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

// --- REMOVED DUPLICATE: Wallet data class and generateMockWallets() function ---
// These are now defined ONLY in 'com.example.financialplannerapp.ui.model.WalletUiModels.kt'
// and data will be fetched via ViewModel.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferBetweenWalletsScreen(
    navController: NavController,
    // For simplicity, we're hardcoding a userId for ViewModel factory.
    // In a real app, this should come from navigation arguments or a session manager.
    userId: String = "user123", // Default for preview/initial setup
    walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            walletRepository = WalletRepositoryImpl(AppDatabase.getDatabase(LocalContext.current).walletDao()),
            userId = userId
        )
    )
) {
    var selectedFromWalletOption by remember { mutableStateOf("") } // Format: "Wallet Name ($Balance)"
    var selectedToWalletOption by remember { mutableStateOf("") }   // Format: "Wallet Name ($Balance)"
    var amountInput by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    // Observe wallets from ViewModel
    val allWallets by walletViewModel.wallets.collectAsState()
    val isLoading by walletViewModel.isLoading.collectAsState()
    val error by walletViewModel.error.collectAsState()

    // Derived state for wallet options in dropdowns
    val walletOptions = remember(allWallets) {
        allWallets.map { "${it.name} ($${String.format("%.2f", it.balance)})" }
    }

    // Find the actual Wallet UI models based on selected options
    val fromWalletData = remember(selectedFromWalletOption, allWallets) {
        val name = selectedFromWalletOption.substringBefore(" (").trim()
        allWallets.find { it.name == name }
    }
    val toWalletData = remember(selectedToWalletOption, allWallets) {
        val name = selectedToWalletOption.substringBefore(" (").trim()
        allWallets.find { it.name == name }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transfer Between Wallets",
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
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error", color = Color.Red)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftGray)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transfer Preview Card
                TransferPreviewCard(
                    fromWalletName = selectedFromWalletOption.takeWhile { it != '(' }.trim(),
                    toWalletName = selectedToWalletOption.takeWhile { it != '(' }.trim(),
                    amount = amountInput.toDoubleOrNull() ?: 0.0
                )

                // From Wallet Selection
                WalletSelectionCard(
                    title = "From Wallet",
                    selectedWalletOption = selectedFromWalletOption,
                    walletOptions = walletOptions.filter { it != selectedToWalletOption },
                    onWalletChange = { selectedFromWalletOption = it },
                    icon = Icons.Default.AccountBalanceWallet
                )

                // Transfer Direction Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.SwapVert,
                        contentDescription = "Transfer",
                        tint = BibitGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // To Wallet Selection
                WalletSelectionCard(
                    title = "To Wallet",
                    selectedWalletOption = selectedToWalletOption,
                    walletOptions = walletOptions.filter { it != selectedFromWalletOption },
                    onWalletChange = { selectedToWalletOption = it },
                    icon = Icons.Default.AccountBalance
                )

                // Amount Input
                AmountInputCard(
                    amount = amountInput,
                    onAmountChange = { amountInput = it }
                )

                // Note Input
                NoteInputCard(
                    note = note,
                    onNoteChange = { note = it }
                )

                // Transfer Button
                Button(
                    onClick = { showConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedFromWalletOption.isNotEmpty() &&
                            selectedToWalletOption.isNotEmpty() &&
                            amountInput.toDoubleOrNull() != null &&
                            selectedFromWalletOption != selectedToWalletOption, // Prevent transfer to self
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Transfer",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Transfer Funds",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmation) {
        TransferConfirmationDialog(
            fromWalletName = selectedFromWalletOption.takeWhile { it != '(' }.trim(),
            toWalletName = selectedToWalletOption.takeWhile { it != '(' }.trim(),
            amount = amountInput.toDoubleOrNull() ?: 0.0,
            note = note,
            onConfirm = {
                // Handle transfer logic: update balances via ViewModel
                val transferAmount = amountInput.toDoubleOrNull() ?: 0.0
                if (fromWalletData != null && toWalletData != null && transferAmount > 0) {
                    val updatedFromWallet = fromWalletData.copy(balance = fromWalletData.balance - transferAmount)
                    val updatedToWallet = toWalletData.copy(balance = toWalletData.balance + transferAmount)

                    walletViewModel.updateWallet(updatedFromWallet)
                    walletViewModel.updateWallet(updatedToWallet)
                    // TODO: You might also want to record this as a transaction in your transaction history
                }
                showConfirmation = false
                navController.navigateUp()
            },
            onDismiss = { showConfirmation = false }
        )
    }
}

@Composable
private fun TransferPreviewCard(
    fromWalletName: String, // Renamed for clarity to avoid confusion with Wallet object
    toWalletName: String,   // Renamed for clarity
    amount: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BibitGreen)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Transfer Preview",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${String.format("%.2f", amount)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "From",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = fromWalletName.ifEmpty { "Select wallet" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "To",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "To",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = toWalletName.ifEmpty { "Select wallet" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSelectionCard(
    title: String,
    selectedWalletOption: String, // Renamed parameter
    walletOptions: List<String>,
    onWalletChange: (String) -> Unit,
    icon: ImageVector // Changed to ImageVector
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = BibitGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedWalletOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select wallet") },
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
                    walletOptions.forEach { walletOption -> // Use walletOption here
                        DropdownMenuItem(
                            text = { Text(walletOption) },
                            onClick = {
                                onWalletChange(walletOption)
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
private fun AmountInputCard(
    amount: String,
    onAmountChange: (String) -> Unit
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = "Amount",
                    tint = BibitGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Transfer Amount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    // Allow only numerical input (including decimal)
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        onAmountChange(newValue)
                    }
                },
                label = { Text("Enter amount") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Text(
                        "$",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BibitGreen
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    Icons.Default.Note,
                    contentDescription = "Note",
                    tint = BibitGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Transfer Note",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Add a note (optional)") },
                placeholder = { Text("e.g., Monthly allowance transfer") },
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
private fun TransferConfirmationDialog(
    fromWalletName: String,
    toWalletName: String,
    amount: Double,
    note: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Transfer",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    text = "Please confirm the transfer details:",
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TransferDetailRow("Amount", "$${String.format("%.2f", amount)}")
                TransferDetailRow("From", fromWalletName)
                TransferDetailRow("To", toWalletName)
                if (note.isNotEmpty()) {
                    TransferDetailRow("Note", note)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Confirm Transfer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TransferDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MediumGray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransferBetweenWalletsScreenPreview() {
    // For preview, provide a dummy NavController and userId
    TransferBetweenWalletsScreen(rememberNavController(), userId = "preview_user_id_123")
}