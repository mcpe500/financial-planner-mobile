package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import com.example.financialplannerapp.core.util.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtReceivableDetailsScreen(
    item: DebtReceivable,
    onNavigateBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onAddPayment: (Double, Date, String) -> Unit = { _, _, _ -> },
    onWithdraw: (Double, Date, String) -> Unit = { _, _, _ -> }
) {
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color(0xFF1A202C)
                            )
                        }

                        Column {
                            Text(
                                text = item.type.label,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A202C)
                            )

                            Text(
                                text = "Detail dan riwayat pembayaran",
                                fontSize = 12.sp,
                                color = Color(0xFF718096)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF4CAF50)
                            )
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color(0xFFE53E3E)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Item Info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(item.type.color.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.type.icon,
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C)
                        )

                        if (item.description.isNotEmpty()) {
                            Text(
                                text = item.description,
                                fontSize = 14.sp,
                                color = Color(0xFF718096),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        when {
                                            item.remainingAmount <= 0 -> Color(0xFF38A169)
                                            item.isOverdue -> Color(0xFFE53E3E)
                                            item.isDueSoon -> Color(0xFFFF9800)
                                            else -> Color(0xFF4CAF50)
                                        },
                                        CircleShape
                                    )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = when {
                                    item.remainingAmount <= 0 -> "Lunas"
                                    item.isOverdue -> "Terlambat"
                                    item.isDueSoon -> "Jatuh Tempo Segera"
                                    else -> "Aktif"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    item.remainingAmount <= 0 -> Color(0xFF38A169)
                                    item.isOverdue -> Color(0xFFE53E3E)
                                    item.isDueSoon -> Color(0xFFFF9800)
                                    else -> Color(0xFF4CAF50)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress Card
            item {
                ProgressCard(item = item)
            }

            // Statistics Card
            item {
                StatisticsCard(item = item)
            }

            // Quick Actions
            item {
                QuickActionsCard(
                    item = item,
                    onAddPayment = { showAddPaymentDialog = true },
                    onWithdraw = { showWithdrawDialog = true }
                )
            }

            // Payment History
            item {
                PaymentHistoryCard(payments = item.payments)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Add Payment Dialog
    if (showAddPaymentDialog) {
        AddPaymentDialog(
            maxAmount = item.remainingAmount,
            type = item.type,
            onDismiss = { showAddPaymentDialog = false },
            onConfirm = { amount, date, note ->
                onAddPayment(amount, date, note)
                showAddPaymentDialog = false
            }
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog && item.type == DebtReceivableType.RECEIVABLE) {
        WithdrawDialog(
            maxAmount = item.paidAmount,
            onDismiss = { showWithdrawDialog = false },
            onConfirm = { amount, date, note ->
                onWithdraw(amount, date, note)
                showWithdrawDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus ${item.type.label}?") },
            text = { Text("Data ${item.name} akan dihapus permanen. Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Hapus", color = Color(0xFFE53E3E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = Color(0xFF718096))
                }
            }
        )
    }
}

@Composable
private fun ProgressCard(item: DebtReceivable) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Progress Pembayaran",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Circular Progress
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = item.progressPercentage,
                    modifier = Modifier.size(140.dp),
                    color = if (item.progressPercentage >= 1f) Color(0xFF38A169) else item.type.color,
                    trackColor = item.type.color.copy(alpha = 0.2f),
                    strokeWidth = 10.dp
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(item.progressPercentage * 100).toInt()}%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.progressPercentage >= 1f) Color(0xFF38A169) else item.type.color
                    )
                    Text(
                        text = "selesai",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AmountDetail(
                    label = "Dibayar",
                    amount = item.paidAmount,
                    color = Color(0xFF38A169)
                )

                AmountDetail(
                    label = "Sisa",
                    amount = item.remainingAmount,
                    color = if (item.remainingAmount <= 0) Color(0xFF38A169) else item.type.color
                )

                AmountDetail(
                    label = "Total",
                    amount = item.totalAmount,
                    color = Color(0xFF1A202C)
                )
            }
        }
    }
}

@Composable
private fun AmountDetail(
    label: String,
    amount: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF718096)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = formatCurrency(amount),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatisticsCard(item: DebtReceivable) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Statistik",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    icon = "📅",
                    label = "Dibuat",
                    value = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(item.createdDate)
                )

                StatisticItem(
                    icon = "⏰",
                    label = "Jatuh Tempo",
                    value = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(item.dueDate)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    icon = "💳",
                    label = "Total Transaksi",
                    value = "${item.payments.size} kali"
                )

                val daysRemaining = ((item.dueDate.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()
                StatisticItem(
                    icon = "📊",
                    label = "Hari Tersisa",
                    value = if (daysRemaining >= 0) "$daysRemaining hari" else "Terlambat"
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF718096)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A202C)
        )
    }
}

@Composable
private fun QuickActionsCard(
    item: DebtReceivable,
    onAddPayment: () -> Unit,
    onWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Aksi Cepat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add Payment Button
                if (item.remainingAmount > 0) {
                    Button(
                        onClick = onAddPayment,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = item.type.color
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (item.type == DebtReceivableType.DEBT) "Bayar" else "Terima",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Withdraw Button (only for receivables)
                if (item.type == DebtReceivableType.RECEIVABLE && item.paidAmount > 0) {
                    OutlinedButton(
                        onClick = onWithdraw,
                        modifier = Modifier.weight(1f),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFF9800))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tarik",
                            fontSize = 14.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentHistoryCard(payments: List<Payment>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Riwayat Pembayaran",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (payments.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💳",
                        fontSize = 48.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Belum ada pembayaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A202C)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tambah pembayaran pertama untuk mulai melacak progress",
                        fontSize = 14.sp,
                        color = Color(0xFF718096),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                payments.sortedByDescending { it.date }.forEach { payment ->
                    PaymentItem(payment = payment)

                    if (payment != payments.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentItem(payment: Payment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF38A169).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF38A169),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = formatCurrency(payment.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A202C)
                )

                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(payment.date),
                    fontSize = 12.sp,
                    color = Color(0xFF718096)
                )

                if (payment.note.isNotEmpty()) {
                    Text(
                        text = payment.note,
                        fontSize = 12.sp,
                        color = Color(0xFF718096),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddPaymentDialog(
    maxAmount: Double,
    type: DebtReceivableType,
    onDismiss: () -> Unit,
    onConfirm: (Double, Date, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showValidationError by remember { mutableStateOf(false) }

    val isAmountValid = amount.trim().isNotEmpty() &&
            amount.toDoubleOrNull() != null &&
            amount.toDouble() > 0 &&
            amount.toDouble() <= maxAmount

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
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
                            text = if (type == DebtReceivableType.DEBT) "Tambah Pembayaran" else "Terima Pembayaran",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C)
                        )

                        Text(
                            text = "Masukkan jumlah yang ${if (type == DebtReceivableType.DEBT) "dibayar" else "diterima"}",
                            fontSize = 12.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Jumlah") },
                    prefix = { Text("Rp ", color = Color(0xFF718096)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = showValidationError && !isAmountValid,
                    supportingText = if (showValidationError && !isAmountValid) {
                        { Text("Masukkan jumlah yang valid (max: ${formatCurrency(maxAmount)})", color = Color(0xFFE53E3E)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = type.color,
                        focusedLabelColor = type.color
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Catatan (Opsional)") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = type.color,
                        focusedLabelColor = type.color
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (isAmountValid) {
                                onConfirm(amount.toDouble(), Date(), note.trim())
                            } else {
                                showValidationError = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = type.color
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun WithdrawDialog(
    maxAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, Date, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showValidationError by remember { mutableStateOf(false) }

    val isAmountValid = amount.trim().isNotEmpty() &&
            amount.toDoubleOrNull() != null &&
            amount.toDouble() > 0 &&
            amount.toDouble() <= maxAmount

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Tarik Dana",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A202C)
                        )

                        Text(
                            text = "Mengurangi jumlah yang sudah dibayar",
                            fontSize = 12.sp,
                            color = Color(0xFF718096)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Jumlah") },
                    prefix = { Text("Rp ", color = Color(0xFF718096)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = showValidationError && !isAmountValid,
                    supportingText = if (showValidationError && !isAmountValid) {
                        { Text("Masukkan jumlah yang valid (max: ${formatCurrency(maxAmount)})", color = Color(0xFFE53E3E)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Alasan Penarikan") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (isAmountValid) {
                                onConfirm(amount.toDouble(), Date(), note.trim())
                            } else {
                                showValidationError = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text("Tarik", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}


