package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
private val WarningOrange = Color(0xFFFF9800)
private val DangerRed = Color(0xFFFF5722)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetMainScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Kelola", "Progres", "Riwayat")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Anggaran",
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
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { navController.navigate("create_budget") },
                    containerColor = BibitGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Buat Anggaran")
                }
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
                0 -> ManageBudgetTab(navController)
                1 -> BudgetProgressTab(navController)
                2 -> BudgetHistoryTab(navController)
            }
        }
    }
}

@Composable
private fun ManageBudgetTab(navController: NavController) {
    val budgets = remember { generateMockBudgets() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Stats Card
        item {
            BudgetStatsCard(budgets)
        }

        // Active Budgets
        item {
            Text(
                text = "Anggaran Aktif",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(budgets.filter { it.isActive }) { budget ->
            ManageBudgetCard(
                budget = budget,
                onEdit = { navController.navigate("edit_budget/${budget.id}") },
                onDelete = { /* Handle delete */ },
                onToggle = { /* Handle toggle */ }
            )
        }

        // Inactive Budgets
        val inactiveBudgets = budgets.filter { !it.isActive }
        if (inactiveBudgets.isNotEmpty()) {
            item {
                Text(
                    text = "Anggaran Nonaktif",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MediumGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(inactiveBudgets) { budget ->
                ManageBudgetCard(
                    budget = budget,
                    onEdit = { navController.navigate("edit_budget/${budget.id}") },
                    onDelete = { /* Handle delete */ },
                    onToggle = { /* Handle toggle */ }
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
private fun BudgetProgressTab(navController: NavController) {
    var selectedPeriod by remember { mutableStateOf("Bulan Ini") }
    val periods = listOf("Minggu Ini", "Bulan Ini", "3 Bulan Terakhir")
    val budgets = remember { generateMockBudgets().filter { it.isActive } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Period Filter
        item {
            PeriodFilterCard(
                selectedPeriod = selectedPeriod,
                periods = periods,
                onPeriodChange = { selectedPeriod = it }
            )
        }

        // Overall Progress
        item {
            OverallProgressCard(budgets)
        }

        // Category Progress
        items(budgets) { budget ->
            BudgetProgressCard(budget)
        }
    }
}

@Composable
private fun BudgetHistoryTab(navController: NavController) {
    val historyPeriods = remember { generateMockBudgetHistory() }
    var expandedPeriod by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(historyPeriods) { period ->
            BudgetHistoryCard(
                period = period,
                isExpanded = expandedPeriod == period.id,
                onToggleExpand = {
                    expandedPeriod = if (expandedPeriod == period.id) null else period.id
                }
            )
        }
    }
}

data class Budget(
    val id: String,
    val category: String,
    val categoryIcon: String,
    val limit: Double,
    val spent: Double,
    val period: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean = true
) {
    val remaining: Double get() = limit - spent
    val progressPercentage: Float get() = (spent / limit).toFloat().coerceAtMost(1f)
    val status: BudgetStatus get() = when {
        spent >= limit -> BudgetStatus.EXCEEDED
        spent >= limit * 0.8 -> BudgetStatus.WARNING
        else -> BudgetStatus.SAFE
    }
}

enum class BudgetStatus {
    SAFE, WARNING, EXCEEDED
}

data class BudgetPeriod(
    val id: String,
    val name: String,
    val dateRange: String,
    val totalBudget: Double,
    val totalSpent: Double,
    val budgets: List<Budget>
)

@Composable
private fun BudgetStatsCard(budgets: List<Budget>) {
    val activeBudgets = budgets.filter { it.isActive }
    val totalBudget = activeBudgets.sumOf { it.limit }
    val totalSpent = activeBudgets.sumOf { it.spent }
    val totalRemaining = totalBudget - totalSpent

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
                text = "Total Anggaran Bulan Ini",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "Rp ${String.format("%,.0f", totalBudget)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Terpakai",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", totalSpent)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Column {
                    Text(
                        text = "Tersisa",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", totalRemaining)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ManageBudgetCard(
    budget: Budget,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (budget.isActive) Color.White else Color.White.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                        text = budget.categoryIcon,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = budget.category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (budget.isActive) DarkGray else MediumGray
                        )
                        Text(
                            text = budget.period,
                            fontSize = 12.sp,
                            color = MediumGray
                        )
                        Text(
                            text = "${budget.startDate} - ${budget.endDate}",
                            fontSize = 10.sp,
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
                    IconButton(onClick = onToggle) {
                        Icon(
                            if (budget.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (budget.isActive) "Pause" else "Activate",
                            tint = if (budget.isActive) WarningOrange else BibitGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Budget amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Limit: Rp ${String.format("%,.0f", budget.limit)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (budget.isActive) DarkGray else MediumGray
                )
                Text(
                    text = "Terpakai: Rp ${String.format("%,.0f", budget.spent)}",
                    fontSize = 14.sp,
                    color = when (budget.status) {
                        BudgetStatus.SAFE -> BibitGreen
                        BudgetStatus.WARNING -> WarningOrange
                        BudgetStatus.EXCEEDED -> DangerRed
                    }
                )
            }

            // Progress bar
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = budget.progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = when (budget.status) {
                    BudgetStatus.SAFE -> BibitGreen
                    BudgetStatus.WARNING -> WarningOrange
                    BudgetStatus.EXCEEDED -> DangerRed
                },
                trackColor = SoftGray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodFilterCard(
    selectedPeriod: String,
    periods: List<String>,
    onPeriodChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                text = "Periode",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedPeriod,
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
                    periods.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period) },
                            onClick = {
                                onPeriodChange(period)
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
private fun OverallProgressCard(budgets: List<Budget>) {
    val totalBudget = budgets.sumOf { it.limit }
    val totalSpent = budgets.sumOf { it.spent }
    val overallProgress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = "Progress",
                    tint = BibitGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Progress Keseluruhan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            Text(
                text = "${(overallProgress * 100).toInt()}% dari total anggaran",
                fontSize = 14.sp,
                color = MediumGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LinearProgressIndicator(
                progress = overallProgress.coerceAtMost(1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    overallProgress >= 1f -> DangerRed
                    overallProgress >= 0.8f -> WarningOrange
                    else -> BibitGreen
                },
                trackColor = SoftGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Anggaran",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", totalBudget)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Terpakai",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", totalSpent)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            overallProgress >= 1f -> DangerRed
                            overallProgress >= 0.8f -> WarningOrange
                            else -> BibitGreen
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetProgressCard(budget: Budget) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = budget.categoryIcon,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = budget.category,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray
                        )
                        Text(
                            text = "${(budget.progressPercentage * 100).toInt()}% terpakai",
                            fontSize = 12.sp,
                            color = MediumGray
                        )
                    }
                }

                // Status indicator
                when (budget.status) {
                    BudgetStatus.EXCEEDED -> {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Exceeded",
                            tint = DangerRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    BudgetStatus.WARNING -> {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = WarningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    BudgetStatus.SAFE -> {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Safe",
                            tint = BibitGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = budget.progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when (budget.status) {
                    BudgetStatus.SAFE -> BibitGreen
                    BudgetStatus.WARNING -> WarningOrange
                    BudgetStatus.EXCEEDED -> DangerRed
                },
                trackColor = SoftGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Budget details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Limit",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", budget.limit)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Terpakai",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", budget.spent)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (budget.status) {
                            BudgetStatus.SAFE -> BibitGreen
                            BudgetStatus.WARNING -> WarningOrange
                            BudgetStatus.EXCEEDED -> DangerRed
                        }
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tersisa",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", budget.remaining)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (budget.remaining >= 0) BibitGreen else DangerRed
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetHistoryCard(
    period: BudgetPeriod,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = period.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                    Text(
                        text = period.dateRange,
                        fontSize = 12.sp,
                        color = MediumGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Text(
                            text = "Anggaran: Rp ${String.format("%,.0f", period.totalBudget)}",
                            fontSize = 12.sp,
                            color = MediumGray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Terpakai: Rp ${String.format("%,.0f", period.totalSpent)}",
                            fontSize = 12.sp,
                            color = if (period.totalSpent <= period.totalBudget) BibitGreen else DangerRed
                        )
                    }
                }

                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MediumGray
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = SoftGray)
                Spacer(modifier = Modifier.height(16.dp))

                period.budgets.forEach { budget ->
                    BudgetHistoryItem(budget)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BudgetHistoryItem(budget: Budget) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = budget.categoryIcon,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = budget.category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Rp ${String.format("%,.0f", budget.spent)} / Rp ${String.format("%,.0f", budget.limit)}",
                fontSize = 12.sp,
                color = if (budget.spent <= budget.limit) DarkGray else DangerRed
            )
            Text(
                text = "${(budget.progressPercentage * 100).toInt()}%",
                fontSize = 10.sp,
                color = MediumGray
            )
        }
    }
}

fun generateMockBudgets(): List<Budget> {
    return listOf(
        Budget(
            id = "1",
            category = "Makanan",
            categoryIcon = "🍔",
            limit = 1500000.0,
            spent = 850000.0,
            period = "Bulanan",
            startDate = "1 Des",
            endDate = "31 Des",
            isActive = true
        ),
        Budget(
            id = "2",
            category = "Transportasi",
            categoryIcon = "🚗",
            limit = 800000.0,
            spent = 720000.0,
            period = "Bulanan",
            startDate = "1 Des",
            endDate = "31 Des",
            isActive = true
        ),
        Budget(
            id = "3",
            category = "Hiburan",
            categoryIcon = "🎬",
            limit = 500000.0,
            spent = 520000.0,
            period = "Bulanan",
            startDate = "1 Des",
            endDate = "31 Des",
            isActive = true
        ),
        Budget(
            id = "4",
            category = "Belanja",
            categoryIcon = "🛍️",
            limit = 1000000.0,
            spent = 450000.0,
            period = "Bulanan",
            startDate = "1 Des",
            endDate = "31 Des",
            isActive = true
        ),
        Budget(
            id = "5",
            category = "Kesehatan",
            categoryIcon = "🏥",
            limit = 300000.0,
            spent = 150000.0,
            period = "Bulanan",
            startDate = "1 Nov",
            endDate = "30 Nov",
            isActive = false
        )
    )
}

private fun generateMockBudgetHistory(): List<BudgetPeriod> {
    return listOf(
        BudgetPeriod(
            id = "1",
            name = "November 2024",
            dateRange = "1 Nov - 30 Nov 2024",
            totalBudget = 3500000.0,
            totalSpent = 3200000.0,
            budgets = listOf(
                Budget("h1", "Makanan", "🍔", 1500000.0, 1400000.0, "Bulanan", "1 Nov", "30 Nov"),
                Budget("h2", "Transportasi", "🚗", 800000.0, 750000.0, "Bulanan", "1 Nov", "30 Nov"),
                Budget("h3", "Hiburan", "🎬", 500000.0, 480000.0, "Bulanan", "1 Nov", "30 Nov"),
                Budget("h4", "Belanja", "🛍️", 700000.0, 570000.0, "Bulanan", "1 Nov", "30 Nov")
            )
        ),
        BudgetPeriod(
            id = "2",
            name = "Oktober 2024",
            dateRange = "1 Okt - 31 Okt 2024",
            totalBudget = 3200000.0,
            totalSpent = 3450000.0,
            budgets = listOf(
                Budget("h5", "Makanan", "🍔", 1400000.0, 1550000.0, "Bulanan", "1 Okt", "31 Okt"),
                Budget("h6", "Transportasi", "🚗", 750000.0, 800000.0, "Bulanan", "1 Okt", "31 Okt"),
                Budget("h7", "Hiburan", "🎬", 450000.0, 600000.0, "Bulanan", "1 Okt", "31 Okt"),
                Budget("h8", "Belanja", "🛍️", 600000.0, 500000.0, "Bulanan", "1 Okt", "31 Okt")
            )
        ),
        BudgetPeriod(
            id = "3",
            name = "September 2024",
            dateRange = "1 Sep - 30 Sep 2024",
            totalBudget = 3000000.0,
            totalSpent = 2850000.0,
            budgets = listOf(
                Budget("h9", "Makanan", "🍔", 1300000.0, 1200000.0, "Bulanan", "1 Sep", "30 Sep"),
                Budget("h10", "Transportasi", "🚗", 700000.0, 680000.0, "Bulanan", "1 Sep", "30 Sep"),
                Budget("h11", "Hiburan", "🎬", 400000.0, 370000.0, "Bulanan", "1 Sep", "30 Sep"),
                Budget("h12", "Belanja", "🛍️", 600000.0, 600000.0, "Bulanan", "1 Sep", "30 Sep")
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun BudgetMainScreenPreview() {
    BudgetMainScreen(rememberNavController())
}
