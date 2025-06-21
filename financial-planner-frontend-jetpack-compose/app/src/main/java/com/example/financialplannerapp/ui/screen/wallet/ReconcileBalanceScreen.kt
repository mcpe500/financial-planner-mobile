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
import androidx.compose.ui.graphics.vector.ImageVector // Keep this for Icons.Default
import androidx.compose.ui.platform.LocalContext
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
// No need to import 'icon' extension here unless you explicitly use WalletType.icon in this file
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

// --- REMOVED DUPLICATE: Wallet data class, WalletType enum, and WalletType.icon extension property ---
// These are now defined ONLY in 'com.example.financialplannerapp.ui.model.WalletUiModels.kt'

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReconcileBalanceScreen(
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
    var selectedWalletOption by remember { mutableStateOf("") } // Format: "Wallet Name ($Balance)"
    var realBalanceInput by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    // Observe wallets from ViewModel
    val allWallets by walletViewModel.wallets.collectAsState()
    val isLoading by walletViewModel.isLoading.collectAsState()
    val error by walletViewModel.error.collectAsState()

    // Find the selected Wallet UI model based on the string option
    val selectedWalletData = remember(selectedWalletOption, allWallets) {
        val name = selectedWalletOption.substringBefore(" (").trim()
        allWallets.find { it.name == name }
    }

    val appBalance = selectedWalletData?.balance ?: 0.0
    val realBalanceValue = realBalanceInput.toDoubleOrNull() ?: 0.0
    val difference = realBalanceValue - appBalance

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reconcile Balance",
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
                // Info Card
                ReconcileInfoCard()

                // Wallet Selection
                WalletSelectionCard(
                    selectedWalletOption = selectedWalletOption,
                    wallets = allWallets, // Pass the actual wallets from ViewModel
                    onWalletChange = { selectedWalletOption = it }
                )

                // Balance Comparison
                if (selectedWalletOption.isNotEmpty()) {
                    BalanceComparisonCard(
                        appBalance = appBalance,
                        realBalance = realBalanceValue,
                        difference = difference
                    )
                }

                // Real Balance Input
                RealBalanceInputCard(
                    realBalance = realBalanceInput,
                    onRealBalanceChange = { realBalanceInput = it }
                )

                // Note Input
                ReconcileNoteCard(
                    note = note,
                    onNoteChange = { note = it }
                )

                // Reconcile Button
                Button(
                    onClick = { showConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedWalletOption.isNotEmpty() && realBalanceInput.toDoubleOrNull() != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Balance,
                        contentDescription = "Reconcile",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Reconcile Balance",
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
        ReconcileConfirmationDialog(
            walletName = selectedWalletData?.name ?: "N/A", // Use actual wallet name
            appBalance = appBalance,
            realBalance = realBalanceValue,
            difference = difference,
            note = note,
            onConfirm = {
                // Handle reconciliation logic:
                // 1. Update the balance of the selected wallet in the database
                // 2. Potentially add a reconciliation transaction record
                selectedWalletData?.let { walletToUpdate ->
                    val updatedWallet = walletToUpdate.copy(balance = realBalanceValue)
                    walletViewModel.updateWallet(updatedWallet) // Call ViewModel to update
                    // TODO: Add logic to record reconciliation transaction if needed
                }
                showConfirmation = false
                navController.navigateUp()
            },
            onDismiss = { showConfirmation = false }
        )
    }
}

@Composable
private fun ReconcileInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "Info",
                tint = BibitGreen,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Balance Reconciliation",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Compare your app balance with the real balance from your bank or wallet. This helps keep your records accurate and up-to-date.",
                    fontSize = 14.sp,
                    color = MediumGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSelectionCard(
    selectedWalletOption: String, // Renamed to avoid conflict
    wallets: List<Wallet>, // Now expects List<Wallet> from UI model
    onWalletChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    // Map Wallet UI models to string options for the dropdown
    val walletOptions = wallets.map { "${it.name} ($${String.format("%.2f", it.balance)})" }

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
                text = "Select Wallet",
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
                    value = selectedWalletOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Choose wallet to reconcile") },
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
private fun BalanceComparisonCard(
    appBalance: Double,
    realBalance: Double,
    difference: Double
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
                text = "Balance Comparison",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // App Balance
            BalanceRow(
                label = "App Balance",
                amount = appBalance,
                icon = Icons.Default.PhoneAndroid,
                color = BibitGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Real Balance
            BalanceRow(
                label = "Real Balance",
                amount = realBalance,
                icon = Icons.Default.AccountBalance,
                color = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Difference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (difference >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = "Difference",
                        tint = if (difference >= 0) BibitGreen else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Difference",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                }

                Text(
                    text = "${if (difference >= 0) "+" else ""}$${String.format("%.2f", difference)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (difference >= 0) BibitGreen else Color.Red
                )
            }

            if (difference != 0.0) {
                Text(
                    text = if (difference > 0)
                        "Your real balance is higher than the app balance"
                    else
                        "Your real balance is lower than the app balance",
                    fontSize = 12.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun BalanceRow(
    label: String,
    amount: Double,
    icon: ImageVector, // Changed to ImageVector
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )
        }

        Text(
            text = "$${String.format("%.2f", amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun RealBalanceInputCard(
    realBalance: String,
    onRealBalanceChange: (String) -> Unit
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
                text = "Real Balance",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = realBalance,
                onValueChange = onRealBalanceChange,
                label = { Text("Enter actual balance") },
                placeholder = { Text("Check your bank/wallet app") },
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

            Text(
                text = "Enter the current balance shown in your bank or wallet app",
                fontSize = 12.sp,
                color = MediumGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ReconcileNoteCard(
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
                text = "Reconciliation Note",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Add a note (optional)") },
                placeholder = { Text("e.g., Bank statement reconciliation") },
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
private fun ReconcileConfirmationDialog(
    walletName: String,
    appBalance: Double,
    realBalance: Double,
    difference: Double,
    note: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Reconciliation",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    text = "This will update your wallet balance:",
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ReconcileDetailRow("Wallet", walletName)
                ReconcileDetailRow("Current App Balance", "$${String.format("%.2f", appBalance)}")
                ReconcileDetailRow("Real Balance", "$${String.format("%.2f", realBalance)}")
                ReconcileDetailRow(
                    "Adjustment",
                    "${if (difference >= 0) "+" else ""}$${String.format("%.2f", difference)}",
                    if (difference >= 0) BibitGreen else Color.Red
                )

                if (note.isNotEmpty()) {
                    ReconcileDetailRow("Note", note)
                }

                if (difference != 0.0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A reconciliation transaction will be created to adjust the balance.",
                        fontSize = 12.sp,
                        color = MediumGray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
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
                Text("Confirm Reconciliation")
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
private fun ReconcileDetailRow(
    label: String,
    value: String,
    valueColor: Color = DarkGray
) {
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
            color = valueColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReconcileBalanceScreenPreview() {
    ReconcileBalanceScreen(rememberNavController(), userId = "preview_user_id_123")
}