package com.example.financialplannerapp.ui.screen.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialplannerapp.data.model.RecurringBill
import com.example.financialplannerapp.data.model.BillPayment
import com.example.financialplannerapp.data.model.RepeatCycle
import com.example.financialplannerapp.data.model.BillStatus
import com.example.financialplannerapp.data.model.BillFilter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialplannerapp.ui.viewmodel.BillViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringBillsMainScreen(
    billViewModel: BillViewModel = viewModel(),
    onNavigateToAdd: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(BillFilter.ALL) }
    var showMarkAsPaidDialog by remember { mutableStateOf<RecurringBill?>(null) }
    var showDeleteDialog by remember { mutableStateOf<RecurringBill?>(null) }

    val billEntities by billViewModel.localBills.collectAsState()
    val bills = billEntities.map { entity ->
        RecurringBill(
            id = entity.uuid,
            name = entity.name,
            estimatedAmount = entity.estimatedAmount,
            dueDate = entity.dueDate,
            repeatCycle = entity.repeatCycle,
            category = entity.category,
            notes = entity.notes,
            isActive = entity.isActive,
            payments = Gson().fromJson<List<BillPayment>>(entity.paymentsJson, object : TypeToken<List<BillPayment>>() {}.type) ?: emptyList(),
            autoPay = entity.autoPay,
            notificationEnabled = entity.notificationEnabled,
            lastPaymentDate = entity.lastPaymentDate,
            creationDate = entity.creationDate
        )
    }

    val filteredBills = bills.filter { bill ->
        when (selectedFilter) {
            BillFilter.ALL -> true
            BillFilter.UPCOMING -> bill.status == BillStatus.UPCOMING || bill.status == BillStatus.DUE_SOON
            BillFilter.PAID -> bill.status == BillStatus.PAID
            BillFilter.UNPAID -> bill.status != BillStatus.PAID
        }
    }

    // Colors
    val BibitGreen = Color(0xFF4CAF50)
    val BibitDarkGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFFE8F5E8)
    val LightRed = Color(0xFFFFEBEE)
    val LightOrange = Color(0xFFFFF3E0)
    val LightBlue = Color(0xFFE3F2FD)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tagihan Rutin",
                        fontWeight = FontWeight.Bold,
                        color = BibitDarkGreen
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToCalendar) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Calendar View",
                            tint = BibitGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = BibitGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tagihan")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Bills Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = LightBlue),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = bills.size.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                            Text(
                                text = "Total Tagihan",
                                fontSize = 12.sp,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }

                    // Upcoming Bills Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = LightOrange),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = bills.count { it.status == BillStatus.DUE_SOON }.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                            Text(
                                text = "Jatuh Tempo",
                                fontSize = 12.sp,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }

                    // Overdue Bills Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = LightRed),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = bills.count { it.status == BillStatus.OVERDUE }.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53E3E)
                            )
                            Text(
                                text = "Terlambat",
                                fontSize = 12.sp,
                                color = Color(0xFFE53E3E)
                            )
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(BillFilter.entries.toTypedArray()) { filter ->
                        FilterChip(
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    "${filter.label} (${
                                        when (filter) {
                                            BillFilter.ALL -> bills.size
                                            BillFilter.UPCOMING -> bills.count { it.status == BillStatus.UPCOMING || it.status == BillStatus.DUE_SOON }
                                            BillFilter.PAID -> bills.count { it.status == BillStatus.PAID }
                                            BillFilter.UNPAID -> bills.count { it.status != BillStatus.PAID }
                                        }
                                    })"
                                )
                            },
                            selected = selectedFilter == filter,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BibitGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Bills List
            items(filteredBills) { bill ->
                BillCard(
                    bill = bill,
                    onMarkAsPaid = { showMarkAsPaidDialog = bill },
                    onEdit = { onNavigateToDetails(bill.id) },
                    onDelete = { showDeleteDialog = bill },
                    onClick = { onNavigateToDetails(bill.id) }
                )
            }

            // Empty State
            if (filteredBills.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📋",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tidak ada tagihan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "Tambah tagihan rutin pertama Anda",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                }
            }
        }
    }

    // Mark as Paid Dialog
    showMarkAsPaidDialog?.let { bill ->
        AlertDialog(
            onDismissRequest = { showMarkAsPaidDialog = null },
            title = { Text("Tandai Sebagai Lunas") },
            text = {
                Text("Apakah Anda yakin ingin menandai tagihan \"${bill.name}\" sebagai sudah dibayar?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Mark as paid logic
                        showMarkAsPaidDialog = null
                    }
                ) {
                    Text("Ya", color = BibitGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMarkAsPaidDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete Dialog
    showDeleteDialog?.let { bill ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Tagihan") },
            text = {
                Text("Apakah Anda yakin ingin menghapus tagihan \"${bill.name}\"? Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Delete logic
                        showDeleteDialog = null
                    }
                ) {
                    Text("Hapus", color = Color(0xFFE53E3E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun BillCard(
    bill: RecurringBill,
    onMarkAsPaid: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bill.status == BillStatus.PAID) Color(0xFFF8F8F8) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bill.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bill.status == BillStatus.PAID) Color(0xFF999999) else Color(0xFF333333)
                    )
                    Text(
                        text = currencyFormat.format(bill.estimatedAmount),
                        fontSize = 14.sp,
                        color = if (bill.status == BillStatus.PAID) Color(0xFF999999) else Color(0xFF666666)
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = bill.status.color.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = bill.status.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = bill.status.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Due Date and Cycle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jatuh tempo: ${dateFormat.format(bill.nextDueDate)}",
                        fontSize = 12.sp,
                        color = if (bill.status == BillStatus.PAID) Color(0xFF999999) else Color(0xFF666666)
                    )
                    Text(
                        text = "${getRepeatCycleIcon(bill.repeatCycle)} ${getRepeatCycleLabel(bill.repeatCycle)}",
                        fontSize = 12.sp,
                        color = if (bill.status == BillStatus.PAID) Color(0xFF999999) else Color(0xFF666666)
                    )
                }

                // Days to Due
                if (bill.status != BillStatus.PAID) {
                    Text(
                        text = when {
                            bill.isOverdue -> "${Math.abs(bill.daysToDue)} hari terlambat"
                            bill.daysToDue == 0 -> "Hari ini"
                            bill.daysToDue == 1 -> "Besok"
                            else -> "${bill.daysToDue} hari lagi"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (bill.status) {
                            BillStatus.OVERDUE -> Color(0xFFE53E3E)
                            BillStatus.DUE_SOON -> Color(0xFFFF9800)
                            else -> Color(0xFF666666)
                        }
                    )
                }
            }

            // Notes
            if (bill.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bill.notes,
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Action Buttons
            if (bill.status != BillStatus.PAID) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mark as Paid Button
                    Button(
                        onClick = onMarkAsPaid,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38A169)
                        )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bayar", fontSize = 12.sp)
                    }

                    // Edit Button
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 12.sp)
                    }
                }
            } else {
                // Paid Status
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF38A169),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sudah dibayar",
                        fontSize = 12.sp,
                        color = Color(0xFF38A169),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Helper functions to get RepeatCycle properties from string
private fun getRepeatCycleIcon(repeatCycle: String): String {
    return when (repeatCycle) {
        "DAILY" -> "📅"
        "WEEKLY" -> "📅"
        "MONTHLY" -> "📅"
        "YEARLY" -> "📅"
        "CUSTOM" -> "⚙️"
        else -> "📅"
    }
}

private fun getRepeatCycleLabel(repeatCycle: String): String {
    return when (repeatCycle) {
        "DAILY" -> "Daily"
        "WEEKLY" -> "Weekly"
        "MONTHLY" -> "Monthly"
        "YEARLY" -> "Yearly"
        "CUSTOM" -> "Custom"
        else -> "Unknown"
    }
}
