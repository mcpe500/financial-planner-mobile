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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferBetweenWalletsScreen(navController: NavController) {
    var fromWallet by remember { mutableStateOf("") }
    var toWallet by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    val wallets = remember { generateMockWallets() }
    val walletNames = wallets.map { "${it.name} ($${String.format("%.2f", it.balance)})" }

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
                fromWallet = fromWallet,
                toWallet = toWallet,
                amount = amount.toDoubleOrNull() ?: 0.0
            )

            // From Wallet Selection
            WalletSelectionCard(
                title = "From Wallet",
                selectedWallet = fromWallet,
                walletOptions = walletNames.filter { it != toWallet },
                onWalletChange = { fromWallet = it },
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
                selectedWallet = toWallet,
                walletOptions = walletNames.filter { it != fromWallet },
                onWalletChange = { toWallet = it },
                icon = Icons.Default.AccountBalance
            )

            // Amount Input
            AmountInputCard(
                amount = amount,
                onAmountChange = { amount = it }
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
                enabled = fromWallet.isNotEmpty() && toWallet.isNotEmpty() && amount.isNotEmpty(),
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

    // Confirmation Dialog
    if (showConfirmation) {
        TransferConfirmationDialog(
            fromWallet = fromWallet,
            toWallet = toWallet,
            amount = amount.toDoubleOrNull() ?: 0.0,
            note = note,
            onConfirm = {
                // Handle transfer
                showConfirmation = false
                navController.navigateUp()
            },
            onDismiss = { showConfirmation = false }
        )
    }
}

@Composable
private fun TransferPreviewCard(
    fromWallet: String,
    toWallet: String,
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
                        text = fromWallet.takeWhile { it != '(' }.trim().ifEmpty { "Select wallet" },
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
                        text = toWallet.takeWhile { it != '(' }.trim().ifEmpty { "Select wallet" },
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
    selectedWallet: String,
    walletOptions: List<String>,
    onWalletChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
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
                    value = selectedWallet,
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
                    walletOptions.forEach { wallet ->
                        DropdownMenuItem(
                            text = { Text(wallet) },
                            onClick = {
                                onWalletChange(wallet)
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
                onValueChange = onAmountChange,
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
    fromWallet: String,
    toWallet: String,
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
                TransferDetailRow("From", fromWallet.takeWhile { it != '(' }.trim())
                TransferDetailRow("To", toWallet.takeWhile { it != '(' }.trim())
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
    TransferBetweenWalletsScreen(rememberNavController())
}
