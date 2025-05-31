package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
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
fun AddWalletScreen(navController: NavController) {
    var walletName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(WalletType.CASH) }
    var initialBalance by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.Wallet) }
    var selectedColor by remember { mutableStateOf(BibitGreen) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add New Wallet",
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
            // Preview Card
            WalletPreviewCard(
                name = walletName.ifEmpty { "New Wallet" },
                type = selectedType,
                balance = initialBalance.toDoubleOrNull() ?: 0.0,
                icon = selectedIcon,
                color = selectedColor
            )

            // Wallet Name Input
            WalletNameCard(
                name = walletName,
                onNameChange = { walletName = it }
            )

            // Wallet Type Selection
            WalletTypeCard(
                selectedType = selectedType,
                onTypeChange = { selectedType = it }
            )

            // Initial Balance Input
            InitialBalanceCard(
                balance = initialBalance,
                onBalanceChange = { initialBalance = it }
            )

            // Icon Selection
            IconSelectionCard(
                selectedIcon = selectedIcon,
                onIconChange = { selectedIcon = it }
            )

            // Color Selection
            ColorSelectionCard(
                selectedColor = selectedColor,
                onColorChange = { selectedColor = it }
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BibitGreen
                    )
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        // Save wallet logic
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    ),
                    enabled = walletName.isNotEmpty()
                ) {
                    Text("Save Wallet")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WalletPreviewCard(
    name: String,
    type: WalletType,
    balance: Double,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = name,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Initial Balance",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "$${String.format("%.2f", balance)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun WalletNameCard(
    name: String,
    onNameChange: (String) -> Unit
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
                text = "Wallet Name",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Enter wallet name") },
                placeholder = { Text("e.g., Cash Wallet, BCA Savings") },
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
private fun WalletTypeCard(
    selectedType: WalletType,
    onTypeChange: (WalletType) -> Unit
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
                text = "Wallet Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WalletType.values().forEach { type ->
                    WalletTypeOption(
                        type = type,
                        isSelected = selectedType == type,
                        onClick = { onTypeChange(type) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletTypeOption(
    type: WalletType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val typeInfo = getWalletTypeInfo(type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BibitGreen.copy(alpha = 0.1f) else SoftGray
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BibitGreen) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                typeInfo.icon,
                contentDescription = typeInfo.name,
                tint = if (isSelected) BibitGreen else MediumGray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = typeInfo.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) BibitGreen else DarkGray
                )
                Text(
                    text = typeInfo.description,
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BibitGreen
                )
            )
        }
    }
}

@Composable
private fun InitialBalanceCard(
    balance: String,
    onBalanceChange: (String) -> Unit
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
                text = "Initial Balance",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = balance,
                onValueChange = onBalanceChange,
                label = { Text("Enter initial balance") },
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

            Text(
                text = "You can always update this later",
                fontSize = 12.sp,
                color = MediumGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun IconSelectionCard(
    selectedIcon: ImageVector,
    onIconChange: (ImageVector) -> Unit
) {
    val availableIcons = listOf(
        Icons.Default.Wallet,
        Icons.Default.Money,
        Icons.Default.AccountBalance,
        Icons.Default.CreditCard,
        Icons.Default.TrendingUp,
        Icons.Default.Savings,
        Icons.Default.LocalAtm,
        Icons.Default.AccountBalanceWallet
    )

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
                text = "Choose Icon",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableIcons) { icon ->
                    IconOption(
                        icon = icon,
                        isSelected = selectedIcon == icon,
                        onClick = { onIconChange(icon) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IconOption(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                if (isSelected) BibitGreen else SoftGray,
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = "Icon",
            tint = if (isSelected) Color.White else MediumGray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ColorSelectionCard(
    selectedColor: Color,
    onColorChange: (Color) -> Unit
) {
    val availableColors = listOf(
        BibitGreen,
        Color(0xFF2196F3), // Blue
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF9800), // Orange
        Color(0xFFF44336), // Red
        Color(0xFF00BCD4), // Cyan
        Color(0xFF795548), // Brown
        Color(0xFF607D8B)  // Blue Grey
    )

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
                text = "Choose Color",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableColors) { color ->
                    ColorOption(
                        color = color,
                        isSelected = selectedColor == color,
                        onClick = { onColorChange(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class WalletTypeInfo(
    val name: String,
    val description: String,
    val icon: ImageVector
)

private fun getWalletTypeInfo(type: WalletType): WalletTypeInfo {
    return when (type) {
        WalletType.CASH -> WalletTypeInfo(
            "Cash",
            "Physical money you carry",
            Icons.Default.Money
        )
        WalletType.BANK -> WalletTypeInfo(
            "Bank Account",
            "Savings or checking account",
            Icons.Default.AccountBalance
        )
        WalletType.E_WALLET -> WalletTypeInfo(
            "E-Wallet",
            "Digital wallet like GoPay, OVO",
            Icons.Default.Wallet
        )
        WalletType.INVESTMENT -> WalletTypeInfo(
            "Investment",
            "Stocks, bonds, mutual funds",
            Icons.Default.TrendingUp
        )
        WalletType.DEBT -> WalletTypeInfo(
            "Debt/Credit",
            "Credit cards, loans",
            Icons.Default.CreditCard
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddWalletScreenPreview() {
    AddWalletScreen(rememberNavController())
}
