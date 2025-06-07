package com.example.financialplannerapp.ui.screen.dashboard

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.core.util.formatCurrency
import com.example.financialplannerapp.core.util.toCurrency
import java.text.SimpleDateFormat
import java.util.*

private const val TAG_DASHBOARD_SCREEN = "DashboardScreen"

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val BibitDarkGreen = Color(0xFF388E3C)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, tokenManager: TokenManager) {
    Log.d(TAG_DASHBOARD_SCREEN, "DashboardScreen composing...")

    val userName = tokenManager.getUserName() ?: "Guest"
    val userEmail = tokenManager.getUserEmail() ?: "No email"
    val isLoggedIn = tokenManager.getToken() != null
    val isNoAccountMode = tokenManager.isNoAccountMode()

    Log.d(TAG_DASHBOARD_SCREEN, "User info - Name: $userName, Email: $userEmail, LoggedIn: $isLoggedIn, NoAccountMode: $isNoAccountMode")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d(TAG_DASHBOARD_SCREEN, "Add transaction FAB clicked, navigating to transactions")
                    navController.navigate("transactions")
                },
                containerColor = BibitGreen,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Transactions",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        DashboardContent(
            userName = userName,
            userEmail = userEmail,
            isLoggedIn = isLoggedIn,
            isNoAccountMode = isNoAccountMode,
            navController = navController,
            onLogout = {
                tokenManager.clear()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onSettingsClick = {
                Log.d(TAG_DASHBOARD_SCREEN, "Settings icon clicked, navigating to settings")
                navController.navigate("settings")
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun DashboardContent(
    userName: String = "Guest",
    userEmail: String = "No email",
    isLoggedIn: Boolean = false,
    isNoAccountMode: Boolean = false,
    navController: NavController? = null,
    onLogout: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Mock data
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())
    }
    val monthlyIncome = 5000.0
    val monthlyExpenses = 3200.0
    val totalBudget = 4500.0
    val spent = 3200.0
    val remaining = totalBudget - spent

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Section
        HeaderSection(
            userName = userName,
            currentDate = currentDate,
            isNoAccountMode = isNoAccountMode,
            onSettingsClick = onSettingsClick
        )

        // Income vs Expenses Chart
        IncomeExpensesChart(
            income = monthlyIncome,
            expenses = monthlyExpenses
        )

        // Budget Summary Card
        BudgetSummaryCard(
            totalBudget = totalBudget,
            spent = spent,
            remaining = remaining
        )

        // Account Balance Card (Enhanced)
        AccountBalanceCard()

        // Simple Transaction Summary
        navController?.let { nav ->
            TransactionSummaryCard(navController = nav)
        }

        // Upcoming Bills Reminder
        UpcomingBillsCard()

        // Bottom spacing for FAB
        Spacer(modifier = Modifier.height(100.dp))

        // Bottom spacing for FAB
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun HeaderSection(
    userName: String,
    currentDate: String,
    isNoAccountMode: Boolean,
    onSettingsClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Good ${getGreeting()}, ${if (isNoAccountMode) "Guest" else userName} 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Icon container with proper spacing
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { /* TODO: Handle notifications */ },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = BibitGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = BibitGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = BibitGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = BibitGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun getGreeting(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "morning"
        in 12..16 -> "afternoon"
        else -> "evening"
    }
}

@Composable
private fun IncomeExpensesChart(
    income: Double,
    expenses: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = BibitGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = BibitGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "This Month Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Income vs Expenses",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Enhanced bar chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                drawEnhancedIncomeExpensesChart(income, expenses)
            }

            // Enhanced Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EnhancedLegendItem(
                    color = BibitGreen,
                    label = "Income",
                    amount = income,
                    isPositive = true
                )
                EnhancedLegendItem(
                    color = Color(0xFFFF7043),
                    label = "Expenses",
                    amount = expenses,
                    isPositive = false
                )
            }
            
            // Net difference
            val netAmount = income - expenses
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Net: ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = if (netAmount >= 0) "+${netAmount.toCurrency()}" else netAmount.toCurrency(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (netAmount >= 0) BibitGreen else Color(0xFFFF7043)
                )
            }
        }
    }
}

@Composable
private fun EnhancedLegendItem(
    color: Color,
    label: String,
    amount: Double,
    isPositive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = if (isPositive) "+${amount.toCurrency()}" else amount.toCurrency(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

private fun DrawScope.drawEnhancedIncomeExpensesChart(income: Double, expenses: Double) {
    val maxValue = maxOf(income, expenses)
    val barWidth = size.width * 0.25f
    val spacing = size.width * 0.125f
    val maxHeight = size.height * 0.85f
    val cornerRadius = 8.dp.toPx()

    // Income bar with rounded corners
    val incomeHeight = (income / maxValue * maxHeight).toFloat()
    drawRoundRect(
        color = BibitGreen,
        topLeft = Offset(spacing, size.height - incomeHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, incomeHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
    )

    // Expenses bar with rounded corners
    val expensesHeight = (expenses / maxValue * maxHeight).toFloat()
    drawRoundRect(
        color = Color(0xFFFF7043),
        topLeft = Offset(spacing * 3 + barWidth, size.height - expensesHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, expensesHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
    )
}

@Composable
private fun BudgetSummaryCard(
    totalBudget: Double,
    spent: Double,
    remaining: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = BibitGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = BibitGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Budget Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val progressPercentage = ((spent / totalBudget) * 100).toInt()
                    Text(
                        text = "$progressPercentage% of budget used",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Enhanced progress indicator
            val progress = (spent / totalBudget).toFloat()
            val progressColor = when {
                progress > 0.9f -> Color(0xFFD32F2F) // Red for over 90%
                progress > 0.8f -> Color(0xFFFF7043) // Orange for over 80%
                else -> BibitGreen // Green for under 80%
            }
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EnhancedBudgetItem("Total", totalBudget, MaterialTheme.colorScheme.onSurface)
                EnhancedBudgetItem("Spent", spent, progressColor)
                EnhancedBudgetItem("Remaining", remaining, BibitGreen)
            }
        }
    }
}

@Composable
private fun EnhancedBudgetItem(
    label: String,
    amount: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = amount.toCurrency(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun AccountBalanceCard() {
    val totalBalance = 12847.56
    val percentageChange = 2.5f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BibitGreen
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background pattern (optional decorative element)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Add subtle geometric patterns or gradients here if desired
            }
            
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Total Balance",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = totalBalance.toCurrency(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+${percentageChange}% from last month",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(start = 6.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingBillsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFFF7043).copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color(0xFFFF7043),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Upcoming Bills",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val totalUpcoming = 155.0
                    Text(
                        text = "${totalUpcoming.toCurrency()} total due",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Mock bill items with enhanced design
            EnhancedBillItem("Electricity Bill", "Due May 30", "$85.00", 2)
            Spacer(modifier = Modifier.height(12.dp))
            EnhancedBillItem("Internet", "Due Jun 2", "$45.00", 5)
            Spacer(modifier = Modifier.height(12.dp))
            EnhancedBillItem("Phone", "Due Jun 5", "$25.00", 8)
        }
    }
}

@Composable
private fun EnhancedBillItem(
    title: String,
    dueDate: String,
    amount: String,
    daysUntilDue: Int
) {
    val urgencyColor = when {
        daysUntilDue <= 3 -> Color(0xFFD32F2F) // Red for urgent
        daysUntilDue <= 7 -> Color(0xFFFF7043) // Orange for soon
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Normal
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(urgencyColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dueDate,
                    fontSize = 13.sp,
                    color = urgencyColor
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amount,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$daysUntilDue days",
                fontSize = 12.sp,
                color = urgencyColor
            )
        }
    }
}

@Composable
private fun UserInfoCard(
    userName: String,
    userEmail: String,
    isLoggedIn: Boolean,
    isNoAccountMode: Boolean
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
                text = if (isNoAccountMode) "Guest Mode" else "Account Info",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )
            if (isLoggedIn && !isNoAccountMode) {
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (isNoAccountMode) {
                Text(
                    text = "Using app without account",
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TransactionSummaryCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable { navController.navigate("transactions") },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
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
                            .size(40.dp)
                            .background(
                                color = BibitGreen.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            tint = BibitGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Transactions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Manage your money",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = BibitGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simple transaction summary - just overview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SimpleSummaryItem("Status", "Active")
                SimpleSummaryItem("Quick Access", "Tap to manage")
            }
        }
    }
}

@Composable
private fun SimpleSummaryItem(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardContent(
        userName = "Alex",
        userEmail = "alex@example.com",
        isLoggedIn = true,
        isNoAccountMode = false
    )
}
