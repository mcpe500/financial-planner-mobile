package com.example.financialplannerapp.ui.screen.goal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.TokenManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import com.example.financialplannerapp.core.util.formatCurrency
import com.example.financialplannerapp.core.util.getCurrentCurrencySymbol
import com.example.financialplannerapp.data.model.FinancialGoal
import com.example.financialplannerapp.data.model.generateMockGoals
import com.example.financialplannerapp.ui.viewmodel.GoalViewModel
import com.example.financialplannerapp.ui.viewmodel.GoalViewModelFactory
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory
import com.example.financialplannerapp.ui.viewmodel.toWalletEntity

data class GoalTransaction(
    val id: String,
    val amount: Double,
    val date: Date,
    val type: GoalTransactionType,
    val note: String,
    val sourceWallet: String
)

enum class GoalTransactionType {
    DEPOSIT, WITHDRAWAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsScreen(
    navController: NavController,
    goalId: String?
) {
    val application = LocalContext.current.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(application) }
    val userId = tokenManager.getUserEmail() ?: "guest"
    val goalViewModel: GoalViewModel = viewModel(
        factory = GoalViewModelFactory(application.appContainer.goalRepository, tokenManager, application.appContainer.walletRepository)
    )
    val walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(application.appContainer.walletRepository, tokenManager)
    )
    val goals by goalViewModel.goals.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()

    val goal = goals.find { it.id.toString() == goalId }
    val wallet = wallets.find { it.id == goal?.walletId }

    var showEditDialog by remember { mutableStateOf(false) }
    var addAmount by remember { mutableStateOf("") }
    var subtractAmount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (goal == null || wallet == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Goal not found.")
        }
        return
    }

    val isComplete = goal.currentAmount >= goal.targetAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Detail
            Text("Name: ${goal.name}", style = MaterialTheme.typography.titleLarge)
            Text("Target: ${goal.targetAmount}", style = MaterialTheme.typography.bodyLarge)
            Text("Current: ${goal.currentAmount}", style = MaterialTheme.typography.bodyLarge)
            Text("Priority: ${goal.priority}", style = MaterialTheme.typography.bodyLarge)
            Text("Wallet: ${wallet.name}", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )

            // Edit button
            Button(onClick = { showEditDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Edit Goal")
            }

            // Tambah currentAmount
            OutlinedTextField(
                value = addAmount,
                onValueChange = { addAmount = it },
                label = { Text("Add to Goal (from wallet)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val amount = addAmount.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && wallet.balance >= amount) {
                        goalViewModel.addToGoalAmount(goal, wallet.toWalletEntity(userId), amount)
                        addAmount = ""
                        errorMessage = null
                    } else {
                        errorMessage = "Insufficient wallet balance or invalid amount."
                    }
                },
                enabled = addAmount.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add Amount") }

            // Kurangi currentAmount
            OutlinedTextField(
                value = subtractAmount,
                onValueChange = { subtractAmount = it },
                label = { Text("Subtract from Goal (return to wallet)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val amount = subtractAmount.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && goal.currentAmount >= amount) {
                        goalViewModel.subtractFromGoalAmount(goal, wallet.toWalletEntity(userId), amount)
                        subtractAmount = ""
                        errorMessage = null
                    } else {
                        errorMessage = "Insufficient goal amount or invalid amount."
                    }
                },
                enabled = subtractAmount.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Subtract Amount") }

            // Error message
            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red)
            }

            // Complete button
            Button(
                onClick = { /* TODO: Implement complete logic */ },
                enabled = isComplete,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Complete Goal") }
        }
    }

    // Edit dialog
    if (showEditDialog) {
        EditGoalDialog(
            goal = goal,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newTarget, newPriority ->
                goalViewModel.editGoal(goal, newName, newTarget, newPriority)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun GoalHeaderCard(goal: FinancialGoal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = goal.icon,
                contentDescription = goal.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = goal.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            if (goal.isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Goal Achieved!",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalProgressCard(goal: FinancialGoal) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = goal.progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = if (goal.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${(goal.progressPercentage * 100).toInt()}% Complete",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current Amount",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currencyFormat.format(goal.currentAmount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target Amount",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currencyFormat.format(goal.targetAmount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            if (!goal.isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Remaining: ${currencyFormat.format(goal.remainingAmount)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GoalDetailsCard(goal: FinancialGoal) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Goal Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(
                label = "Category",
                value = goal.category.displayName,
                icon = goal.category.icon
            )
            
            DetailRow(
                label = "Priority",
                value = goal.priority,
                icon = Icons.Default.PriorityHigh
            )
            
            DetailRow(
                label = "Target Date",
                value = dateFormat.format(goal.targetDate),
                icon = Icons.Default.DateRange
            )
            
            if (!goal.isCompleted) {
                DetailRow(
                    label = "Days Remaining",
                    value = "${goal.daysRemaining} days",
                    icon = Icons.Default.Schedule
                )
            }
            
            if (goal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Description",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = goal.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun GoalActionsCard(
    onAddFunds: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAddFunds,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Funds")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Goal")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Goal")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFundsBottomSheet(
    goal: FinancialGoal,
    onDismiss: () -> Unit,
    onAddFunds: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Add Funds to ${goal.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue != null && amountValue > 0) {
                            onAddFunds(amountValue)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0
                ) {
                    Text("Add Funds")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalDetailsScreenPreview() {
    GoalDetailsScreen(rememberNavController(), "1")
}
