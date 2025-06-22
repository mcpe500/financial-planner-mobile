package com.example.financialplannerapp.ui.screen.budget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.data.model.Budget
import com.example.financialplannerapp.data.model.generateMockBudgets
import kotlin.math.cos
import kotlin.math.sin
import com.example.financialplannerapp.ui.theme.BibitGreen
import com.example.financialplannerapp.ui.theme.BibitLightGreen
import com.example.financialplannerapp.ui.theme.MediumGray
import com.example.financialplannerapp.ui.theme.DangerRed
import com.example.financialplannerapp.ui.theme.WarningOrange
import com.example.financialplannerapp.ui.theme.MotivationalOrange
import com.example.financialplannerapp.ui.theme.DarkGray
import com.example.financialplannerapp.ui.theme.SoftGray

data class BudgetInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

enum class InsightType {
    SUCCESS, WARNING, DANGER, INFO
}

// Chart colors - defined locally for chart purposes
private val ChartColors = listOf(
    Color(0xFF4CAF50), // Green
    Color(0xFFFF9800), // Orange
    Color(0xFFF44336), // Red
    Color(0xFF2196F3), // Blue
    Color(0xFF9C27B0), // Purple
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF795548), // Brown
    Color(0xFF607D8B)  // Blue Grey
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetAnalyticsScreen(navController: NavController) {
    val budgets = remember { generateMockBudgets() }
    val insights = remember { generateBudgetInsights() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Analisis Anggaran",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Budget Distribution Chart
            item {
                BudgetDistributionCard(budgets)
            }

            // Monthly Comparison
            item {
                MonthlyComparisonCard()
            }

            // Budget Performance
            item {
                BudgetPerformanceCard(budgets)
            }

            // Insights Section
            item {
                Text(
                    text = "Wawasan & Rekomendasi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(insights) { insight ->
                InsightCard(insight)
            }
        }
    }
}

@Composable
private fun BudgetDistributionCard(budgets: List<Budget>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Distribusi Anggaran",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pie Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.size(180.dp)
                ) {
                    drawBudgetPieChart(budgets)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            budgets.forEachIndexed { index, budget ->
                BudgetLegendItem(
                    budget = budget,
                    color = getBudgetColor(index)
                )
                if (index < budgets.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BudgetLegendItem(budget: Budget, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = budget.name,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${budget.progressPercentage * 100}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun getBudgetColor(index: Int): Color {
    return ChartColors[index % ChartColors.size]
}

private fun DrawScope.drawBudgetPieChart(budgets: List<Budget>) {
    val totalAmount = budgets.sumOf { it.amount }
    if (totalAmount <= 0) return

    var startAngle = 0f
    val center = Offset(size.width / 2, size.height / 2)
    val radius = minOf(size.width, size.height) / 2 * 0.8f

    budgets.forEachIndexed { index, budget ->
        val sweepAngle = (budget.amount / totalAmount * 360).toFloat()
        val color = getBudgetColor(index)
        
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
        
        startAngle += sweepAngle
    }
}

@Composable
private fun MonthlyComparisonCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Perbandingan Bulanan",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Current vs Previous Month
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MonthComparisonItem(
                    title = "Bulan Ini",
                    amount = 3740000.0,
                    subtitle = "Total Terpakai",
                    color = BibitGreen
                )
                MonthComparisonItem(
                    title = "Bulan Lalu",
                    amount = 3450000.0,
                    subtitle = "Total Terpakai",
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
private fun MonthComparisonItem(
    title: String,
    amount: Double,
    subtitle: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Rp ${String.format("%,.0f", amount)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BudgetPerformanceCard(budgets: List<Budget>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Performa Anggaran",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            budgets.forEach { budget ->
                BudgetPerformanceItem(budget)
                if (budget != budgets.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun BudgetPerformanceItem(budget: Budget) {
    val progressColor = when {
        budget.progressPercentage > 1.0f -> DangerRed
        budget.progressPercentage > 0.8f -> WarningOrange
        else -> BibitGreen
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = budget.categoryIcon,
                    contentDescription = budget.category,
                    tint = progressColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = budget.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "${(budget.progressPercentage * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = progressColor
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = budget.progressPercentage,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.background
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Terpakai: Rp ${String.format("%,.0f", budget.spentAmount)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Limit: Rp ${String.format("%,.0f", budget.amount)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InsightCard(insight: BudgetInsight) {
    val backgroundColor = when (insight.type) {
        InsightType.SUCCESS -> Color(0xFFE8F5E8)
        InsightType.WARNING -> Color(0xFFFFF3E0)
        InsightType.DANGER -> Color(0xFFFFEBEE)
        InsightType.INFO -> Color(0xFFE3F2FD)
    }
    
    val iconColor = when (insight.type) {
        InsightType.SUCCESS -> BibitGreen
        InsightType.WARNING -> WarningOrange
        InsightType.DANGER -> DangerRed
        InsightType.INFO -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = insight.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = insight.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun generateBudgetInsights(): List<BudgetInsight> {
    return listOf(
        BudgetInsight(
            title = "Anggaran Makanan Terlampaui",
            description = "Anda telah menghabiskan 90% dari anggaran makanan bulan ini. Pertimbangkan untuk mengurangi pengeluaran di kategori ini.",
            type = InsightType.WARNING,
            icon = Icons.Default.Restaurant
        ),
        BudgetInsight(
            title = "Tabungan Meningkat",
            description = "Tabungan Anda meningkat 15% dibanding bulan lalu. Pertahankan kebiasaan baik ini!",
            type = InsightType.SUCCESS,
            icon = Icons.Default.TrendingUp
        ),
        BudgetInsight(
            title = "Anggaran Transportasi Optimal",
            description = "Pengeluaran transportasi masih dalam batas wajar. Anda menghemat 20% dari anggaran yang dialokasikan.",
            type = InsightType.SUCCESS,
            icon = Icons.Default.DirectionsCar
        )
    )
}

@Preview
@Composable
fun BudgetAnalyticsScreenPreview() {
    BudgetAnalyticsScreen(rememberNavController())
}
