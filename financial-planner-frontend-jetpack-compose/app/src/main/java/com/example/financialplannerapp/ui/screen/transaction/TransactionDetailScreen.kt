package com.example.financialplannerapp.ui.screen.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.data.local.model.TransactionEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(navController: NavController, transactionId: Long?) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val transactionRepository = application.appContainer.transactionRepository
    var transaction by remember { mutableStateOf<TransactionEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            coroutineScope.launch {
                transaction = transactionRepository.getTransactionById(transactionId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction Detail",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            if (transaction == null) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = transaction?.merchantName ?: transaction?.note ?: "Transaction",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Amount: ${transaction?.amount}",
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Type: ${transaction?.type}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Category: ${transaction?.category}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Pocket: ${transaction?.pocket}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Date: ${transaction?.date?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "-"}",
                            fontSize = 16.sp
                        )
                        if (!transaction?.note.isNullOrBlank()) {
                            Text(
                                text = "Note: ${transaction?.note}",
                                fontSize = 16.sp
                            )
                        }
                        // Show receipt items if present
                        if (!transaction?.receipt_items.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Receipt Items",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                transaction?.receipt_items?.forEach { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(item.name, fontWeight = FontWeight.SemiBold)
                                                Text("Category: ${item.category}", fontSize = 12.sp)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Qty: ${item.quantity}", fontSize = 12.sp)
                                                Text("Price: ${item.price}", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 