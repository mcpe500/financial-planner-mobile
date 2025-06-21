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
import androidx.compose.ui.platform.LocalContext // Needed for AppDatabase and ViewModelFactory
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Needed for viewModel()
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.data.local.AppDatabase // Import your AppDatabase
import com.example.financialplannerapp.data.repository.WalletRepositoryImpl // Import your repository impl
import com.example.financialplannerapp.ui.model.Wallet // Import your Wallet UI model
import com.example.financialplannerapp.ui.model.WalletType // Import WalletType enum
import com.example.financialplannerapp.ui.model.icon // Import WalletType.icon extension
import com.example.financialplannerapp.ui.model.toHex // Import Color.toHex extension
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory
import java.util.UUID // For generating unique IDs

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

// --- IMPORTANT: Removed duplicate WalletType, its icon extension, toHex, toColor, iconFromName ---
// These are now defined ONLY in 'com.example.financialplannerapp.ui.model.WalletUiModels.kt'
// and imported above.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWalletScreen(
    navController: NavController,
    // IMPORTANT: userId should be passed from the navigation arguments or a session manager
    userId: String = "default_user_id" // Provide a default or get it from navigation.
) {
    // Initialize ViewModel with necessary dependencies
    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            walletRepository = WalletRepositoryImpl(AppDatabase.getDatabase(LocalContext.current).walletDao()),
            userId = userId
        )
    )

    var walletName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(WalletType.CASH) }
    var initialBalance by remember { mutableStateOf("") }
    // selectedIcon is of type ImageVector
    var selectedIcon by remember { mutableStateOf(selectedType.icon) }
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
            // Preview Card - Correctly passes selectedIcon (which is ImageVector)
            WalletPreviewCard(
                name = walletName.ifEmpty { "New Wallet" },
                type = selectedType,
                balance = initialBalance.toDoubleOrNull() ?: 0.0,
                icon = selectedIcon, // <-- CORRECTED: Pass ImageVector directly
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
                onTypeChange = {
                    selectedType = it
                    // Update icon when type changes, but allow manual override later
                    selectedIcon = it.icon // This correctly uses WalletType.icon
                }
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
                        val newWallet = Wallet(
                            id = UUID.randomUUID().toString(), // Generate a unique ID for the UI model
                            name = walletName,
                            type = selectedType, // Use the WalletType enum directly
                            balance = initialBalance.toDoubleOrNull() ?: 0.0,
                            icon = selectedIcon, // Store ImageVector directly in UI model
                            color = selectedColor, // Store Color directly in UI model
                            isShared = false,
                            memberCount = 1
                        )
                        // ViewModel will handle mapping this UI Wallet to WalletEntity
                        viewModel.addWallet(newWallet)
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    ),
                    enabled = walletName.isNotEmpty() && initialBalance.toDoubleOrNull() != null
                ) {
                    Text("Save Wallet")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// These @Composable functions remain largely the same as they work with ImageVector and Color directly,
// which are the types present in the UI model.

@Composable
private fun WalletPreviewCard(
    name: String,
    type: WalletType,
    balance: Double,
    icon: ImageVector, // This is ImageVector, not WalletType.icon
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
                        icon, // Correct: icon is already an ImageVector
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) BibitLightGreen.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = type.icon, // Correct: Uses the WalletType.icon extension
            contentDescription = type.name,
            tint = if (isSelected) BibitGreen else MediumGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) BibitGreen else DarkGray
        )
        Spacer(modifier = Modifier.weight(1f))
        RadioButton(
            selected = isSelected,
            onClick = null, // Handled by the Row's clickable modifier
            colors = RadioButtonDefaults.colors(
                selectedColor = BibitGreen,
                unselectedColor = MediumGray
            )
        )
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
                onValueChange = { newValue ->
                    // Allow only digits and a single decimal point
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        onBalanceChange(newValue)
                    }
                },
                label = { Text("Enter initial balance") },
                placeholder = { Text("e.g., 1000.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Text("$", color = DarkGray) },
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
private fun IconSelectionCard(
    selectedIcon: ImageVector,
    onIconChange: (ImageVector) -> Unit
) {
    val availableIcons = remember {
        listOf(
            Icons.Default.Wallet,
            Icons.Default.Money,
            Icons.Default.AccountBalance,
            Icons.Default.CreditCard,
            Icons.Default.TrendingUp,
            Icons.Default.Savings,
            Icons.Default.Home,
            Icons.Default.DirectionsCar,
            Icons.Default.Fastfood,
            Icons.Default.MedicalServices,
            Icons.Default.School,
            Icons.Default.Smartphone
        )
    }

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
                text = "Select Icon",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
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
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) BibitLightGreen.copy(alpha = 0.4f) else SoftGray,
                shape = CircleShape
            )
            .shadow(if (isSelected) 4.dp else 1.dp, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) BibitGreen else MediumGray,
            modifier = Modifier.size(32.dp)
        )
    }
}


@Composable
private fun ColorSelectionCard(
    selectedColor: Color,
    onColorChange: (Color) -> Unit
) {
    val availableColors = remember {
        listOf(
            BibitGreen,
            Color(0xFF2196F3), // Blue
            Color(0xFFFFC107), // Amber
            Color(0xFF9C27B0), // Purple
            Color(0xFFFF5722), // Deep Orange
            Color(0xFF00BCD4), // Cyan
            Color(0xFFE91E63), // Pink
            Color(0xFF795548)  // Brown
        )
    }

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
                text = "Select Color",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
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
            .clickable(onClick = onClick)
            .background(color, CircleShape)
            .padding(4.dp), // Add padding for the border effect
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f), CircleShape), // Inner translucent circle
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddWalletScreenPreview() {
    // Provide a dummy userId for the preview, as AddWalletScreen now expects it.
    // In a real app, you'd pass this via navigation arguments.
    AddWalletScreen(navController = rememberNavController(), userId = "preview_user_id_123")
}