package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.core.util.formatCurrency
import com.example.financialplannerapp.data.local.AppDatabase
import com.example.financialplannerapp.data.repository.WalletRepositoryImpl
import com.example.financialplannerapp.ui.model.Wallet
import com.example.financialplannerapp.ui.model.WalletType
import com.example.financialplannerapp.ui.model.icon
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory
import com.example.financialplannerapp.MainApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsMainScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val tokenManager = remember { TokenManager(context) }
    
    val userEmail = remember {
        if (tokenManager.isNoAccountMode()) {
            "guest"
        } else {
            tokenManager.getUserEmail() ?: "guest"
        }
    }

    val walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(application.appContainer.walletRepository, tokenManager)
    )

    val allWallets by walletViewModel.wallets.collectAsState()
    val isLoading by walletViewModel.isLoading.collectAsState()
    val error by walletViewModel.error.collectAsState()
    val successMessage by walletViewModel.successMessage.collectAsState()

    var selectedFilter by remember { mutableStateOf(WalletTypeFilter.ALL) }

    val filteredWallets = remember(allWallets, selectedFilter) {
        when (selectedFilter) {
            WalletTypeFilter.ALL -> allWallets
            WalletTypeFilter.CASH -> allWallets.filter { it.type == WalletType.CASH }
            WalletTypeFilter.BANK -> allWallets.filter { it.type == WalletType.BANK }
            WalletTypeFilter.E_WALLET -> allWallets.filter { it.type == WalletType.E_WALLET }
            WalletTypeFilter.INVESTMENT -> allWallets.filter { it.type == WalletType.INVESTMENT }
        }
    }

    val statistics = remember(allWallets) {
        calculateWalletStatistics(allWallets)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            walletViewModel.clearMessages()
        }
    }
    
    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            walletViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Wallets",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        TotalBalanceCard(
                            totalBalance = statistics.totalBalance,
                            walletCount = allWallets.size
                        )
                    }

                    if (allWallets.isNotEmpty()) {
                        item {
                            QuickActionsSection(navController = navController, isEnabled = true)
                        }
                    } else {
                        item {
                            QuickActionsSection(navController = navController, isEnabled = false)
                        }
                    }

                    if (allWallets.isNotEmpty()) {
                        item {
                            FinancialInsightsCard(statistics = statistics)
                        }
                    }

                    if (allWallets.isNotEmpty()) {
                        item {
                            WalletDistributionChart(wallets = allWallets)
                        }
                    }

                    item {
                        WalletTypeFilterChips(
                            selectedFilter = selectedFilter,
                            onFilterChange = { selectedFilter = it }
                        )
                    }

                    items(filteredWallets) { wallet ->
                        WalletCard(
                            wallet = wallet,
                            onClick = { 
                                navController.navigate("wallet_details/${wallet.id}")
                            },
                            onEditClick = { 
                                navController.navigate("edit_wallet/${wallet.id}")
                            }
                        )
                    }

                    if (filteredWallets.isEmpty()) {
                        item {
                            EmptyStateCard(
                                hasWallets = allWallets.isNotEmpty(),
                                selectedFilter = selectedFilter
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

enum class WalletTypeFilter(val label: String, val icon: ImageVector) {
    ALL("All", Icons.Default.AllInclusive),
    CASH("Cash", Icons.Default.Money),
    BANK("Bank", Icons.Default.AccountBalance),
    E_WALLET("E-Wallet", Icons.Default.Smartphone),
    INVESTMENT("Investment", Icons.Default.TrendingUp)
}

data class WalletStatistics(
    val totalBalance: Double,
    val totalAssets: Double,
    val totalDebt: Double,
    val netWorth: Double,
    val walletCount: Int,
    val assetWalletCount: Int,
    val debtWalletCount: Int,
    val typeDistribution: Map<WalletType, Double>
)

fun calculateWalletStatistics(wallets: List<Wallet>): WalletStatistics {
    val totalBalance = wallets.sumOf { it.balance }
    val totalAssets = wallets.sumOf { it.balance }
    val totalDebt = 0.0
    val netWorth = totalAssets
    
    val typeDistribution = wallets.groupBy { it.type }
        .mapValues { (_, walletList) -> walletList.sumOf { it.balance } }
    
    return WalletStatistics(
        totalBalance = totalBalance,
        totalAssets = totalAssets,
        totalDebt = totalDebt,
        netWorth = netWorth,
        walletCount = wallets.size,
        assetWalletCount = wallets.size,
        debtWalletCount = 0,
        typeDistribution = typeDistribution
    )
}

@Composable
private fun TotalBalanceCard(totalBalance: Double, walletCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Total Balance",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Text(
                text = formatCurrency(totalBalance),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Wallets",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$walletCount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(navController: NavController, isEnabled: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Add Wallet",
                    onClick = { navController.navigate("add_wallet") },
                    enabled = true
                )
                
                QuickActionButton(
                    icon = Icons.Default.Receipt,
                    label = "Scan Receipt",
                    onClick = { navController.navigate("scan_receipt") },
                    enabled = isEnabled
                )
                
                QuickActionButton(
                    icon = Icons.Default.Assessment,
                    label = "Reports",
                    onClick = { navController.navigate("financial_reports") },
                    enabled = isEnabled
                )
            }
        }
    }
}

@Composable
private fun RowScope.QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val contentColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val cardModifier = if (enabled) {
        Modifier
            .weight(1f)
            .clickable { onClick() }
    } else {
        Modifier.weight(1f)
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FinancialInsightsCard(statistics: WalletStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Financial Insights",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val assetTypes = statistics.typeDistribution.keys.size
            InsightRow(
                icon = Icons.Default.Diversity3,
                title = "Asset Diversity",
                value = "$assetTypes types",
                description = if (assetTypes >= 3) "Well diversified" else "Consider diversifying",
                isWarning = assetTypes < 3
            )
        }
    }
}

@Composable
private fun InsightRow(
    icon: ImageVector,
    title: String,
    value: String,
    description: String,
    isWarning: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun WalletDistributionChart(wallets: List<Wallet>) {
    if (wallets.isEmpty()) return
    
    val statistics = calculateWalletStatistics(wallets)
    val totalBalance = statistics.totalBalance
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Balance Distribution",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            WalletType.values().forEach { walletType ->
                val typeBalance = statistics.typeDistribution[walletType] ?: 0.0
                if (typeBalance > 0) {
                    val percentage = (typeBalance / totalBalance * 100).toFloat()
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = walletType.icon,
                            contentDescription = walletType.name,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = walletType.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "${String.format("%.1f", percentage)}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = percentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletTypeFilterChips(
    selectedFilter: WalletTypeFilter,
    onFilterChange: (WalletTypeFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(WalletTypeFilter.values()) { filter ->
            FilterChip(
                onClick = { onFilterChange(filter) },
                label = { Text(filter.label) },
                selected = selectedFilter == filter,
                leadingIcon = {
                    Icon(
                        imageVector = filter.icon,
                        contentDescription = filter.label,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun WalletCard(
    wallet: Wallet,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(wallet.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = wallet.icon,
                    contentDescription = wallet.name,
                    tint = wallet.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = wallet.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = wallet.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formatCurrency(wallet.balance),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(hasWallets: Boolean, selectedFilter: WalletTypeFilter) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (hasWallets) Icons.Default.FilterList else Icons.Default.AccountBalance,
                contentDescription = "No Wallets",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hasWallets) "No ${selectedFilter.label} Wallets" else "No Wallets Yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (hasWallets) 
                    "You don't have any ${selectedFilter.label.lowercase()} wallets yet" 
                else 
                    "Create your first wallet to start managing your finances",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletsMainScreenPreview() {
    WalletsMainScreen(rememberNavController())
}