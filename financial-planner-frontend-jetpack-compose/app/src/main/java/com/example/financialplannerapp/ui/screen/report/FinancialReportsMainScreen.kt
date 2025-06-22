package com.example.financialplannerapp.ui.screen.report

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*
import com.example.financialplannerapp.core.util.formatCurrency

private const val TAG_REPORTS = "FinancialReportsMainScreen"

// Enhanced color palette for financial reports
private val BibitGreen = Color(0xFF4CAF50)
private val IncomeGreen = Color(0xFF2E7D32)
private val ExpenseRed = Color(0xFFD32F2F)
private val NetWorthBlue = Color(0xFF1976D2)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val LightBlue = Color(0xFFE3F2FD)
private val LightGreen = Color(0xFFE8F5E8)
private val LightRed = Color(0xFFFFEBEE)

// Report types enum
enum class ReportType(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    CASH_FLOW("Arus Kas", Icons.Default.TrendingUp),
    CATEGORY_SPENDING("Per Kategori", Icons.Default.PieChart),
    TREND_ANALYSIS("Tren", Icons.Default.ShowChart),
    NET_WORTH("Aset Bersih", Icons.Default.AccountBalance)
}

// Time period filter
enum class TimePeriod(val label: String) {
    THIS_MONTH("Bulan Ini"),
    LAST_MONTH("Bulan Lalu"),
    LAST_3_MONTHS("3 Bulan"),
    LAST_6_MONTHS("6 Bulan"),
    THIS_YEAR("Tahun Ini"),
    CUSTOM("Custom")
}

// Data classes for reports
data class CashFlowData(
    val period: String,
    val totalIncome: Double,
    val totalExpenses: Double,
    val netCashFlow: Double
)

data class CategorySpending(
    val categoryName: String,
    val icon: String,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

data class TrendData(
    val period: String,
    val income: Double,
    val expenses: Double,
    val date: Date
)

data class NetWorthData(
    val totalAssets: Double,
    val totalLiabilities: Double,
    val netWorth: Double,
    val previousNetWorth: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialReportsMainScreen(navController: NavController) {
    Log.d(TAG_REPORTS, "FinancialReportsMainScreen composing...")

    var selectedReportType by remember { mutableStateOf(ReportType.CASH_FLOW) }
    var selectedTimePeriod by remember { mutableStateOf(TimePeriod.THIS_MONTH) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan Keuangan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
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
        ) {
            // Report Type Tabs
            ReportTypeTabs(
                selectedType = selectedReportType,
                onTypeSelected = { selectedReportType = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Time Period Filter (for applicable reports)
            if (selectedReportType != ReportType.NET_WORTH) {
                TimePeriodFilter(
                    selectedPeriod = selectedTimePeriod,
                    onPeriodSelected = { selectedTimePeriod = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Report Content
            when (selectedReportType) {
                ReportType.CASH_FLOW -> CashFlowReport(
                    timePeriod = selectedTimePeriod,
                    modifier = Modifier.padding(16.dp)
                )
                ReportType.CATEGORY_SPENDING -> CategorySpendingReport(
                    timePeriod = selectedTimePeriod,
                    modifier = Modifier.padding(16.dp)
                )
                ReportType.TREND_ANALYSIS -> TrendAnalysisReport(
                    timePeriod = selectedTimePeriod,
                    modifier = Modifier.padding(16.dp)
                )
                ReportType.NET_WORTH -> NetWorthReport(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ReportTypeTabs(
    selectedType: ReportType,
    onTypeSelected: (ReportType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ReportType.values().forEach { type ->
                val isSelected = selectedType == type

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) BibitGreen.copy(alpha = 0.1f)
                            else Color.Transparent
                        )
                        .clickable { onTypeSelected(type) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = type.title,
                        tint = if (isSelected) BibitGreen else MediumGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = type.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) BibitGreen else MediumGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TimePeriodFilter(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(TimePeriod.values()) { period ->
            val isSelected = selectedPeriod == period

            FilterChip(
                onClick = { onPeriodSelected(period) },
                label = {
                    Text(
                        text = period.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BibitGreen,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = DarkGray
                ),
//                border = FilterChipDefaults.filterChipBorder(
//                    borderColor = if (isSelected) BibitGreen else MediumGray.copy(alpha = 0.3f)
//                )
            )
        }
    }
}

@Composable
private fun CashFlowReport(
    timePeriod: TimePeriod,
    modifier: Modifier = Modifier
) {
    // Mock data
    val cashFlowData = remember {
        CashFlowData(
            period = "November 2024",
            totalIncome = 8500000.0,
            totalExpenses = 6200000.0,
            netCashFlow = 2300000.0
        )
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card
        CashFlowSummaryCard(cashFlowData)

        // Visual Chart
        CashFlowChart(cashFlowData)

        // Breakdown Details
        CashFlowBreakdown(cashFlowData)
    }
}

@Composable
private fun CashFlowSummaryCard(data: CashFlowData) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = BibitGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Arus Kas - ${data.period}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
            }

            // Net Cash Flow
            val netFlowColor = if (data.netCashFlow >= 0) IncomeGreen else ExpenseRed
            val netFlowIcon = if (data.netCashFlow >= 0) "📈" else "📉"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = netFlowIcon,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Arus Kas Bersih",
                        fontSize = 14.sp,
                        color = MediumGray
                    )
                    Text(
                        text = formatCurrencyAmount(data.netCashFlow),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = netFlowColor
                    )
                }
            }
        }
    }
}

@Composable
private fun CashFlowChart(data: CashFlowData) {
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
                text = "Perbandingan Pemasukan vs Pengeluaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bar Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                drawCashFlowBars(data.totalIncome, data.totalExpenses)
            }

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChartLegendItem(
                    color = IncomeGreen,
                    label = "Pemasukan",
                    amount = formatCurrencyAmount(data.totalIncome)
                )
                ChartLegendItem(
                    color = ExpenseRed,
                    label = "Pengeluaran",
                    amount = formatCurrencyAmount(data.totalExpenses)
                )
            }
        }
    }
}

@Composable
private fun CashFlowBreakdown(data: CashFlowData) {
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
                text = "Detail Arus Kas",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Income Row
            CashFlowDetailRow(
                icon = "💰",
                label = "Total Pemasukan",
                amount = data.totalIncome,
                color = IncomeGreen,
                backgroundColor = LightGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expenses Row
            CashFlowDetailRow(
                icon = "💸",
                label = "Total Pengeluaran",
                amount = data.totalExpenses,
                color = ExpenseRed,
                backgroundColor = LightRed
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = SoftGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Net Cash Flow Row
            CashFlowDetailRow(
                icon = if (data.netCashFlow >= 0) "📈" else "📉",
                label = "Arus Kas Bersih",
                amount = data.netCashFlow,
                color = if (data.netCashFlow >= 0) IncomeGreen else ExpenseRed,
                backgroundColor = if (data.netCashFlow >= 0) LightGreen else LightRed
            )
        }
    }
}

@Composable
private fun CashFlowDetailRow(
    icon: String,
    label: String,
    amount: Double,
    color: Color,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )
        }

        Text(
            text = formatCurrencyAmount(amount),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CategorySpendingReport(
    timePeriod: TimePeriod,
    modifier: Modifier = Modifier
) {
    // Mock data
    val categoryData = remember {
        listOf(
            CategorySpending("Makanan & Minuman", "🍽️", 2100000.0, 33.9f, Color(0xFFFF6B6B)),
            CategorySpending("Transportasi", "🚗", 1200000.0, 19.4f, Color(0xFF4ECDC4)),
            CategorySpending("Belanja", "🛒", 980000.0, 15.8f, Color(0xFF45B7D1)),
            CategorySpending("Hiburan", "🎬", 750000.0, 12.1f, Color(0xFF96CEB4)),
            CategorySpending("Kesehatan", "🏥", 620000.0, 10.0f, Color(0xFFFECA57)),
            CategorySpending("Lainnya", "📦", 550000.0, 8.8f, Color(0xFFFF9FF3))
        )
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pie Chart
        CategoryPieChart(categoryData)

        // Category List
        CategorySpendingList(categoryData)
    }
}

@Composable
private fun CategoryPieChart(categories: List<CategorySpending>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pengeluaran per Kategori",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pie Chart
            Canvas(
                modifier = Modifier.size(200.dp)
            ) {
                drawPieChart(categories)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Amount
            Text(
                text = "Total Pengeluaran",
                fontSize = 14.sp,
                color = MediumGray
            )
            Text(
                text = formatCurrencyAmount(categories.sumOf { it.amount }),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )
        }
    }
}

@Composable
private fun CategorySpendingList(categories: List<CategorySpending>) {
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
                text = "Detail per Kategori",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            categories.forEach { category ->
                CategorySpendingItem(category)
                if (category != categories.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CategorySpendingItem(category: CategorySpending) {
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
                text = category.icon,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = category.categoryName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray
                )
                Text(
                    text = "${category.percentage}%",
                    fontSize = 12.sp,
                    color = MediumGray
                )
            }
        }

        Text(
            text = formatCurrencyAmount(category.amount),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = category.color
        )
    }
}

@Composable
private fun TrendAnalysisReport(
    timePeriod: TimePeriod,
    modifier: Modifier = Modifier
) {
    var showIncome by remember { mutableStateOf(true) }
    var showExpenses by remember { mutableStateOf(true) }

    // Mock trend data
    val trendData = remember {
        listOf(
            TrendData("Jan", 7500000.0, 5200000.0, Date()),
            TrendData("Feb", 8200000.0, 5800000.0, Date()),
            TrendData("Mar", 7800000.0, 6100000.0, Date()),
            TrendData("Apr", 8500000.0, 5900000.0, Date()),
            TrendData("May", 8100000.0, 6300000.0, Date()),
            TrendData("Jun", 8800000.0, 6000000.0, Date())
        )
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle Controls
        TrendToggleControls(
            showIncome = showIncome,
            showExpenses = showExpenses,
            onIncomeToggle = { showIncome = it },
            onExpensesToggle = { showExpenses = it }
        )

        // Trend Chart
        TrendChart(
            data = trendData,
            showIncome = showIncome,
            showExpenses = showExpenses
        )

        // Trend Summary
        TrendSummary(trendData)
    }
}

@Composable
private fun TrendToggleControls(
    showIncome: Boolean,
    showExpenses: Boolean,
    onIncomeToggle: (Boolean) -> Unit,
    onExpensesToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onIncomeToggle(!showIncome) }
                    .padding(8.dp)
            ) {
                Checkbox(
                    checked = showIncome,
                    onCheckedChange = onIncomeToggle,
                    colors = CheckboxDefaults.colors(checkedColor = IncomeGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pemasukan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (showIncome) IncomeGreen else MediumGray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onExpensesToggle(!showExpenses) }
                    .padding(8.dp)
            ) {
                Checkbox(
                    checked = showExpenses,
                    onCheckedChange = onExpensesToggle,
                    colors = CheckboxDefaults.colors(checkedColor = ExpenseRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pengeluaran",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (showExpenses) ExpenseRed else MediumGray
                )
            }
        }
    }
}

@Composable
private fun TrendChart(
    data: List<TrendData>,
    showIncome: Boolean,
    showExpenses: Boolean
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
                text = "Tren 6 Bulan Terakhir",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Line Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                drawTrendChart(data, showIncome, showExpenses)
            }
        }
    }
}

@Composable
private fun TrendSummary(data: List<TrendData>) {
    val avgIncome = data.map { it.income }.average()
    val avgExpenses = data.map { it.expenses }.average()
    val trend = if (data.last().income > data.first().income) "📈" else "📉"

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
                text = "Ringkasan Tren",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrendSummaryItem(
                    icon = "💰",
                    label = "Rata-rata Pemasukan",
                    value = formatCurrencyAmount(avgIncome),
                    color = IncomeGreen
                )

                TrendSummaryItem(
                    icon = "💸",
                    label = "Rata-rata Pengeluaran",
                    value = formatCurrencyAmount(avgExpenses),
                    color = ExpenseRed
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trend,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tren ${if (trend == "📈") "Positif" else "Negatif"}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (trend == "📈") IncomeGreen else ExpenseRed
                )
            }
        }
    }
}

@Composable
private fun TrendSummaryItem(
    icon: String,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MediumGray,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun NetWorthReport(
    modifier: Modifier = Modifier
) {
    // Mock data
    val netWorthData = remember {
        NetWorthData(
            totalAssets = 125000000.0,
            totalLiabilities = 45000000.0,
            netWorth = 80000000.0,
            previousNetWorth = 75000000.0
        )
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Net Worth Summary
        NetWorthSummaryCard(netWorthData)

        // Assets vs Liabilities
        AssetsLiabilitiesCard(netWorthData)

        // Net Worth Trend (simplified)
        NetWorthTrendCard(netWorthData)
    }
}

@Composable
private fun NetWorthSummaryCard(data: NetWorthData) {
    val netWorthChange = data.netWorth - data.previousNetWorth
    val changePercentage = (netWorthChange / data.previousNetWorth * 100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NetWorthBlue)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kekayaan Bersih",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Text(
                text = formatCurrencyAmount(data.netWorth),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (netWorthChange >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (netWorthChange >= 0) "+" else ""}${formatCurrencyAmount(netWorthChange)} (${String.format("%.1f", changePercentage)}%)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun AssetsLiabilitiesCard(data: NetWorthData) {
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
                text = "Aset vs Kewajiban",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Assets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightGreen, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏦", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Total Aset",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                }
                Text(
                    text = formatCurrencyAmount(data.totalAssets),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = IncomeGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Liabilities
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightRed, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💳", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Total Kewajiban",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                }
                Text(
                    text = formatCurrencyAmount(data.totalLiabilities),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseRed
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = SoftGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // Net Worth Calculation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlue, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💎", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Kekayaan Bersih",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                }
                Text(
                    text = formatCurrencyAmount(data.netWorth),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NetWorthBlue
                )
            }
        }
    }
}

@Composable
private fun NetWorthTrendCard(data: NetWorthData) {
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
                text = "Tren Kekayaan Bersih",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Simple trend visualization
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                drawNetWorthTrend(data.previousNetWorth, data.netWorth)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Bulan Lalu",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = formatCurrencyAmount(data.previousNetWorth),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Bulan Ini",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = formatCurrencyAmount(data.netWorth),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NetWorthBlue
                    )
                }
            }
        }
    }
}

// Helper functions for drawing charts
private fun DrawScope.drawCashFlowBars(income: Double, expenses: Double) {
    val maxValue = maxOf(income, expenses)
    val barWidth = size.width * 0.3f
    val spacing = size.width * 0.1f
    val maxHeight = size.height * 0.8f

    // Income bar
    val incomeHeight = (income / maxValue * maxHeight).toFloat()
    drawRect(
        color = IncomeGreen,
        topLeft = Offset(spacing, size.height - incomeHeight),
        size = Size(barWidth, incomeHeight)
    )

    // Expenses bar
    val expensesHeight = (expenses / maxValue * maxHeight).toFloat()
    drawRect(
        color = ExpenseRed,
        topLeft = Offset(spacing * 2 + barWidth, size.height - expensesHeight),
        size = Size(barWidth, expensesHeight)
    )
}

private fun DrawScope.drawPieChart(categories: List<CategorySpending>) {
    val total = categories.sumOf { it.amount }
    var startAngle = 0f
    val radius = size.minDimension / 2 * 0.8f
    val center = Offset(size.width / 2, size.height / 2)

    categories.forEach { category ->
        val sweepAngle = (category.amount / total * 360).toFloat()

        drawArc(
            color = category.color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        startAngle += sweepAngle
    }
}

private fun DrawScope.drawTrendChart(
    data: List<TrendData>,
    showIncome: Boolean,
    showExpenses: Boolean
) {
    if (data.isEmpty()) return

    val maxValue = maxOf(
        if (showIncome) data.maxOf { it.income } else 0.0,
        if (showExpenses) data.maxOf { it.expenses } else 0.0
    )

    val stepX = size.width / (data.size - 1)
    val padding = 20.dp.toPx()

    // Draw income line
    if (showIncome) {
        val incomePoints = data.mapIndexed { index, item ->
            Offset(
                x = index * stepX,
                y = size.height - (item.income / maxValue * (size.height - padding * 2)).toFloat() - padding
            )
        }

        for (i in 0 until incomePoints.size - 1) {
            drawLine(
                color = IncomeGreen,
                start = incomePoints[i],
                end = incomePoints[i + 1],
                strokeWidth = 3.dp.toPx()
            )
        }

        // Draw points
        incomePoints.forEach { point ->
            drawCircle(
                color = IncomeGreen,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }

    // Draw expenses line
    if (showExpenses) {
        val expensePoints = data.mapIndexed { index, item ->
            Offset(
                x = index * stepX,
                y = size.height - (item.expenses / maxValue * (size.height - padding * 2)).toFloat() - padding
            )
        }

        for (i in 0 until expensePoints.size - 1) {
            drawLine(
                color = ExpenseRed,
                start = expensePoints[i],
                end = expensePoints[i + 1],
                strokeWidth = 3.dp.toPx()
            )
        }

        // Draw points
        expensePoints.forEach { point ->
            drawCircle(
                color = ExpenseRed,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

private fun DrawScope.drawNetWorthTrend(previousValue: Double, currentValue: Double) {
    val maxValue = maxOf(previousValue, currentValue)
    val minValue = minOf(previousValue, currentValue)
    val range = maxValue - minValue
    val padding = 20.dp.toPx()

    val startY = size.height - ((previousValue - minValue) / range * (size.height - padding * 2)).toFloat() - padding
    val endY = size.height - ((currentValue - minValue) / range * (size.height - padding * 2)).toFloat() - padding

    val startPoint = Offset(padding, startY)
    val endPoint = Offset(size.width - padding, endY)

    // Draw line
    drawLine(
        color = NetWorthBlue,
        start = startPoint,
        end = endPoint,
        strokeWidth = 4.dp.toPx()
    )

    // Draw points
    drawCircle(
        color = NetWorthBlue,
        radius = 6.dp.toPx(),
        center = startPoint
    )

    drawCircle(
        color = NetWorthBlue,
        radius = 6.dp.toPx(),
        center = endPoint
    )
}

@Composable
private fun ChartLegendItem(
    color: Color,
    label: String,
    amount: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MediumGray
            )
            Text(
                text = amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )
        }
    }
}

// Utility function for currency formatting
@Composable
private fun formatCurrencyAmount(amount: Double): String {
    return formatCurrency(amount)
}

@Preview(showBackground = true)
@Composable
fun FinancialReportsMainScreenPreview() {
    FinancialReportsMainScreen(navController = rememberNavController())
}
