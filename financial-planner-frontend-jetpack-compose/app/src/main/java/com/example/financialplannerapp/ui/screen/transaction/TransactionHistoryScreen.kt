package com.example.financialplannerapp.ui.screen.transaction

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModel
import com.example.financialplannerapp.ui.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(
            transactionRepository = application.appContainer.transactionRepository
        )
    )
    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transaction History",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (state.transactions.isEmpty()) {
            EmptyTransactionsView()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.transactions) { transaction ->
                    TransactionHistoryItem(transaction)
                }
            }
        }
    }
}

@Composable
private fun EmptyTransactionsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = "No transactions",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No transactions found",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your transaction history will appear here",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TransactionHistoryItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type.equals("INCOME", true)) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = transaction.type,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.merchantName ?: transaction.note ?: "Transaction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = transaction.category,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(transaction.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (transaction.type.equals("INCOME", true)) "+${transaction.amount}" else "-${transaction.amount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type.equals("INCOME", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                
                if (transaction.note != null && transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
} 