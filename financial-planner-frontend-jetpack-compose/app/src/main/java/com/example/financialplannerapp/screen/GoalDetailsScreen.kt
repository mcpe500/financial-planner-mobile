package com.example.financialplannerapp.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val MotivationalOrange = Color(0xFFFF9800)
private val AchievementGold = Color(0xFFFFD700)

data class GoalTransaction(
    val id: String,
    val amount: Double,
    val date: Date,
    val type: GoalTransactionType,
    val note: String,
    val sourceWallet: String
)

enum class GoalTransactionType {
    DEPOSIT, WITHDRAWAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsScreen(navController: NavController, goalId: String) {
    var showAddFundsSheet by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // Mock data - in real app, fetch by goalId
    val goal = remember {
        generateMockGoals().find { it.id == goalId } ?: generateMockGoals().first()
    }
    val transactions = remember { generateMockTransactions() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        goal.name,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("edit_goal/${goal.id}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Goal")
                    }
                    IconButton(onClick = { /* Show more options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        },
        floatingActionButton = {
            if (!goal.isCompleted) {
                FloatingActionButton(
                    onClick = { showAddFundsSheet = true },
                    containerColor = BibitGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Funds")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Goal Progress Card
            item {
                GoalProgressCard(goal)
            }

            // Goal Statistics
            item {
                GoalStatisticsCard(goal, transactions)
            }

            // Quick Actions
            if (!goal.isCompleted) {
                item {
                    QuickActionsCard(
                        onAddFunds = { showAddFundsSheet = true },
                        onWithdraw = { showWithdrawDialog = true },
                        onEditGoal = { navController.navigate("edit_goal/${goal.id}") }
                    )
                }
            }

            // Transaction History
            item {
                Text(
                    text = "Riwayat Transaksi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            items(transactions) { transaction ->
                TransactionHistoryItem(transaction)
            }

            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Funds Bottom Sheet
    if (showAddFundsSheet) {
        AddFundsBottomSheet(
            goal = goal,
            onDismiss = { showAddFundsSheet = false },
            onAddFunds = { amount, sourceWallet ->
                // Handle add funds logic
                showAddFundsSheet = false
            }
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        WithdrawDialog(
            goal = goal,
            onDismiss = { showWithdrawDialog = false },
            onWithdraw = { amount, note ->
                // Handle withdraw logic
                showWithdrawDialog = false
            }
        )
    }
}

@Composable
private fun GoalProgressCard(goal: FinancialGoal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted) AchievementGold else BibitGreen
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Goal Icon and Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.icon,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                if (goal.isCompleted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawGoalProgressCircle(goal.progressPercentage)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(goal.progressPercentage * 100).toInt()}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (goal.isCompleted) "Tercapai!" else "Progress",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Info
            Text(
                text = "Rp ${String.format("%,.0f", goal.currentAmount)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "dari Rp ${String.format("%,.0f", goal.targetAmount)}",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            if (!goal.isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sisa: Rp ${String.format("%,.0f", goal.remainingAmount)}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Target",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(goal.targetDate),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                if (!goal.isCompleted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sisa Waktu",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "${goal.daysRemaining} hari",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalStatisticsCard(goal: FinancialGoal, transactions: List<GoalTransaction>) {
    val totalDeposits = transactions.filter { it.type == GoalTransactionType.DEPOSIT }.sumOf { it.amount }
    val totalWithdrawals = transactions.filter { it.type == GoalTransactionType.WITHDRAWAL }.sumOf { it.amount }
    val averageMonthly = if (transactions.isNotEmpty()) {
        val monthsActive = ((Date().time - goal.createdDate.time) / (1000 * 60 * 60 * 24 * 30)).coerceAtLeast(1)
        totalDeposits / monthsActive
    } else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Statistik Tujuan",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    title = "Total Setoran",
                    value = "Rp ${String.format("%,.0f", totalDeposits)}",
                    icon = Icons.Default.TrendingUp,
                    color = BibitGreen
                )
                StatisticItem(
                    title = "Rata-rata/Bulan",
                    value = "Rp ${String.format("%,.0f", averageMonthly)}",
                    icon = Icons.Default.CalendarMonth,
                    color = MotivationalOrange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem(
                    title = "Transaksi",
                    value = "${transactions.size}x",
                    icon = Icons.Default.Receipt,
                    color = Color(0xFF2196F3)
                )

                goal.estimatedCompletionDate?.let { estimatedDate ->
                    StatisticItem(
                        title = "Estimasi Selesai",
                        value = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(estimatedDate),
                        icon = Icons.Default.Schedule,
                        color = Color(0xFF9C27B0)
                    )
                } ?: StatisticItem(
                    title = "Wallet",
                    value = goal.linkedWallet,
                    icon = Icons.Default.AccountBalanceWallet,
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = MediumGray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGray
        )
    }
}

@Composable
private fun QuickActionsCard(
    onAddFunds: () -> Unit,
    onWithdraw: () -> Unit,
    onEditGoal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Aksi Cepat",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    text = "Tambah Dana",
                    icon = Icons.Default.Add,
                    color = BibitGreen,
                    onClick = onAddFunds,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    text = "Tarik Dana",
                    icon = Icons.Default.Remove,
                    color = MotivationalOrange,
                    onClick = onWithdraw,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    text = "Edit Tujuan",
                    icon = Icons.Default.Edit,
                    color = Color(0xFF2196F3),
                    onClick = onEditGoal,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TransactionHistoryItem(transaction: GoalTransaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Type Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (transaction.type == GoalTransactionType.DEPOSIT)
                            BibitGreen.copy(alpha = 0.2f)
                        else
                            MotivationalOrange.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (transaction.type == GoalTransactionType.DEPOSIT) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = transaction.type.name,
                    tint = if (transaction.type == GoalTransactionType.DEPOSIT) BibitGreen else MotivationalOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.type == GoalTransactionType.DEPOSIT) "Setoran" else "Penarikan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray
                )
                Text(
                    text = transaction.sourceWallet,
                    fontSize = 12.sp,
                    color = MediumGray
                )
                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                }
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(transaction.date),
                    fontSize = 10.sp,
                    color = MediumGray
                )
            }

            // Amount
            Text(
                text = "${if (transaction.type == GoalTransactionType.DEPOSIT) "+" else "-"}Rp ${String.format("%,.0f", transaction.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == GoalTransactionType.DEPOSIT) BibitGreen else MotivationalOrange
            )
        }
    }
}

@Composable
private fun WithdrawDialog(
    goal: FinancialGoal,
    onDismiss: () -> Unit,
    onWithdraw: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tarik Dana",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    text = "Tarik dana dari tujuan \"${goal.name}\"",
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Jumlah") },
                    placeholder = { Text("0") },
                    leadingIcon = {
                        Text(
                            "Rp",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MotivationalOrange
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MotivationalOrange,
                        focusedLabelColor = MotivationalOrange
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Catatan (opsional)") },
                    placeholder = { Text("Alasan penarikan") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MotivationalOrange,
                        focusedLabelColor = MotivationalOrange
                    )
                )

                Text(
                    text = "Saldo tersedia: Rp ${String.format("%,.0f", goal.currentAmount)}",
                    fontSize = 12.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let { amountValue ->
                        onWithdraw(amountValue, note)
                    }
                },
                enabled = amount.isNotEmpty() &&
                        amount.toDoubleOrNull() != null &&
                        (amount.toDoubleOrNull() ?: 0.0) <= goal.currentAmount,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MotivationalOrange,
                    contentColor = Color.White
                )
            ) {
                Text("Tarik Dana")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun DrawScope.drawGoalProgressCircle(progress: Float) {
    val strokeWidth = 12.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2
    val center = Offset(size.width / 2, size.height / 2)

    // Background circle
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Progress arc
    val sweepAngle = 360 * progress.coerceAtMost(1f)
    drawArc(
        color = Color.White,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

private fun generateMockTransactions(): List<GoalTransaction> {
    val calendar = Calendar.getInstance()
    return listOf(
        GoalTransaction(
            id = "1",
            amount = 2000000.0,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            type = GoalTransactionType.DEPOSIT,
            note = "Setoran bulanan",
            sourceWallet = "BCA Savings"
        ),
        GoalTransaction(
            id = "2",
            amount = 1500000.0,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -15) }.time,
            type = GoalTransactionType.DEPOSIT,
            note = "Bonus kerja",
            sourceWallet = "Cash Wallet"
        ),
        GoalTransaction(
            id = "3",
            amount = 500000.0,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -20) }.time,
            type = GoalTransactionType.WITHDRAWAL,
            note = "Kebutuhan mendadak",
            sourceWallet = "BCA Savings"
        ),
        GoalTransaction(
            id = "4",
            amount = 3000000.0,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -30) }.time,
            type = GoalTransactionType.DEPOSIT,
            note = "Setoran awal",
            sourceWallet = "BCA Savings"
        ),
        GoalTransaction(
            id = "5",
            amount = 2500000.0,
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -45) }.time,
            type = GoalTransactionType.DEPOSIT,
            note = "Transfer dari tabungan lama",
            sourceWallet = "Emergency Fund"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun GoalDetailsScreenPreview() {
    GoalDetailsScreen(rememberNavController(), "1")
}
