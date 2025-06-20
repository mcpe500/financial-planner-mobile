package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

data class Wallet(
    val id: String,
    val name: String,
    val type: WalletType,
    val balance: Double,
    val icon: ImageVector,
    val color: Color,
    val isShared: Boolean = false,
    val memberCount: Int = 1
)

enum class WalletType {
    CASH, BANK, E_WALLET, INVESTMENT, DEBT
}

// Extension property to get icon for WalletType
val WalletType.icon: ImageVector
    get() = when (this) {
        WalletType.CASH -> Icons.Default.Money
        WalletType.BANK -> Icons.Default.AccountBalance
        WalletType.E_WALLET -> Icons.Default.Smartphone // Changed to Smartphone for e-wallet
        WalletType.INVESTMENT -> Icons.Default.TrendingUp
        WalletType.DEBT -> Icons.Default.CreditCard
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsMainScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All Wallets", "Shared", "Invitations")

    // Mock data
    val allWallets = remember { generateMockWallets() }
    val sharedWallets = remember { allWallets.filter { it.isShared } }
    val pendingInvitations = remember { generateMockInvitations() }

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
                actions = {
                    IconButton(onClick = { /* TODO: Handle transfer */ }) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Transfer")
                    }
                    IconButton(onClick = { /* TODO: Handle reconcile */ }) {
                        Icon(Icons.Default.Balance, contentDescription = "Reconcile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Ini yang akan mengarahkan ke AddWalletScreen
                    navController.navigate("add_wallet")
                },
                containerColor = BibitGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Wallet")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = BibitGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = BibitGreen
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> AllWalletsTab(
                    wallets = allWallets,
                    onWalletClick = { wallet ->
                        // Handle wallet click, e.g., navigate to details
                        // navController.navigate("wallet_details/${wallet.id}")
                    },
                    onEditClick = { wallet ->
                        // Handle edit wallet
                        // navController.navigate("edit_wallet/${wallet.id}")
                    },
                    onDeleteClick = { wallet ->
                        // Handle delete wallet
                    }
                )
                1 -> SharedWalletsTab(
                    sharedWallets = sharedWallets,
                    onWalletClick = { wallet ->
                        // Handle shared wallet click
                        // navController.navigate("shared_wallet_details/${wallet.id}")
                    }
                )
                2 -> InvitationsTab(
                    invitations = pendingInvitations,
                    onAcceptInvitation = { invitation ->
                        // Handle accept
                    },
                    onDeclineInvitation = { invitation ->
                        // Handle decline
                    }
                )
            }
        }
    }
}

@Composable
private fun AllWalletsTab(
    wallets: List<Wallet>,
    onWalletClick: (Wallet) -> Unit,
    onEditClick: (Wallet) -> Unit,
    onDeleteClick: (Wallet) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Balance Card
        item {
            TotalBalanceCard(totalBalance = wallets.sumOf { it.balance })
        }

        // Wallet Cards
        items(wallets) { wallet ->
            WalletCard(
                wallet = wallet,
                onClick = { onWalletClick(wallet) },
                onEditClick = { onEditClick(wallet) },
                onDeleteClick = { onDeleteClick(wallet) }
            )
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TotalBalanceCard(totalBalance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BibitGreen)
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
                text = "$${String.format("%.2f", totalBalance)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Across ${generateMockWallets().size} wallets",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun WalletCard(
    wallet: Wallet,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wallet Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(wallet.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    wallet.icon,
                    contentDescription = wallet.name,
                    tint = wallet.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Wallet Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wallet.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (wallet.isShared) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = "Shared",
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp),
                            tint = BibitGreen
                        )
                    }
                }

                Text(
                    text = wallet.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = MediumGray
                )

                Text(
                    text = "$${String.format("%.2f", wallet.balance)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (wallet.type == WalletType.DEBT) Color.Red else BibitGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Action Buttons
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = BibitGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedWalletsTab(
    sharedWallets: List<Wallet>,
    onWalletClick: (Wallet) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sharedWallets) { wallet ->
            SharedWalletCard(
                wallet = wallet,
                onClick = { onWalletClick(wallet) }
            )
        }

        if (sharedWallets.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Group,
                    title = "No Shared Wallets",
                    description = "You don't have any shared wallets yet. Create a wallet and invite family or friends to manage finances together."
                )
            }
        }
    }
}

@Composable
private fun SharedWalletCard(
    wallet: Wallet,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wallet Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(wallet.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    wallet.icon,
                    contentDescription = wallet.name,
                    tint = wallet.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Wallet Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = wallet.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )

                Text(
                    text = "${wallet.memberCount} members",
                    fontSize = 12.sp,
                    color = MediumGray
                )

                Text(
                    text = "$${String.format("%.2f", wallet.balance)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BibitGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = MediumGray
            )
        }
    }
}

data class WalletInvitation(
    val id: String,
    val walletName: String,
    val inviterName: String,
    val inviterEmail: String,
    val type: InvitationType,
    val permission: String
)

enum class InvitationType {
    SENT, RECEIVED
}

@Composable
private fun InvitationsTab(
    invitations: List<WalletInvitation>,
    onAcceptInvitation: (WalletInvitation) -> Unit,
    onDeclineInvitation: (WalletInvitation) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(invitations) { invitation ->
            InvitationCard(
                invitation = invitation,
                onAccept = { onAcceptInvitation(invitation) },
                onDecline = { onDeclineInvitation(invitation) }
            )
        }

        if (invitations.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.MailOutline,
                    title = "No Pending Invitations",
                    description = "You don't have any pending wallet invitations."
                )
            }
        }
    }
}

@Composable
private fun InvitationCard(
    invitation: WalletInvitation,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = invitation.walletName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )

                    Text(
                        text = if (invitation.type == InvitationType.RECEIVED)
                            "Invited by ${invitation.inviterName}"
                        else
                            "Sent to ${invitation.inviterEmail}",
                        fontSize = 12.sp,
                        color = MediumGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Permission: ${invitation.permission}",
                        fontSize = 12.sp,
                        color = BibitGreen,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = invitation.type.name,
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            if (invitation.type == InvitationType.RECEIVED) BibitGreen else MediumGray,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            if (invitation.type == InvitationType.RECEIVED) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Decline")
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BibitGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MediumGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

fun generateMockWallets(): List<Wallet> {
    return listOf(
        Wallet(
            id = "1",
            name = "Cash Wallet",
            type = WalletType.CASH,
            balance = 250.75,
            icon = Icons.Default.Money,
            color = BibitGreen
        ),
        Wallet(
            id = "2",
            name = "BCA Savings",
            type = WalletType.BANK,
            balance = 5420.30,
            icon = Icons.Default.AccountBalance,
            color = Color(0xFF2196F3)
        ),
        Wallet(
            id = "3",
            name = "GoPay",
            type = WalletType.E_WALLET,
            balance = 125.50,
            icon = Icons.Default.Smartphone, // Updated icon
            color = Color(0xFF00BCD4)
        ),
        Wallet(
            id = "4",
            name = "Investment Portfolio",
            type = WalletType.INVESTMENT,
            balance = 12500.00,
            icon = Icons.Default.TrendingUp,
            color = Color(0xFF9C27B0),
            isShared = true,
            memberCount = 2
        ),
        Wallet(
            id = "5",
            name = "Credit Card",
            type = WalletType.DEBT,
            balance = -850.25,
            icon = Icons.Default.CreditCard,
            color = Color(0xFFFF5722)
        ),
        Wallet(
            id = "6",
            name = "Family Budget",
            type = WalletType.BANK,
            balance = 2300.00,
            icon = Icons.Default.Group,
            color = Color(0xFF4CAF50),
            isShared = true,
            memberCount = 4
        )
    )
}

private fun generateMockInvitations(): List<WalletInvitation> {
    return listOf(
        WalletInvitation(
            id = "1",
            walletName = "Family Vacation Fund",
            inviterName = "John Doe",
            inviterEmail = "john@example.com",
            type = InvitationType.RECEIVED,
            permission = "Edit"
        ),
        WalletInvitation(
            id = "2",
            walletName = "Shared Expenses",
            inviterName = "",
            inviterEmail = "sarah@example.com",
            type = InvitationType.SENT,
            permission = "View Only"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WalletsMainScreenPreview() {
    WalletsMainScreen(rememberNavController())
}