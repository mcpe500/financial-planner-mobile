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

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

data class BudgetCategory(
    val id: String,
    val name: String,
    val icon: String,
    val description: String
)

enum class BudgetPeriodType {
    WEEKLY, MONTHLY, CUSTOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetScreen(navController: NavController, budgetId: String? = null) {
    var selectedCategory by remember { mutableStateOf<BudgetCategory?>(null) }
    var budgetLimit by remember { mutableStateOf("") }
    var selectedPeriodType by remember { mutableStateOf(BudgetPeriodType.MONTHLY) }
    var startDate by remember { mutableStateOf("1 Desember 2024") }
    var endDate by remember { mutableStateOf("31 Desember 2024") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val categories = remember { generateBudgetCategories() }
    val isEditing = budgetId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Anggaran" else "Buat Anggaran",
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
            BudgetPreviewCard(
                category = selectedCategory,
                limit = budgetLimit.toDoubleOrNull() ?: 0.0,
                periodType = selectedPeriodType,
                startDate = startDate,
                endDate = endDate
            )

            // Category Selection
            CategorySelectionCard(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it }
            )

            // Budget Limit Input
            BudgetLimitCard(
                limit = budgetLimit,
                onLimitChange = { budgetLimit = it }
            )

            // Period Selection
            PeriodSelectionCard(
                selectedPeriodType = selectedPeriodType,
                onPeriodTypeChange = { selectedPeriodType = it }
            )

            // Date Range
            DateRangeCard(
                startDate = startDate,
                endDate = endDate,
                onStartDateChange = { startDate = it },
                onEndDateChange = { endDate = it },
                periodType = selectedPeriodType
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
                    Text("Batal")
                }

                Button(
                    onClick = { showSuccessDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = selectedCategory != null && budgetLimit.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isEditing) "Update" else "Simpan")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        SuccessDialog(
            isEditing = isEditing,
            onDismiss = {
                showSuccessDialog = false
                navController.navigateUp()
            }
        )
    }
}

@Composable
private fun BudgetPreviewCard(
    category: BudgetCategory?,
    limit: Double,
    periodType: BudgetPeriodType,
    startDate: String,
    endDate: String
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
                text = "Preview Anggaran",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category?.icon ?: "📊",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = category?.name ?: "Pilih Kategori",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = when (periodType) {
                            BudgetPeriodType.WEEKLY -> "Mingguan"
                            BudgetPeriodType.MONTHLY -> "Bulanan"
                            BudgetPeriodType.CUSTOM -> "Custom"
                        },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rp ${String.format("%,.0f", limit)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "$startDate - $endDate",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun CategorySelectionCard(
    categories: List<BudgetCategory>,
    selectedCategory: BudgetCategory?,
    onCategoryChange: (BudgetCategory) -> Unit
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
                text = "Pilih Kategori",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            categories.chunked(2).forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowCategories.forEach { category ->
                        CategoryOption(
                            category = category,
                            isSelected = selectedCategory?.id == category.id,
                            onClick = { onCategoryChange(category) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if odd number of categories
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CategoryOption(
    category: BudgetCategory,
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
                text = category.icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) BibitGreen else DarkGray
            )
        }
    }
}

@Composable
private fun BudgetLimitCard(
    limit: String,
    onLimitChange: (String) -> Unit
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
                text = "Batas Anggaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = limit,
                onValueChange = onLimitChange,
                label = { Text("Masukkan batas anggaran") },
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

            Text(
                text = "Tentukan batas maksimal pengeluaran untuk kategori ini",
                fontSize = 12.sp,
                color = MediumGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PeriodSelectionCard(
    selectedPeriodType: BudgetPeriodType,
    onPeriodTypeChange: (BudgetPeriodType) -> Unit
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
                text = "Periode Anggaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            BudgetPeriodType.values().forEach { periodType ->
                PeriodOption(
                    periodType = periodType,
                    isSelected = selectedPeriodType == periodType,
                    onClick = { onPeriodTypeChange(periodType) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PeriodOption(
    periodType: BudgetPeriodType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (title, description) = when (periodType) {
        BudgetPeriodType.WEEKLY -> "Mingguan" to "Anggaran berlaku selama 1 minggu"
        BudgetPeriodType.MONTHLY -> "Bulanan" to "Anggaran berlaku selama 1 bulan"
        BudgetPeriodType.CUSTOM -> "Custom" to "Tentukan periode sendiri"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = BibitGreen
            )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) BibitGreen else DarkGray
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MediumGray
            )
        }
    }
}

@Composable
private fun DateRangeCard(
    startDate: String,
    endDate: String,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    periodType: BudgetPeriodType
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
                text = "Rentang Tanggal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Date
                OutlinedTextField(
                    value = startDate,
                    onValueChange = onStartDateChange,
                    label = { Text("Tanggal Mulai") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { /* Open date picker */ }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    ),
                    enabled = periodType == BudgetPeriodType.CUSTOM
                )

                // End Date
                OutlinedTextField(
                    value = endDate,
                    onValueChange = onEndDateChange,
                    label = { Text("Tanggal Selesai") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { /* Open date picker */ }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    ),
                    enabled = periodType == BudgetPeriodType.CUSTOM
                )
            }

            if (periodType != BudgetPeriodType.CUSTOM) {
                Text(
                    text = "Tanggal akan diatur otomatis berdasarkan periode yang dipilih",
                    fontSize = 12.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SuccessDialog(
    isEditing: Boolean,
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
                text = if (isEditing) "Anggaran Diperbarui!" else "Anggaran Dibuat!",
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        text = {
            Text(
                text = if (isEditing)
                    "Anggaran Anda telah berhasil diperbarui. Pantau terus pengeluaran Anda!"
                else
                    "Anggaran baru telah dibuat. Mulai pantau pengeluaran Anda sekarang!",
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
                Text("OK")
            }
        }
    )
}

private fun generateBudgetCategories(): List<BudgetCategory> {
    return listOf(
        BudgetCategory("1", "Makanan", "🍔", "Makanan dan minuman"),
        BudgetCategory("2", "Transportasi", "🚗", "Transportasi dan bahan bakar"),
        BudgetCategory("3", "Hiburan", "🎬", "Hiburan dan rekreasi"),
        BudgetCategory("4", "Belanja", "🛍️", "Belanja dan kebutuhan"),
        BudgetCategory("5", "Kesehatan", "🏥", "Kesehatan dan obat-obatan"),
        BudgetCategory("6", "Pendidikan", "📚", "Pendidikan dan kursus"),
        BudgetCategory("7", "Tagihan", "💡", "Tagihan dan utilitas"),
        BudgetCategory("8", "Investasi", "📈", "Investasi dan tabungan")
    )
}

@Preview(showBackground = true)
@Composable
fun CreateBudgetScreenPreview() {
    CreateBudgetScreen(rememberNavController())
}
