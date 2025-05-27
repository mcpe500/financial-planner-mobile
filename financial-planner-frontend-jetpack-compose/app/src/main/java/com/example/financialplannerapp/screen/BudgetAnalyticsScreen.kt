package com.example.financialplannerapp.screen

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
import kotlin.math.cos
import kotlin.math.sin

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val WarningOrange = Color(0xFFFF9800)
private val DangerRed = Color(0xFFFF5722)

data class BudgetInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

enum class InsightType {
    SUCCESS, WARNING, DANGER, INFO
}

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
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
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
                    color = DarkGray
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Distribusi Anggaran",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
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
private fun MonthlyComparisonCard() {
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
                text = "Perbandingan Bulanan",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
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

            Spacer(modifier = Modifier.height(16.dp))

            // Comparison indicator
            val difference = 3740000.0 - 3450000.0
            val percentageChange = (difference / 3450000.0) * 100

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (difference > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = "Trend",
                    tint = if (difference > 0) DangerRed else BibitGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${if (difference > 0) "+" else ""}${String.format("%.1f", percentageChange)}% dari bulan lalu",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (difference > 0) DangerRed else BibitGreen
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
            fontWeight = FontWeight.Medium,
            color = DarkGray
        )
        Text(
            text = "Rp ${String.format("%,.0f", amount)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MediumGray
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Performa Anggaran",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            budgets.forEach { budget ->
                BudgetPerformanceItem(budget)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun BudgetPerformanceItem(budget: Budget) {
    Column {
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

            Text(
                text = "${(budget.progressPercentage * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = when (budget.status) {
                    BudgetStatus.SAFE -> BibitGreen
                    BudgetStatus.WARNING -> WarningOrange
                    BudgetStatus.EXCEEDED -> DangerRed
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

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

@Composable
private fun InsightCard(insight: BudgetInsight) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                insight.icon,
                contentDescription = insight.title,
                tint = when (insight.type) {
                    InsightType.SUCCESS -> BibitGreen
                    InsightType.WARNING -> WarningOrange
                    InsightType.DANGER -> DangerRed
                    InsightType.INFO -> Color(0xFF2196F3)
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
                Text(
                    text = insight.description,
                    fontSize = 12.sp,
                    color = MediumGray,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun BudgetLegendItem(
    budget: Budget,
    color: Color
) {
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
                    .size(12.dp)
                    .background(color, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${budget.categoryIcon} ${budget.category}",
                fontSize = 12.sp,
                color = DarkGray
            )
        }
        Text(
            text = "Rp ${String.format("%,.0f", budget.limit)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGray
        )
    }
}

private fun DrawScope.drawBudgetPieChart(budgets: List<Budget>) {
    val total = budgets.sumOf { it.limit }
    var startAngle = 0f

    budgets.forEachIndexed { index, budget ->
        val sweepAngle = (budget.limit / total * 360).toFloat()
        val color = getBudgetColor(index)

        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
            size = Size(size.width * 0.8f, size.height * 0.8f)
        )

        startAngle += sweepAngle
    }
}

private fun getBudgetColor(index: Int): Color {
    val colors = listOf(
        BibitGreen,
        Color(0xFF2196F3),
        WarningOrange,
        Color(0xFF9C27B0),
        DangerRed,
        Color(0xFF00BCD4),
        Color(0xFF795548),
        Color(0xFF607D8B)
    )
    return colors[index % colors.size]
}

private fun generateBudgetInsights(): List<BudgetInsight> {
    return listOf(
        BudgetInsight(
            title = "Anggaran Hiburan Terlampaui",
            description = "Anda telah melebihi anggaran hiburan sebesar Rp 20.000. Pertimbangkan untuk mengurangi pengeluaran hiburan minggu depan.",
            type = InsightType.DANGER,
            icon = Icons.Default.Warning
        ),
        BudgetInsight(
            title = "Pengeluaran Transportasi Meningkat",
            description = "Pengeluaran transportasi Anda meningkat 15% dari bulan lalu. Mungkin saatnya mencari alternatif transportasi yang lebih hemat.",
            type = InsightType.WARNING,
            icon = Icons.Default.TrendingUp
        ),
        BudgetInsight(
            title = "Anggaran Makanan Terkendali",
            description = "Selamat! Anda berhasil menghemat 12% dari anggaran makanan bulan ini. Pertahankan kebiasaan baik ini.",
            type = InsightType.SUCCESS,
            icon = Icons.Default.CheckCircle
        ),
        BudgetInsight(
            title = "Saran Optimasi Anggaran",
            description = "Berdasarkan pola pengeluaran Anda, pertimbangkan untuk menaikkan anggaran transportasi dan menurunkan anggaran belanja.",
            type = InsightType.INFO,
            icon = Icons.Default.Lightbulb
        )
    )
}

@Preview(showBackground = true)
@Composable
fun BudgetAnalyticsScreenPreview() {
    BudgetAnalyticsScreen(rememberNavController())
}
