package com.example.financialplannerapp.ui.screen.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.model.GoalCategory
import com.example.financialplannerapp.ui.model.Wallet
import com.example.financialplannerapp.ui.viewmodel.GoalViewModel
import com.example.financialplannerapp.ui.viewmodel.GoalViewModelFactory
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.financialplannerapp.core.util.getCurrentCurrencySymbol
import com.example.financialplannerapp.ui.theme.BibitGreen
import com.example.financialplannerapp.ui.theme.BibitLightGreen
import com.example.financialplannerapp.ui.theme.MediumGray
import com.example.financialplannerapp.ui.theme.DangerRed
import com.example.financialplannerapp.ui.theme.WarningOrange
import com.example.financialplannerapp.ui.theme.MotivationalOrange
import com.example.financialplannerapp.ui.theme.DarkGray
import com.example.financialplannerapp.ui.theme.SoftGray

data class GoalTemplate(
    val id: String,
    val name: String,
    val icon: String,
    val category: GoalCategory,
    val suggestedAmount: Double? = null,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(application) }
    val userId = tokenManager.getUserEmail() ?: "guest"
    val goalViewModel: GoalViewModel = viewModel(
        factory = GoalViewModelFactory(application.appContainer.goalRepository, tokenManager, application.appContainer.walletRepository)
    )
    val walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(application.appContainer.walletRepository, tokenManager)
    )

    // State
    val wallets by walletViewModel.wallets.collectAsState()
    var selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var currentAmount by remember { mutableStateOf("0") }
    var selectedCategory by remember { mutableStateOf(GoalCategory.SAVINGS) }
    var targetDate by remember { mutableStateOf(Date()) }
    var priority by remember { mutableStateOf("Medium") }
    var isPriorityDropdownExpanded by remember { mutableStateOf(false) }
    val priorities = listOf("High", "Medium", "Low")
    var description by remember { mutableStateOf("") }

    var isWalletDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategorySelector by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = targetDate.time)

    LaunchedEffect(Unit) {
        walletViewModel.loadWallets()
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        targetDate = Date(it)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Goal") },
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
            // Wallet Dropdown
            ExposedDropdownMenuBox(
                expanded = isWalletDropdownExpanded,
                onExpandedChange = { isWalletDropdownExpanded = !isWalletDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedWallet?.name ?: "Select a Wallet",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Wallet", style = MaterialTheme.typography.bodyLarge) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isWalletDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                ExposedDropdownMenu(
                    expanded = isWalletDropdownExpanded,
                    onDismissRequest = { isWalletDropdownExpanded = false }
                ) {
                    wallets.forEach { wallet ->
                        DropdownMenuItem(
                            text = { Text(wallet.name, style = MaterialTheme.typography.bodyLarge) },
                            onClick = {
                                selectedWallet = wallet
                                isWalletDropdownExpanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Goal Name
            OutlinedTextField(
                value = goalName,
                onValueChange = { goalName = it },
                label = { Text("Goal Name (e.g., New Laptop)", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            // Target Amount
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = { Text("Target Amount", style = MaterialTheme.typography.bodyLarge) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            // Target Date
            OutlinedTextField(
                value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(targetDate),
                onValueChange = {},
                readOnly = true,
                label = { Text("Target Date", style = MaterialTheme.typography.bodyLarge) },
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )
            
            StringCycleDropDown(
                label = "Priority",
                options = priorities,
                selected = priority,
                onSelected = { priority = it },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val amount = targetAmount.toDoubleOrNull()
                    if (selectedWallet != null && goalName.isNotBlank() && amount != null) {
                        goalViewModel.addGoal(
                            walletId = selectedWallet!!.id,
                            name = goalName,
                            targetAmount = amount,
                            currentAmount = 0.0,
                            targetDate = targetDate,
                            priority = priority
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedWallet != null && goalName.isNotBlank() && targetAmount.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Goal", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun StringCycleDropDown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun GoalPreviewCard(
    name: String,
    targetAmount: Double,
    targetDate: String,
    wallet: String,
    icon: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Preview Tujuan",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = icon,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Target",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Text(
                text = getCurrentCurrencySymbol() + " ${String.format("%,.0f", targetAmount)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Deadline: $targetDate",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Text(
                text = "Wallet: $wallet",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun GoalTemplatesCard(
    templates: List<GoalTemplate>,
    selectedTemplate: GoalTemplate?,
    onTemplateSelect: (GoalTemplate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Template Tujuan",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            templates.chunked(2).forEach { rowTemplates ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTemplates.forEach { template ->
                        GoalTemplateOption(
                            template = template,
                            isSelected = selectedTemplate?.id == template.id,
                            onClick = { onTemplateSelect(template) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if odd number of templates
                    if (rowTemplates.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun GoalTemplateOption(
    template: GoalTemplate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = template.icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = template.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            template.suggestedAmount?.let { amount ->
                Text(
                    text = "~" + getCurrentCurrencySymbol() + " ${String.format("%,.0f", amount)}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GoalNameCard(
    name: String,
    onNameChange: (String) -> Unit,
    selectedTemplate: GoalTemplate?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Nama Tujuan",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Masukkan nama tujuan") },
                placeholder = { Text("e.g., Liburan ke Bali") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            selectedTemplate?.let { template ->
                Text(
                    text = template.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TargetAmountCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    suggestedAmount: Double?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Target Jumlah",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Masukkan target jumlah") },
                placeholder = { Text("0") },
                leadingIcon = {
                    Text(
                        getCurrentCurrencySymbol(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            suggestedAmount?.let { suggested ->
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = "Suggestion",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Saran: " + getCurrentCurrencySymbol() + " ${String.format("%,.0f", suggested)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onAmountChange(suggested.toInt().toString()) },
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            "Gunakan",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetDateCard(
    date: String,
    onDateChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Target Tanggal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                label = { Text("Pilih target tanggal") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { /* Open date picker */ }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = "Pilih tanggal yang realistis untuk mencapai tujuan Anda",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSelectionCard(
    selectedWallet: String,
    wallets: List<String>,
    onWalletChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Wallet Tujuan",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedWallet,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih wallet") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    wallets.forEach { wallet ->
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

            Text(
                text = "Dana akan disimpan di wallet yang dipilih",
                fontSize = 12.sp,
                color = MediumGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun MotivationCard(selectedTemplate: GoalTemplate?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MotivationalOrange.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = "Motivation",
                tint = MotivationalOrange,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Tips Sukses",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
                Text(
                    text = getMotivationalTip(selectedTemplate?.category),
                    fontSize = 12.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun GoalSuccessDialog(
    isEditing: Boolean,
    goalName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = BibitGreen,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = if (isEditing) "Tujuan Diperbarui!" else "Tujuan Dibuat!",
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        text = {
            Text(
                text = if (isEditing)
                    "Tujuan \"$goalName\" telah berhasil diperbarui. Tetap semangat mencapai target!"
                else
                    "Tujuan \"$goalName\" telah dibuat. Mulai menabung sekarang untuk mewujudkan impian Anda!",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Mulai Menabung")
            }
        }
    )
}

private fun generateGoalTemplates(): List<GoalTemplate> {
    return listOf(
        GoalTemplate(
            id = "1",
            name = "Liburan",
            icon = "🏖️",
            category = GoalCategory.VACATION,
            suggestedAmount = 15000000.0,
            description = "Rencanakan liburan impian Anda"
        ),
        GoalTemplate(
            id = "2",
            name = "Dana Darurat",
            icon = "🛡️",
            category = GoalCategory.EMERGENCY_FUND,
            suggestedAmount = 50000000.0,
            description = "Siapkan dana untuk keadaan darurat"
        ),
        GoalTemplate(
            id = "3",
            name = "Beli Rumah",
            icon = "🏠",
            category = GoalCategory.HOME,
            suggestedAmount = 500000000.0,
            description = "Wujudkan impian memiliki rumah"
        ),
        GoalTemplate(
            id = "4",
            name = "Beli Mobil",
            icon = "🚗",
            category = GoalCategory.VEHICLE,
            suggestedAmount = 200000000.0,
            description = "Dapatkan kendaraan impian Anda"
        ),
        GoalTemplate(
            id = "5",
            name = "Pendidikan",
            icon = "📚",
            category = GoalCategory.EDUCATION,
            suggestedAmount = 100000000.0,
            description = "Investasi untuk masa depan yang cerah"
        ),
        GoalTemplate(
            id = "6",
            name = "Investasi",
            icon = "📈",
            category = GoalCategory.INVESTMENT,
            suggestedAmount = 25000000.0,
            description = "Mulai berinvestasi untuk masa depan"
        )
    )
}

private fun getMotivationalTip(category: GoalCategory?): String {
    return when (category) {
        GoalCategory.VACATION -> "Buat rencana detail dan mulai menabung sedikit demi sedikit setiap bulan."
        GoalCategory.EMERGENCY_FUND -> "Dana darurat sebaiknya 6-12 kali pengeluaran bulanan Anda."
        GoalCategory.HOME -> "Pertimbangkan untuk menabung minimal 20% dari harga rumah sebagai DP."
        GoalCategory.VEHICLE -> "Jangan lupa hitung biaya pajak, asuransi, dan perawatan kendaraan."
        GoalCategory.EDUCATION -> "Investasi terbaik adalah investasi untuk diri sendiri dan masa depan."
        GoalCategory.INVESTMENT -> "Mulai dengan jumlah kecil dan tingkatkan secara bertahap."
        else -> "Konsistensi adalah kunci sukses mencapai tujuan finansial Anda."
    }
}

@Preview(showBackground = true)
@Composable
fun CreateGoalScreenPreview() {
    CreateGoalScreen(rememberNavController())
}
