package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val ExpenseRed = Color(0xFFE53E3E)
private val ReceivableGreen = Color(0xFF38A169)

// Data classes
data class DebtReceivable(
    val id: String,
    val name: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val description: String,
    val type: DebtReceivableType,
    val dueDate: Date,
    val createdDate: Date,
    val payments: List<Payment> = emptyList()
) {
    val remainingAmount: Double get() = totalAmount - paidAmount
    val progressPercentage: Float get() = if (totalAmount > 0) (paidAmount / totalAmount).toFloat() else 0f
    val isOverdue: Boolean get() = Date().after(dueDate) && remainingAmount > 0
    val isDueSoon: Boolean
        get() = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }.time.let {
            dueDate.before(it) && !isOverdue && remainingAmount > 0
        }
}

data class Payment(
    val id: String,
    val amount: Double,
    val date: Date,
    val note: String = ""
)

enum class DebtReceivableType(val label: String, val icon: String, val color: Color) {
    DEBT("Hutang", "💸", ExpenseRed),
    RECEIVABLE("Piutang", "💰", ReceivableGreen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtReceivableMainScreen(
    onNavigateToAdd: () -> Unit = {},
    onNavigateToDetails: (DebtReceivable) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Semua", "Hutang", "Piutang")

    // Mock data
    val mockData = remember { getMockDebtReceivableData() }

    val filteredData = mockData.filter {
        when (selectedTab) {
            0 -> true // Semua
            1 -> it.type == DebtReceivableType.DEBT // Hutang
            2 -> it.type == DebtReceivableType.RECEIVABLE // Piutang
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hutang & Piutang",
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = BibitGreen,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Hutang/Piutang",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(paddingValues)
        ) {
            // Summary Section
            SummarySection(mockData)

            // Tab Section
            TabSection(
                selectedTab = selectedTab,
                tabs = tabs,
                onTabSelected = { selectedTab = it }
            )

            // Content Section
            ContentSection(
                filteredData = filteredData,
                selectedTab = selectedTab,
                onItemClick = onNavigateToDetails
            )
        }
    }
}

@Composable
private fun SummarySection(data: List<DebtReceivable>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Ringkasan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Total Hutang",
                    amount = data.filter { it.type == DebtReceivableType.DEBT }
                        .sumOf { it.remainingAmount },
                    color = DebtReceivableType.DEBT.color,
                    icon = "💸",
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Total Piutang",
                    amount = data.filter { it.type == DebtReceivableType.RECEIVABLE }
                        .sumOf { it.remainingAmount },
                    color = DebtReceivableType.RECEIVABLE.color,
                    icon = "💰",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickStat(
                    label = "Jatuh Tempo",
                    value = "${data.count { it.isOverdue }}",
                    color = ExpenseRed,
                    modifier = Modifier.weight(1f)
                )

                QuickStat(
                    label = "Segera Jatuh Tempo",
                    value = "${data.count { it.isDueSoon }}",
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )

                QuickStat(
                    label = "Lunas",
                    value = "${data.count { it.remainingAmount <= 0 }}",
                    color = ReceivableGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                    .format(amount).replace("IDR", "Rp"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun QuickStat(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            fontSize = 10.sp,
            color = MediumGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TabSection(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = BibitGreen,
            indicator = { tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BibitGreen,
                    height = 3.dp
                )
            },
            divider = {},
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (selectedTab == index) BibitGreen else MediumGray
                        )

                        if (selectedTab == index) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(BibitGreen, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentSection(
    filteredData: List<DebtReceivable>,
    selectedTab: Int,
    onItemClick: (DebtReceivable) -> Unit
) {
    if (filteredData.isEmpty()) {
        EmptyStateCard(selectedTab = selectedTab)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredData.sortedWith(
                compareBy<DebtReceivable> { !it.isOverdue }
                    .thenBy { !it.isDueSoon }
                    .thenBy { it.dueDate }
            )) { item ->
                DebtReceivableItemCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DebtReceivableItemCard(
    item: DebtReceivable,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(item.type.color.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.type.icon,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (item.description.isNotEmpty()) {
                            Text(
                                text = item.description,
                                fontSize = 12.sp,
                                color = MediumGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Status indicator
                StatusIndicator(item = item)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Sisa",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            .format(item.remainingAmount).replace("IDR", "Rp"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.remainingAmount <= 0) ReceivableGreen else item.type.color
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Jatuh Tempo",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                            .format(item.dueDate),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            item.isOverdue -> ExpenseRed
                            item.isDueSoon -> Color(0xFFFF9800)
                            else -> DarkGray
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            ProgressSection(item = item)
        }
    }
}

@Composable
private fun StatusIndicator(item: DebtReceivable) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.remainingAmount <= 0) {
            Box(
                modifier = Modifier
                    .background(ReceivableGreen, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Lunas",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else if (item.isOverdue) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Overdue",
                tint = ExpenseRed,
                modifier = Modifier.size(20.dp)
            )
        } else if (item.isDueSoon) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFFF9800), CircleShape)
            )
        }
    }
}

@Composable
private fun ProgressSection(item: DebtReceivable) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progress",
                fontSize = 12.sp,
                color = MediumGray
            )
            Text(
                text = "${(item.progressPercentage * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (item.progressPercentage >= 1f) ReceivableGreen else item.type.color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = item.progressPercentage,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (item.progressPercentage >= 1f) ReceivableGreen else item.type.color,
            trackColor = item.type.color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun EmptyStateCard(selectedTab: Int) {
    val (icon, title, subtitle) = when (selectedTab) {
        1 -> Triple("💸", "Belum ada hutang", "Tap tombol + untuk menambah hutang baru")
        2 -> Triple("💰", "Belum ada piutang", "Tap tombol + untuk menambah piutang baru")
        else -> Triple("📊", "Belum ada data", "Tap tombol + untuk menambah hutang atau piutang")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MediumGray
            )
        }
    }
}

// Mock data function
private fun getMockDebtReceivableData(): List<DebtReceivable> {
    return listOf(
        DebtReceivable(
            id = "1",
            name = "Pinjaman Bank BCA",
            totalAmount = 50000000.0,
            paidAmount = 15000000.0,
            description = "KTA untuk renovasi rumah",
            type = DebtReceivableType.DEBT,
            dueDate = Calendar.getInstance().apply { add(Calendar.MONTH, 2) }.time,
            createdDate = Date(),
            payments = listOf(
                Payment("p1", 5000000.0, Calendar.getInstance().apply { add(Calendar.MONTH, -2) }.time),
                Payment("p2", 5000000.0, Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time),
                Payment("p3", 5000000.0, Date())
            )
        ),
        DebtReceivable(
            id = "2",
            name = "Hutang ke Budi",
            totalAmount = 2000000.0,
            paidAmount = 500000.0,
            description = "Pinjaman darurat",
            type = DebtReceivableType.DEBT,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 3) }.time,
            createdDate = Date()
        ),
        DebtReceivable(
            id = "3",
            name = "Piutang dari Sari",
            totalAmount = 3000000.0,
            paidAmount = 1000000.0,
            description = "Pinjaman untuk modal usaha",
            type = DebtReceivableType.RECEIVABLE,
            dueDate = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }.time,
            createdDate = Date()
        ),
        DebtReceivable(
            id = "4",
            name = "Piutang Freelance",
            totalAmount = 5000000.0,
            paidAmount = 0.0,
            description = "Project website company ABC",
            type = DebtReceivableType.RECEIVABLE,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
            createdDate = Date()
        ),
        DebtReceivable(
            id = "5",
            name = "Hutang Kartu Kredit",
            totalAmount = 8000000.0,
            paidAmount = 3000000.0,
            description = "Cicilan kartu kredit BNI",
            type = DebtReceivableType.DEBT,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 15) }.time,
            createdDate = Date()
        ),
        DebtReceivable(
            id = "6",
            name = "Piutang Toko Online",
            totalAmount = 1500000.0,
            paidAmount = 1500000.0,
            description = "Pembayaran produk elektronik",
            type = DebtReceivableType.RECEIVABLE,
            dueDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -10) }.time,
            createdDate = Date()
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DebtReceivableMainScreenPreview() {
    DebtReceivableMainScreen()
}

@Preview(showBackground = true)
@Composable
fun SummaryCardPreview() {
    SummaryCard(
        title = "Total Hutang",
        amount = 50000000.0,
        color = DebtReceivableType.DEBT.color,
        icon = "💸"
    )
}

@Preview(showBackground = true)
@Composable
fun DebtReceivableItemCardPreview() {
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

    DebtReceivableItemCard(
        item = mockItem,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyStateCardPreview() {
    EmptyStateCard(selectedTab = 1)
}
