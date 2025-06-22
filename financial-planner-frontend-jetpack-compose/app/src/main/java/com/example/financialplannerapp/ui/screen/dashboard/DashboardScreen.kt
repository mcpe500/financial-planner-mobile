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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, tokenManager: TokenManager) {
    Log.d(TAG_DASHBOARD_SCREEN, "DashboardScreen composing...")

    val userName = tokenManager.getUserName() ?: "Guest"
    val userEmail = tokenManager.getUserEmail() ?: "No email"
    val isLoggedIn = tokenManager.getToken() != null
    val isNoAccountMode = tokenManager.isNoAccountMode()

    Log.d(TAG_DASHBOARD_SCREEN, "User info - Name: $userName, Email: $userEmail, LoggedIn: $isLoggedIn, NoAccountMode: $isNoAccountMode")

    Scaffold { paddingValues ->
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

        // Quick Actions Grid (First)
        navController?.let { nav ->
            QuickActionsGrid(navController = nav)
        }

        // Financial Overview Card
        FinancialOverviewCard(
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses,
            totalBudget = totalBudget,
            spent = spent,
            remaining = remaining
        )

        // Recent Activity Section
        navController?.let { nav ->
            RecentActivitySection(navController = nav)
        }

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
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary,
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
private fun FinancialOverviewCard(
    monthlyIncome: Double,
    monthlyExpenses: Double,
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
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Financial Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "This month's summary",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Income vs Expenses Chart
            val incomeColor = MaterialTheme.colorScheme.primary
            val expensesColor = MaterialTheme.colorScheme.tertiary
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                drawEnhancedIncomeExpensesChart(
                    monthlyIncome, 
                    monthlyExpenses, 
                    incomeColor, 
                    expensesColor
                )
            }

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EnhancedLegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = "Income",
                    amount = monthlyIncome,
                    isPositive = true
                )
                EnhancedLegendItem(
                    color = MaterialTheme.colorScheme.tertiary,
                    label = "Expenses",
                    amount = monthlyExpenses,
                    isPositive = false
                )
            }

            // Net difference
            val netAmount = monthlyIncome - monthlyExpenses
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
                    color = if (netAmount >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                )
            }

            // Budget Progress
            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Budget Progress",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                val progressPercentage = ((spent / totalBudget) * 100).toInt()
                Text(
                    text = "$progressPercentage% used",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            val progress = (spent / totalBudget).toFloat()
            val progressColor = when {
                progress > 0.9f -> MaterialTheme.colorScheme.error
                progress > 0.8f -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetItem("Total Budget", totalBudget, MaterialTheme.colorScheme.onSurface)
                BudgetItem("Spent", spent, progressColor)
                BudgetItem("Remaining", remaining, MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun BudgetItem(
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = amount.toCurrency(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
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

private fun DrawScope.drawEnhancedIncomeExpensesChart(income: Double, expenses: Double, incomeColor: Color, expensesColor: Color) {
    val maxValue = maxOf(income, expenses)
    val barWidth = size.width * 0.25f
    val spacing = size.width * 0.125f
    val maxHeight = size.height * 0.85f
    val cornerRadius = 8.dp.toPx()

    // Income bar with rounded corners
    val incomeHeight = (income / maxValue * maxHeight).toFloat()
    drawRoundRect(
        color = incomeColor,
        topLeft = Offset(spacing, size.height - incomeHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, incomeHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
    )

    // Expenses bar with rounded corners
    val expensesHeight = (expenses / maxValue * maxHeight).toFloat()
    drawRoundRect(
        color = expensesColor,
        topLeft = Offset(spacing * 3 + barWidth, size.height - expensesHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, expensesHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
    )
}

@Composable
private fun QuickActionsGrid(navController: NavController) {
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
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "Wallet",
                    onClick = { navController.navigate("wallet") }
                )
                QuickActionButton(
                    icon = Icons.Default.SwapVert,
                    label = "Transactions",
                    onClick = { navController.navigate("transactions") }
                )
                QuickActionButton(
                    icon = Icons.Default.Flag,
                    label = "Goals",
                    onClick = { navController.navigate("goals") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.TrackChanges,
                    label = "Budget",
                    onClick = { navController.navigate("budget") }
                )
                QuickActionButton(
                    icon = Icons.Default.Receipt,
                    label = "Bills",
                    onClick = { navController.navigate("bills") }
                )
                QuickActionButton(
                    icon = Icons.Default.TrendingUp,
                    label = "Reports",
                    onClick = { navController.navigate("reports") }
                )
            }
        }
    }
}

@Composable
private fun RowScope.QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .height(90.dp)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun RecentActivitySection(navController: NavController) {
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
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Recent Activity",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Latest transactions",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent transaction items
            RecentTransactionItem("Coffee Shop", "Food & Beverage", -8.50, "2 hours ago")
            Spacer(modifier = Modifier.height(12.dp))
            RecentTransactionItem("Salary", "Income", 2500.00, "Yesterday")
            Spacer(modifier = Modifier.height(12.dp))
            RecentTransactionItem("Gas Station", "Transportation", -45.00, "2 days ago")
        }
    }
}

@Composable
private fun RecentTransactionItem(
    title: String,
    category: String,
    amount: Double,
    timeAgo: String
) {
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
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = category,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = timeAgo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        Text(
            text = if (amount >= 0) "+${amount.toCurrency()}" else amount.toCurrency(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (amount >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
        )
    }
}