package com.example.financialplannerapp.ui.screen.goal

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
import com.example.financialplannerapp.data.local.model.GoalEntity
import com.example.financialplannerapp.ui.theme.Green
import com.example.financialplannerapp.ui.viewmodel.GoalViewModel
import com.example.financialplannerapp.ui.viewmodel.GoalViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialGoalsMainScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(context) }
    
    val goalViewModel: GoalViewModel = viewModel(
        factory = GoalViewModelFactory(application.appContainer.goalRepository, tokenManager)
    )

    val goals by goalViewModel.goals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Goals") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_goal") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Goal")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (goals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No goals created yet.")
                    }
                }
            } else {
                items(goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = {
                            navController.navigate("goal_details/${goal.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: com.example.financialplannerapp.data.local.model.GoalEntity,
    onClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val remainingAmount = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(goal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Priority: ${goal.priority}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Green,
                trackColor = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Current: ${currencyFormat.format(goal.currentAmount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Target: ${currencyFormat.format(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                "Remaining: ${currencyFormat.format(remainingAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
