package com.example.financialplannerapp.ui.screen.goal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.GoalEntity
import com.example.financialplannerapp.ui.theme.Green
import com.example.financialplannerapp.ui.viewmodel.GoalViewModel
import com.example.financialplannerapp.ui.viewmodel.GoalViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialGoalsMainScreen(navController: NavController) {
    val application = LocalContext.current.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(application) }

    // For now, we assume goals are not tied to a specific wallet on this screen
    // or we are showing goals from all wallets. This can be refined later.
    val goalViewModel: GoalViewModel = viewModel(
        factory = GoalViewModelFactory(application.appContainer.goalRepository, tokenManager)
    )

    // TODO: Implement wallet selection to filter goals.
    // For now, we load goals for a hardcoded or first-available wallet if needed,
    // or the repository could be updated to fetch all goals for the user.
    // Let's assume for now the repository and DAO need a query to fetch all goals by userEmail.
    // I will add this later. For now, let's focus on UI with dummy/all data.

    val goals by goalViewModel.goals.collectAsState()

    // You would call loadGoals here, e.g., goalViewModel.loadGoals(someWalletId)
    // or a new method like goalViewModel.loadAllUserGoals()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Financial Goals") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_goal") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (goals.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No goals yet. Tap the '+' button to add one!")
                    }
                }
            } else {
                items(goals) { goal ->
                    GoalItemCard(goal = goal, onGoalClick = {
                        // Navigate to detail screen
                        navController.navigate("goal_details/${goal.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun GoalItemCard(goal: GoalEntity, onGoalClick: (Int) -> Unit) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val isAchieved = progress >= 1.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onGoalClick(goal.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(goal.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (isAchieved) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Achieved",
                        tint = Green
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${currencyFormat.format(goal.currentAmount)} / ${currencyFormat.format(goal.targetAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = if (isAchieved) Green else MaterialTheme.colorScheme.primary
            )
        }
    }
}
