package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import java.text.SimpleDateFormat
import java.util.*

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val MotivationalOrange = Color(0xFFFF9800)

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
fun CreateGoalScreen(navController: NavController, goalId: String? = null) {
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("31 Desember 2024") }
    var selectedWallet by remember { mutableStateOf("BCA Savings") }
    var selectedTemplate by remember { mutableStateOf<GoalTemplate?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val templates = remember { generateGoalTemplates() }
    val wallets = listOf("Cash Wallet", "BCA Savings", "GoPay", "Dana", "Emergency Fund")
    val isEditing = goalId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Tujuan" else "Buat Tujuan Baru",
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
            GoalPreviewCard(
                name = goalName.ifEmpty { selectedTemplate?.name ?: "Tujuan Baru" },
                targetAmount = targetAmount.toDoubleOrNull() ?: selectedTemplate?.suggestedAmount ?: 0.0,
                targetDate = targetDate,
                wallet = selectedWallet,
                icon = selectedTemplate?.icon ?: "🎯"
            )

            // Goal Templates
            if (!isEditing) {
                GoalTemplatesCard(
                    templates = templates,
                    selectedTemplate = selectedTemplate,
                    onTemplateSelect = { template ->
                        selectedTemplate = template
                        goalName = template.name
                        template.suggestedAmount?.let {
                            targetAmount = it.toInt().toString()
                        }
                    }
                )
            }

            // Goal Name Input
            GoalNameCard(
                name = goalName,
                onNameChange = { goalName = it },
                selectedTemplate = selectedTemplate
            )

            // Target Amount Input
            TargetAmountCard(
                amount = targetAmount,
                onAmountChange = { targetAmount = it },
                suggestedAmount = selectedTemplate?.suggestedAmount
            )

            // Target Date Selection
            TargetDateCard(
                date = targetDate,
                onDateChange = { targetDate = it }
            )

            // Wallet Selection
            WalletSelectionCard(
                selectedWallet = selectedWallet,
                wallets = wallets,
                onWalletChange = { selectedWallet = it }
            )

            // Motivation Card
            MotivationCard(selectedTemplate)

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
                    Text("Batal")
                }

                Button(
                    onClick = { showSuccessDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = goalName.isNotEmpty() && targetAmount.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isEditing) "Update" else "Buat Tujuan")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        GoalSuccessDialog(
            isEditing = isEditing,
            goalName = goalName,
            onDismiss = {
                showSuccessDialog = false
                navController.navigateUp()
            }
        )
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
        colors = CardDefaults.cardColors(containerColor = BibitGreen)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Preview Tujuan",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
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
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Target",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "Rp ${String.format("%,.0f", targetAmount)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Deadline: $targetDate",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "Wallet: $wallet",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Template Tujuan",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
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
            containerColor = if (isSelected) BibitLightGreen.copy(alpha = 0.2f) else SoftGray
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BibitGreen) else null
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
                color = if (isSelected) BibitGreen else DarkGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            template.suggestedAmount?.let { amount ->
                Text(
                    text = "~Rp ${String.format("%,.0f", amount)}",
                    fontSize = 10.sp,
                    color = MediumGray
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Nama Tujuan",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Masukkan nama tujuan") },
                placeholder = { Text("e.g., Liburan ke Bali") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BibitGreen,
                    focusedLabelColor = BibitGreen
                )
            )

            selectedTemplate?.let { template ->
                Text(
                    text = template.description,
                    fontSize = 12.sp,
                    color = MediumGray,
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Target Jumlah",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Masukkan target jumlah") },
                placeholder = { Text("0") },
                leadingIcon = {
                    Text(
                        "Rp",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BibitGreen
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BibitGreen,
                    focusedLabelColor = BibitGreen
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
                        tint = MotivationalOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Saran: Rp ${String.format("%,.0f", suggested)}",
                        fontSize = 12.sp,
                        color = MotivationalOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onAmountChange(suggested.toInt().toString()) },
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            "Gunakan",
                            fontSize = 10.sp,
                            color = BibitGreen
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Target Tanggal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
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
                    focusedBorderColor = BibitGreen,
                    focusedLabelColor = BibitGreen
                )
            )

            Text(
                text = "Pilih tanggal yang realistis untuk mencapai tujuan Anda",
                fontSize = 12.sp,
                color = MediumGray,
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Wallet Tujuan",
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
                    value = selectedWallet,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih wallet") },
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
            category = GoalCategory.EMERGENCY,
            suggestedAmount = 50000000.0,
            description = "Siapkan dana untuk keadaan darurat"
        ),
        GoalTemplate(
            id = "3",
            name = "Beli Rumah",
            icon = "🏠",
            category = GoalCategory.HOUSE,
            suggestedAmount = 500000000.0,
            description = "Wujudkan impian memiliki rumah"
        ),
        GoalTemplate(
            id = "4",
            name = "Beli Mobil",
            icon = "🚗",
            category = GoalCategory.CAR,
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
        GoalCategory.EMERGENCY -> "Dana darurat sebaiknya 6-12 kali pengeluaran bulanan Anda."
        GoalCategory.HOUSE -> "Pertimbangkan untuk menabung minimal 20% dari harga rumah sebagai DP."
        GoalCategory.CAR -> "Jangan lupa hitung biaya pajak, asuransi, dan perawatan kendaraan."
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
