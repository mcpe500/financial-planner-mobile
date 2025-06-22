package com.example.financialplannerapp.ui.screen.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.GoalEntity
import com.example.financialplannerapp.ui.viewmodel.GoalViewModel
import com.example.financialplannerapp.ui.viewmodel.GoalViewModelFactory
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.ui.theme.BibitGreen
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory
import com.example.financialplannerapp.ui.viewmodel.toWalletEntity
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialGoalsMainScreen(navController: NavController) {
    val application = LocalContext.current.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(application) }
    val userId = tokenManager.getUserEmail() ?: "guest"
    val goalViewModel: GoalViewModel = viewModel(
        factory = GoalViewModelFactory(application.appContainer.goalRepository, tokenManager, application.appContainer.walletRepository)
    )
    val walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(application.appContainer.walletRepository, tokenManager)
    )
    val wallets by walletViewModel.wallets.collectAsState()
    val goals by goalViewModel.goals.collectAsState()

    var goalToEdit by remember { mutableStateOf<GoalEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<GoalEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val walletId = wallets.firstOrNull()?.id ?: "wallet1" // TODO: Ganti dengan walletId aktif dari selector jika ada
    LaunchedEffect(walletId) {
        goalViewModel.loadGoals(walletId)
    }

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
                    GoalItemCard(
                        goal = goal,
                        onGoalClick = { navController.navigate("goal_details/${goal.id}") },
                        onEditClick = {
                            goalToEdit = goal
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            goalToDelete = goal
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showEditDialog && goalToEdit != null) {
        EditGoalDialog(
            goal = goalToEdit!!,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newTarget, newPriority ->
                goalViewModel.editGoal(goalToEdit!!, newName, newTarget, newPriority)
                showEditDialog = false
            }
        )
    }
    if (showDeleteDialog && goalToDelete != null) {
        val wallet = wallets.find { it.id == goalToDelete!!.walletId }
        if (wallet != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Goal") },
                text = { Text("Are you sure you want to delete this goal? Current amount will be returned to the wallet.") },
                confirmButton = {
                    Button(onClick = {
                        goalViewModel.deleteGoalAndReturnToWallet(goalToDelete!!, wallet.toWalletEntity(userId))
                        showDeleteDialog = false
                    }) { Text("Delete") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun GoalItemCard(
    goal: GoalEntity,
    onGoalClick: (Int) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
                if (isAchieved) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Achieved",
                        tint = BibitGreen
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
                color = if (isAchieved) BibitGreen else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalDialog(
    goal: GoalEntity,
    onDismiss: () -> Unit,
    onSave: (String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf(goal.name) }
    var target by remember { mutableStateOf(goal.targetAmount.toString()) }
    var priority by remember { mutableStateOf(goal.priority) }
    var isPriorityDropdownExpanded by remember { mutableStateOf(false) }
    val priorities = listOf("High", "Medium", "Low")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                ExposedDropdownMenuBox(
                    expanded = isPriorityDropdownExpanded,
                    onExpandedChange = { isPriorityDropdownExpanded = !isPriorityDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority", style = MaterialTheme.typography.bodyLarge) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPriorityDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    ExposedDropdownMenu(
                        expanded = isPriorityDropdownExpanded,
                        onDismissRequest = { isPriorityDropdownExpanded = false }
                    ) {
                        priorities.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    priority = selection
                                    isPriorityDropdownExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val targetValue = target.toDoubleOrNull() ?: goal.targetAmount
                onSave(name, targetValue, priority)
            }) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}