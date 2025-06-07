package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val ExpenseRed = Color(0xFFE53E3E)
private val ReceivableGreen = Color(0xFF38A169)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtReceivableScreen(
    initialData: DebtReceivable? = null,
    onNavigateBack: () -> Unit = {},
    onSave: (DebtReceivable) -> Unit = {}
) {
    var name by remember { mutableStateOf(initialData?.name ?: "") }
    var amount by remember { mutableStateOf(initialData?.totalAmount?.toString() ?: "") }
    var description by remember { mutableStateOf(initialData?.description ?: "") }
    var selectedType by remember { mutableStateOf(initialData?.type ?: DebtReceivableType.DEBT) }
    var selectedDate by remember {
        mutableStateOf(initialData?.dueDate ?: Calendar.getInstance().apply {
            add(Calendar.MONTH, 1)
        }.time)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }

    val isEditing = initialData != null
    val scrollState = rememberScrollState()

    // Validation
    val isNameValid = name.trim().isNotEmpty()
    val isAmountValid = amount.trim().isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDouble() > 0
    val isFormValid = isNameValid && isAmountValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit ${selectedType.label}" else "Tambah ${selectedType.label}",
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = DarkGray
                        )
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Selection
            TypeSelectionCard(
                selectedType = selectedType,
                onTypeChange = { selectedType = it }
            )

            // Preview Card
            if (name.isNotEmpty() || amount.isNotEmpty()) {
                PreviewCard(
                    name = name.ifEmpty { "Nama ${selectedType.label}" },
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    type = selectedType,
                    dueDate = selectedDate
                )
            }

            // Form Fields
            FormFieldsCard(
                name = name,
                onNameChange = { name = it },
                amount = amount,
                onAmountChange = { amount = it },
                description = description,
                onDescriptionChange = { description = it },
                selectedDate = selectedDate,
                onDateClick = { showDatePicker = true },
                selectedType = selectedType,
                showValidationErrors = showValidationErrors,
                isNameValid = isNameValid,
                isAmountValid = isAmountValid
            )

            // Action Buttons
            ActionButtonsSection(
                isFormValid = isFormValid,
                isEditing = isEditing,
                selectedType = selectedType,
                onReset = {
                    name = ""
                    amount = ""
                    description = ""
                    showValidationErrors = false
                },
                onSave = {
                    if (isFormValid) {
                        val newItem = DebtReceivable(
                            id = initialData?.id ?: UUID.randomUUID().toString(),
                            name = name.trim(),
                            totalAmount = amount.toDouble(),
                            paidAmount = initialData?.paidAmount ?: 0.0,
                            description = description.trim(),
                            type = selectedType,
                            dueDate = selectedDate,
                            createdDate = initialData?.createdDate ?: Date(),
                            payments = initialData?.payments ?: emptyList()
                        )
                        onSave(newItem)
                    } else {
                        showValidationErrors = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Date(millis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = BibitGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = MediumGray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = BibitGreen
                )
            )
        }
    }
}

@Composable
private fun TypeSelectionCard(
    selectedType: DebtReceivableType,
    onTypeChange: (DebtReceivableType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Jenis",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Pilih jenis catatan keuangan",
                fontSize = 12.sp,
                color = MediumGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DebtReceivableType.values().forEach { type ->
                    TypeSelectionItem(
                        type = type,
                        isSelected = selectedType == type,
                        onClick = { onTypeChange(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeSelectionItem(
    type: DebtReceivableType,
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
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) type.color.copy(alpha = 0.1f) else SoftGray
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(type.color),
            width = 2.dp
        ) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = type.icon,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = type.label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) type.color else MediumGray
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (type == DebtReceivableType.DEBT) "Uang yang harus dibayar" else "Uang yang akan diterima",
                    fontSize = 10.sp,
                    color = type.color.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun PreviewCard(
    name: String,
    amount: Double,
    type: DebtReceivableType,
    dueDate: Date
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = type.color.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(type.color.copy(alpha = 0.3f))
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type.icon,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Preview",
                        fontSize = 12.sp,
                        color = type.color,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Tampilan ${type.label.lowercase()} Anda",
                        fontSize = 10.sp,
                        color = MediumGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Jumlah",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            .format(amount).replace("IDR", "Rp"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = type.color
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Jatuh Tempo",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(dueDate),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun FormFieldsCard(
    name: String,
    onNameChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedDate: Date,
    onDateClick: () -> Unit,
    selectedType: DebtReceivableType,
    showValidationErrors: Boolean,
    isNameValid: Boolean,
    isAmountValid: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Detail ${selectedType.label}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            // Name Field
            InputField(
                label = "Nama",
                value = name,
                onValueChange = onNameChange,
                placeholder = "Masukkan nama ${selectedType.label.lowercase()}",
                isError = showValidationErrors && !isNameValid,
                errorMessage = if (showValidationErrors && !isNameValid) "Nama tidak boleh kosong" else null
            )

            // Amount Field
            InputField(
                label = "Jumlah",
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        onAmountChange(newValue)
                    }
                },
                placeholder = "0",
                prefix = "Rp ",
                keyboardType = KeyboardType.Decimal,
                isError = showValidationErrors && !isAmountValid,
                errorMessage = if (showValidationErrors && !isAmountValid) "Masukkan jumlah yang valid" else null
            )

            // Description Field
            InputField(
                label = "Deskripsi (Opsional)",
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = "Tambahkan catatan...",
                minLines = 3,
                maxLines = 4
            )

            // Due Date Field
            DateField(
                label = "Jatuh Tempo",
                selectedDate = selectedDate,
                onClick = onDateClick
            )
        }
    }
}

@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    prefix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = MediumGray) },
            prefix = prefix?.let { { Text(it, color = MediumGray) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            minLines = minLines,
            maxLines = maxLines,
            isError = isError,
            supportingText = errorMessage?.let { { Text(it, color = ExpenseRed) } },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BibitGreen,
                focusedLabelColor = BibitGreen
            )
        )
    }
}

@Composable
private fun DateField(
    label: String,
    selectedDate: Date,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(selectedDate),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Pilih tanggal",
                    tint = BibitGreen
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = DarkGray,
                disabledBorderColor = MediumGray.copy(alpha = 0.5f),
                disabledTrailingIconColor = BibitGreen
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun ActionButtonsSection(
    isFormValid: Boolean,
    isEditing: Boolean,
    selectedType: DebtReceivableType,
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = BibitGreen
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.foundation.BorderStroke(1.dp, BibitGreen).brush
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Reset")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = BibitGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isEditing) "Simpan Perubahan" else "Tambah ${selectedType.label}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDebtReceivableScreenPreview() {
    AddDebtReceivableScreen()
}

@Preview(showBackground = true)
@Composable
fun AddDebtReceivableScreenEditPreview() {
    val mockItem = DebtReceivable(
        id = "1",
        name = "Pinjaman Bank BCA",
        totalAmount = 50000000.0,
        paidAmount = 15000000.0,
        description = "KTA untuk renovasi rumah",
        type = DebtReceivableType.DEBT,
        dueDate = Calendar.getInstance().apply { add(Calendar.MONTH, 2) }.time,
        createdDate = Date()
    )

    AddDebtReceivableScreen(initialData = mockItem)
}

@Preview(showBackground = true)
@Composable
fun TypeSelectionCardPreview() {
    TypeSelectionCard(
        selectedType = DebtReceivableType.DEBT,
        onTypeChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCardPreview() {
    PreviewCard(
        name = "Pinjaman Bank BCA",
        amount = 50000000.0,
        type = DebtReceivableType.DEBT,
        dueDate = Calendar.getInstance().apply { add(Calendar.MONTH, 2) }.time
    )
}
