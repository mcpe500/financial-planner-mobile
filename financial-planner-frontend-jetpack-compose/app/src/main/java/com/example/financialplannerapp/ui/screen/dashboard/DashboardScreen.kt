package com.example.financialplannerapp.ui.screen.dashboard

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Settings
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
                    // TODO: Navigate to add transaction screen when route is available
                    Log.d(TAG_DASHBOARD_SCREEN, "Add transaction FAB clicked - feature coming soon")
                    // For now, just log the action instead of navigating
                },
                containerColor = BibitGreen,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
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
            onLogout = {
                tokenManager.clear()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onSettingsClick = {
                Log.d(TAG_DASHBOARD_SCREEN, "Settings icon clicked, navigating to settingsScreen")
                navController.navigate("settingsScreen")
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
            .background(SoftGray)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

        // Upcoming Bills Reminder
        UpcomingBillsCard()

        // User Info Card (Only show in specific cases, like debugging or settings)
        // Remove this section entirely since header already shows user info

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BibitGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isLoggedIn || isNoAccountMode) "Logout" else "Back to Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom spacing for FAB
        Spacer(modifier = Modifier.height(80.dp))
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
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hi, ${if (isNoAccountMode) "Guest" else userName} 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Icon container with proper spacing
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { /* TODO: Handle notifications */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = BibitGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = BibitGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
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
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "This Month",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            // Simple bar chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                drawIncomeExpensesChart(income, expenses)
            }

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = BibitGreen,
                    label = "Income",
                    amount = "$${income.toInt()}"
                )
                LegendItem(
                    color = Color(0xFFFF7043),
                    label = "Expenses",
                    amount = "$${expenses.toInt()}"
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
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

private fun DrawScope.drawIncomeExpensesChart(income: Double, expenses: Double) {
    val maxValue = maxOf(income, expenses)
    val barWidth = size.width * 0.3f
    val spacing = size.width * 0.1f
    val maxHeight = size.height * 0.8f

    // Income bar
    val incomeHeight = (income / maxValue * maxHeight).toFloat()
    drawRect(
        color = BibitGreen,
        topLeft = Offset(spacing, size.height - incomeHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, incomeHeight)
    )

    // Expenses bar
    val expensesHeight = (expenses / maxValue * maxHeight).toFloat()
    drawRect(
        color = Color(0xFFFF7043),
        topLeft = Offset(spacing * 2 + barWidth, size.height - expensesHeight),
        size = androidx.compose.ui.geometry.Size(barWidth, expensesHeight)
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
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = BibitGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Budget Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            // Progress indicator
            val progress = (spent / totalBudget).toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progress > 0.8f) Color(0xFFFF7043) else BibitGreen,
                trackColor = SoftGray,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetItem("Total", "$${totalBudget.toInt()}", DarkGray)
                BudgetItem("Spent", "$${spent.toInt()}", Color(0xFFFF7043))
                BudgetItem("Remaining", "$${remaining.toInt()}", BibitGreen)
            }
        }
    }
}

@Composable
private fun BudgetItem(
    label: String,
    amount: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MediumGray
        )
        Text(
            text = amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun AccountBalanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BibitGreen
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Total Balance",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "$12,847.56",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "+2.5% from last month",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun UpcomingBillsCard() {
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
                text = "Upcoming Bills",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Mock bill items
            BillItem("Electricity Bill", "Due May 30", "$85.00")
            Spacer(modifier = Modifier.height(8.dp))
            BillItem("Internet", "Due Jun 2", "$45.00")
            Spacer(modifier = Modifier.height(8.dp))
            BillItem("Phone", "Due Jun 5", "$25.00")
        }
    }
}

@Composable
private fun BillItem(
    title: String,
    dueDate: String,
    amount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )
            Text(
                text = dueDate,
                fontSize = 12.sp,
                color = MediumGray
            )
        }
        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFF7043)
        )
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
