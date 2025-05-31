package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
private val AchievementGold = Color(0xFFFFD700)

data class FinancialGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: Date,
    val linkedWallet: String,
    val icon: String,
    val category: GoalCategory,
    val isCompleted: Boolean = false,
    val createdDate: Date = Date()
) {
    val progressPercentage: Float get() = (currentAmount / targetAmount).toFloat().coerceAtMost(1f)
    val remainingAmount: Double get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
    val daysRemaining: Int get() {
        val diffInMillis = targetDate.time - Date().time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }
    val estimatedCompletionDate: Date? get() {
        if (currentAmount <= 0) return null
        val dailyRate = currentAmount / ((Date().time - createdDate.time) / (1000 * 60 * 60 * 24))
        if (dailyRate <= 0) return null
        val daysToComplete = (remainingAmount / dailyRate).toInt()
        return Date(Date().time + (daysToComplete * 24 * 60 * 60 * 1000L))
    }
}

enum class GoalCategory {
    VACATION, HOUSE, CAR, EDUCATION, EMERGENCY, INVESTMENT, OTHER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialGoalsMainScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAddFundsSheet by remember { mutableStateOf<FinancialGoal?>(null) }
    var sortBy by remember { mutableStateOf("deadline") }

    val tabs = listOf("Tujuan Aktif", "Tercapai")
    val goals = remember { generateMockGoals() }
    val activeGoals = goals.filter { !it.isCompleted }
    val completedGoals = goals.filter { it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tujuan Keuangan",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Show sort options */ }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_goal") },
                containerColor = BibitGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Tujuan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = BibitGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = BibitGreen
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> ActiveGoalsTab(
                    goals = activeGoals,
                    sortBy = sortBy,
                    onSortChange = { sortBy = it },
                    onGoalClick = { goal -> navController.navigate("goal_details/${goal.id}") },
                    onAddFunds = { goal -> showAddFundsSheet = goal },
                    onEditGoal = { goal -> navController.navigate("edit_goal/${goal.id}") }
                )
                1 -> CompletedGoalsTab(
                    goals = completedGoals,
                    onGoalClick = { goal -> navController.navigate("goal_details/${goal.id}") }
                )
            }
        }
    }

    // Add Funds Bottom Sheet
    showAddFundsSheet?.let { goal ->
        AddFundsBottomSheet(
            goal = goal,
            onDismiss = { showAddFundsSheet = null },
            onAddFunds = { amount, sourceWallet ->
                // Handle add funds logic
                showAddFundsSheet = null
            }
        )
    }
}

@Composable
private fun ActiveGoalsTab(
    goals: List<FinancialGoal>,
    sortBy: String,
    onSortChange: (String) -> Unit,
    onGoalClick: (FinancialGoal) -> Unit,
    onAddFunds: (FinancialGoal) -> Unit,
    onEditGoal: (FinancialGoal) -> Unit
) {
    val sortedGoals = when (sortBy) {
        "deadline" -> goals.sortedBy { it.daysRemaining }
        "progress" -> goals.sortedByDescending { it.progressPercentage }
        "amount" -> goals.sortedByDescending { it.targetAmount }
        else -> goals
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Card
        item {
            GoalsSummaryCard(goals)
        }

        // Sort Options
        item {
            SortOptionsCard(
                selectedSort = sortBy,
                onSortChange = onSortChange
            )
        }

        // Goals List
        items(sortedGoals) { goal ->
            ActiveGoalCard(
                goal = goal,
                onClick = { onGoalClick(goal) },
                onAddFunds = { onAddFunds(goal) },
                onEdit = { onEditGoal(goal) }
            )
        }

        // Empty state
        if (goals.isEmpty()) {
            item {
                EmptyGoalsCard(
                    title = "Belum Ada Tujuan",
                    description = "Mulai merencanakan masa depan dengan membuat tujuan keuangan pertama Anda!",
                    icon = "🎯"
                )
            }
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun CompletedGoalsTab(
    goals: List<FinancialGoal>,
    onGoalClick: (FinancialGoal) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Achievement Summary
        item {
            AchievementSummaryCard(goals)
        }

        // Completed Goals
        items(goals.sortedByDescending { it.targetDate }) { goal ->
            CompletedGoalCard(
                goal = goal,
                onClick = { onGoalClick(goal) }
            )
        }

        // Empty state
        if (goals.isEmpty()) {
            item {
                EmptyGoalsCard(
                    title = "Belum Ada Pencapaian",
                    description = "Tujuan yang sudah tercapai akan muncul di sini. Tetap semangat!",
                    icon = "🏆"
                )
            }
        }
    }
}

@Composable
private fun GoalsSummaryCard(goals: List<FinancialGoal>) {
    val totalTarget = goals.sumOf { it.targetAmount }
    val totalSaved = goals.sumOf { it.currentAmount }
    val overallProgress = if (totalTarget > 0) (totalSaved / totalTarget).toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BibitGreen)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Total Tujuan Keuangan",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "Rp ${String.format("%,.0f", totalSaved)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "dari Rp ${String.format("%,.0f", totalTarget)}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = overallProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${goals.size} Tujuan Aktif",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "${(overallProgress * 100).toInt()}% Tercapai",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortOptionsCard(
    selectedSort: String,
    onSortChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = mapOf(
        "deadline" to "Deadline Terdekat",
        "progress" to "Progress Tertinggi",
        "amount" to "Target Terbesar"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Sort,
                contentDescription = "Sort",
                tint = BibitGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Urutkan:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )
            Spacer(modifier = Modifier.width(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = sortOptions[selectedSort] ?: "",
                    onValueChange = {},
                    readOnly = true,
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
                    sortOptions.forEach { (key, value) ->
                        DropdownMenuItem(
                            text = { Text(value) },
                            onClick = {
                                onSortChange(key)
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
private fun ActiveGoalCard(
    goal: FinancialGoal,
    onClick: () -> Unit,
    onAddFunds: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = goal.icon,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = goal.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = goal.linkedWallet,
                            fontSize = 12.sp,
                            color = MediumGray
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = BibitGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rp ${String.format("%,.0f", goal.currentAmount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BibitGreen
                    )
                    Text(
                        text = "dari Rp ${String.format("%,.0f", goal.targetAmount)}",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                }

                Text(
                    text = "${(goal.progressPercentage * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BibitGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = goal.progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = BibitGreen,
                trackColor = SoftGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Target: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(goal.targetDate)}",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = "${goal.daysRemaining} hari lagi",
                        fontSize = 12.sp,
                        color = if (goal.daysRemaining <= 30) MotivationalOrange else MediumGray,
                        fontWeight = if (goal.daysRemaining <= 30) FontWeight.Medium else FontWeight.Normal
                    )
                }

                Button(
                    onClick = onAddFunds,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotivationalOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Funds",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Tambah",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedGoalCard(
    goal: FinancialGoal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(AchievementGold.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = goal.icon,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
                Text(
                    text = "Rp ${String.format("%,.0f", goal.targetAmount)}",
                    fontSize = 14.sp,
                    color = BibitGreen,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Tercapai ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(goal.targetDate)}",
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }

            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = AchievementGold,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AchievementSummaryCard(goals: List<FinancialGoal>) {
    val totalAchieved = goals.sumOf { it.targetAmount }
    val averageTime = if (goals.isNotEmpty()) {
        goals.map { (it.targetDate.time - it.createdDate.time) / (1000 * 60 * 60 * 24) }.average().toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AchievementGold)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total Pencapaian",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = "Rp ${String.format("%,.0f", totalAchieved)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${goals.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tujuan",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${averageTime}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Hari Rata-rata",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyGoalsCard(
    title: String,
    description: String,
    icon: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsBottomSheet(
    goal: FinancialGoal,
    onDismiss: () -> Unit,
    onAddFunds: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedWallet by remember { mutableStateOf("Cash Wallet") }
    var expanded by remember { mutableStateOf(false) }

    val wallets = listOf("Cash Wallet", "BCA Savings", "GoPay", "Dana")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tambah Dana",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Goal Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = goal.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                    Text(
                        text = "Sisa: Rp ${String.format("%,.0f", goal.remainingAmount)}",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Jumlah Dana") },
                placeholder = { Text("0") },
                leadingIcon = {
                    Text(
                        "Rp",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BibitGreen
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BibitGreen,
                    focusedLabelColor = BibitGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Source Wallet
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedWallet,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sumber Dana") },
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
                                selectedWallet = wallet
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Add Button
            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let { amountValue ->
                        onAddFunds(amountValue, selectedWallet)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotEmpty() && amount.toDoubleOrNull() != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Tambah Dana",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun generateMockGoals(): List<FinancialGoal> {
    val calendar = Calendar.getInstance()
    return listOf(
        FinancialGoal(
            id = "1",
            name = "Liburan ke Bali",
            targetAmount = 15000000.0,
            currentAmount = 8500000.0,
            targetDate = calendar.apply { add(Calendar.MONTH, 3) }.time,
            linkedWallet = "BCA Savings",
            icon = "🏖️",
            category = GoalCategory.VACATION
        ),
        FinancialGoal(
            id = "2",
            name = "Dana Darurat",
            targetAmount = 50000000.0,
            currentAmount = 32000000.0,
            targetDate = calendar.apply { add(Calendar.MONTH, 8) }.time,
            linkedWallet = "Emergency Fund",
            icon = "🛡️",
            category = GoalCategory.EMERGENCY
        ),
        FinancialGoal(
            id = "3",
            name = "Beli Mobil",
            targetAmount = 200000000.0,
            currentAmount = 45000000.0,
            targetDate = calendar.apply { add(Calendar.YEAR, 2) }.time,
            linkedWallet = "Car Fund",
            icon = "🚗",
            category = GoalCategory.CAR
        ),
        FinancialGoal(
            id = "4",
            name = "Laptop Baru",
            targetAmount = 25000000.0,
            currentAmount = 25000000.0,
            targetDate = calendar.apply { add(Calendar.MONTH, -1) }.time,
            linkedWallet = "Tech Fund",
            icon = "💻",
            category = GoalCategory.OTHER,
            isCompleted = true
        ),
        FinancialGoal(
            id = "5",
            name = "Kursus Online",
            targetAmount = 5000000.0,
            currentAmount = 5000000.0,
            targetDate = calendar.apply { add(Calendar.MONTH, -3) }.time,
            linkedWallet = "Education Fund",
            icon = "📚",
            category = GoalCategory.EDUCATION,
            isCompleted = true
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FinancialGoalsMainScreenPreview() {
    FinancialGoalsMainScreen(rememberNavController())
}
