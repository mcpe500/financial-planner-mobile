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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // For ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// --- IMPORTANT IMPORTS FROM YOUR CENTRALIZED UI MODEL FILE ---
import com.example.financialplannerapp.ui.model.Wallet // Import the Wallet UI data class
import com.example.financialplannerapp.ui.model.WalletType // Import the WalletType enum
import com.example.financialplannerapp.ui.model.icon // Import the WalletType.icon extension property
// --- END IMPORTANT IMPORTS ---

import com.example.financialplannerapp.data.local.AppDatabase // Import your database
import com.example.financialplannerapp.data.repository.WalletRepositoryImpl // Import your repository implementation
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

// --- REMOVED DUPLICATE: Wallet data class, WalletType enum, and WalletType.icon extension property ---
// These are now defined ONLY in 'com.example.financialplannerapp.ui.model.WalletUiModels.kt'

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsMainScreen(
    navController: NavController,
    // IMPORTANT: userId should be passed from the navigation arguments or a session manager.
    // For Preview and initial setup, a default is provided.
    userId: String = "user123", // Replace with actual user ID from authentication or navigation
    // Inject ViewModel using viewModel() Hilt or manual factory
    walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            walletRepository = WalletRepositoryImpl(AppDatabase.getDatabase(LocalContext.current).walletDao()),
            userId = userId // Pass the userId to the ViewModel
        )
    )
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All Wallets", "Shared", "Invitations")

    // Observe wallets from ViewModel
    val allWallets by walletViewModel.wallets.collectAsState()
    val isLoading by walletViewModel.isLoading.collectAsState()
    val error by walletViewModel.error.collectAsState()

    // Filter for shared wallets from the fetched data
    val sharedWallets = allWallets.filter { it.isShared }

    // Mock data for invitations (these aren't handled by Room DB in this setup)
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
                    // Navigate to AddWalletScreen, passing current userId as argument
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

            // Display loading, error, or content
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
            } else {
                // Tab Content
                when (selectedTabIndex) {
                    0 -> AllWalletsTab(
                        wallets = allWallets, // Use data from ViewModel
                        onWalletClick = { wallet ->
                            // Handle wallet click, e.g., navigate to details
                            navController.navigate("wallet_details/${wallet.id}")
                        },
                        onEditClick = { wallet ->
                            // Handle edit wallet (assuming you have an EditWalletScreen)
                            navController.navigate("edit_wallet/${wallet.id}/$userId") // Pass userId for edit
                        },
                        onDeleteClick = { wallet ->
                            walletViewModel.deleteWallet(wallet.id) // Call ViewModel to delete
                        }
                    )
                    1 -> SharedWalletsTab(
                        sharedWallets = sharedWallets, // Use filtered data from ViewModel
                        onWalletClick = { wallet ->
                            // Handle shared wallet click
                            navController.navigate("shared_wallet_details/${wallet.id}")
                        }
                    )
                    2 -> InvitationsTab(
                        invitations = pendingInvitations,
                        onAcceptInvitation = { invitation ->
                            // Handle accept
                            // TODO: Implement logic to update DB and remove invitation
                        },
                        onDeclineInvitation = { invitation ->
                            // Handle decline
                            // TODO: Implement logic to remove invitation
                        }
                    )
                }
            }
        }
    }
}

// --- Reusable Composable Functions (No Changes Needed Here as they use Wallet UI model) ---
// WalletCard, SharedWalletCard, InvitationCard, EmptyStateCard all remain the same.
// The `Wallet` type here refers to `com.example.financialplannerapp.ui.model.Wallet`
// and `WalletType` refers to `com.example.financialplannerapp.ui.model.WalletType`

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
                text = "Across 0 wallets", // Correctly access allWallets count
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

// Mock data classes for invitations (these are not part of your Room DB yet)
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

// --- REMOVED MOCK WALLETS GENERATION ---
// You no longer need `generateMockWallets()` as the data comes from the ViewModel/database.

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
    WalletsMainScreen(rememberNavController(), userId = "preview_user_id_123")
}