package com.example.financialplannerapp.ui.screen.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.BudgetEntity
import com.example.financialplannerapp.ui.theme.DangerRed
import com.example.financialplannerapp.ui.theme.Green
import com.example.financialplannerapp.ui.theme.WarningOrange
import com.example.financialplannerapp.ui.viewmodel.BudgetViewModel
import com.example.financialplannerapp.ui.viewmodel.BudgetViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetMainScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(context) }
    
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(application.appContainer.budgetRepository, tokenManager)
    )

    // TODO: Implement wallet selection to filter budgets.
    // For now, loading all budgets. This needs a new DAO method.
    // budgetViewModel.loadAllUserBudgets()
    val budgets by budgetViewModel.budgets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_budget") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Budget")
            }
        }
    ) { paddingValues ->
        ManageBudgetTab(
            navController = navController,
            budgets = budgets,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ManageBudgetTab(
    navController: NavController,
    budgets: List<BudgetEntity>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (budgets.isEmpty()){
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center){
                    Text("No budgets created yet.")
                }
            }
        } else {
            items(budgets, key = { it.id }) { budget ->
                BudgetProgressCard(
                    budget = budget,
                    // Placeholder for actual spending
                    spentAmount = 0.0,
                    onClick = {
                        // TODO: Navigate to budget details
                        // navController.navigate("budget_details/${budget.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun BudgetProgressCard(
    budget: BudgetEntity,
    spentAmount: Double,
    onClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val progress = (spentAmount / budget.amount).toFloat().coerceIn(0f, 1f)
    val remainingAmount = (budget.amount - spentAmount).coerceAtLeast(0.0)
    
    val progressColor = when {
        progress > 1.0f -> DangerRed
        progress > 0.8f -> WarningOrange
        else -> Green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(budget.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(budget.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = progressColor,
                trackColor = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Spent: ${currencyFormat.format(spentAmount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Limit: ${currencyFormat.format(budget.amount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                "Remaining: ${currencyFormat.format(remainingAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (remainingAmount > 0) Color.DarkGray else DangerRed,
                fontWeight = if (remainingAmount <= 0) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
