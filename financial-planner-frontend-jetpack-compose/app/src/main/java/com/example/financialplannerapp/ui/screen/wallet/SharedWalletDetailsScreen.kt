package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // For ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.data.local.AppDatabase // Import your database
import com.example.financialplannerapp.data.repository.WalletRepositoryImpl // Import your repository implementation

// --- IMPORTANT IMPORTS FROM YOUR CENTRALIZED UI MODEL FILE ---
import com.example.financialplannerapp.ui.model.Wallet // Import the Wallet UI data class
import com.example.financialplannerapp.ui.model.WalletType // Import WalletType enum
import com.example.financialplannerapp.ui.model.icon // Import WalletType.icon extension for WalletInfoCard
import com.example.financialplannerapp.ui.viewmodel.WalletViewModel
import com.example.financialplannerapp.ui.viewmodel.WalletViewModelFactory

// --- END IMPORTANT IMPORTS ---



// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

// WalletMember and MemberPermission can remain here if they are only used within this screen
// or move them to a 'ui.model.sharedwallet' package if you anticipate reuse.
data class WalletMember(
    val id: String,
    val name: String,
    val email: String,
    val permission: MemberPermission,
    val isOwner: Boolean = false,
    val avatarColor: Color
)

enum class MemberPermission {
    OWNER, EDIT, VIEW_ONLY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWalletDetailsScreen(
    navController: NavController,
    walletId: String,
    // Assume userId might be needed for permission checks or future actions
    userId: String = "user123", // Passed from navigation or session
    walletViewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            walletRepository = WalletRepositoryImpl(AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current).walletDao()),
            userId = userId // Pass the userId to the ViewModel
        )
    )
) {
    var showInviteDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf<WalletMember?>(null) }

    // Observe all wallets, then find the specific one by walletId
    val allWallets by walletViewModel.wallets.collectAsState()
    val isLoading by walletViewModel.isLoading.collectAsState()
    val error by walletViewModel.error.collectAsState()

    // Find the specific wallet for this screen
    val wallet = remember(walletId, allWallets) {
        allWallets.find { it.id == walletId }
    }

    // Mock members (since member management isn't in Room DB yet)
    val members = remember { generateMockMembers() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        wallet?.name ?: "Loading...", // Display wallet name if available
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showInviteDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Invite Member")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error", color = Color.Red)
            }
        } else if (wallet == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Wallet not found!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftGray)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wallet Info Card
                item {
                    WalletInfoCard(wallet)
                }

                // Members Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Members (${members.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray
                        )

                        TextButton(
                            onClick = { showInviteDialog = true }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Invite")
                        }
                    }
                }

                // Members List
                items(members) { member ->
                    MemberCard(
                        member = member,
                        onPermissionClick = { showPermissionDialog = member },
                        onRemoveClick = { /* Handle remove */ } // TODO: Implement remove member logic
                    )
                }
            }
        }
    }

    // Invite Dialog
    if (showInviteDialog) {
        InviteMemberDialog(
            onDismiss = { showInviteDialog = false },
            onInvite = { email, permission ->
                // TODO: Implement actual invite logic (e.g., send invite to backend)
                println("Inviting $email with $permission permission")
                showInviteDialog = false
            }
        )
    }

    // Permission Dialog
    showPermissionDialog?.let { member ->
        PermissionDialog(
            member = member,
            onDismiss = { showPermissionDialog = null },
            onPermissionChange = { newPermission ->
                // TODO: Implement actual permission change logic (e.g., update member in backend/DB)
                println("Changing ${member.name}'s permission to $newPermission")
                showPermissionDialog = null
            }
        )
    }
}

@Composable
private fun WalletInfoCard(wallet: Wallet) { // Wallet type is now from ui.model
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = wallet.color)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        wallet.icon, // Uses the extension property from ui.model.WalletType
                        contentDescription = wallet.name,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = wallet.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Shared ${wallet.type.name.lowercase()}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Current Balance",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "$${String.format("%.2f", wallet.balance)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun MemberCard(
    member: WalletMember,
    onPermissionClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(member.avatarColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.first().toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = member.avatarColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Member Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = member.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                    if (member.isOwner) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Owner",
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp),
                            tint = Color(0xFFFFD700)
                        )
                    }
                }

                Text(
                    text = member.email,
                    fontSize = 12.sp,
                    color = MediumGray
                )

                Text(
                    text = member.permission.name.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = BibitGreen,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Actions
            if (!member.isOwner) {
                Row {
                    IconButton(onClick = onPermissionClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Change Permission",
                            tint = BibitGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onRemoveClick) {
                        Icon(
                            Icons.Default.PersonRemove,
                            contentDescription = "Remove Member",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InviteMemberDialog(
    onDismiss: () -> Unit,
    onInvite: (String, MemberPermission) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var selectedPermission by remember { mutableStateOf(MemberPermission.VIEW_ONLY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Invite Member",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("Enter email address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    )
                )

                Column {
                    Text(
                        text = "Permission Level",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(MemberPermission.EDIT, MemberPermission.VIEW_ONLY).forEach { permission ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPermission == permission,
                                onClick = { selectedPermission = permission },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BibitGreen
                                )
                            )
                            Text(
                                text = permission.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onInvite(email, selectedPermission) },
                enabled = email.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Send Invite")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PermissionDialog(
    member: WalletMember,
    onDismiss: () -> Unit,
    onPermissionChange: (MemberPermission) -> Unit
) {
    var selectedPermission by remember { mutableStateOf(member.permission) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change Permission",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    text = "Change permission for ${member.name}",
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                listOf(MemberPermission.EDIT, MemberPermission.VIEW_ONLY).forEach { permission ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPermission == permission,
                            onClick = { selectedPermission = permission },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BibitGreen
                            )
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = permission.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (permission == MemberPermission.EDIT)
                                    "Can add, edit, and delete transactions"
                                else
                                    "Can only view transactions and balance",
                                fontSize = 12.sp,
                                color = MediumGray
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPermissionChange(selectedPermission) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun generateMockMembers(): List<WalletMember> {
    return listOf(
        WalletMember(
            id = "1",
            name = "John Doe",
            email = "john@example.com",
            permission = MemberPermission.OWNER,
            isOwner = true,
            avatarColor = BibitGreen
        ),
        WalletMember(
            id = "2",
            name = "Jane Smith",
            email = "jane@example.com",
            permission = MemberPermission.EDIT,
            avatarColor = Color(0xFF2196F3)
        ),
        WalletMember(
            id = "3",
            name = "Mike Johnson",
            email = "mike@example.com",
            permission = MemberPermission.VIEW_ONLY,
            avatarColor = Color(0xFF9C27B0)
        ),
        WalletMember(
            id = "4",
            name = "Sarah Wilson",
            email = "sarah@example.com",
            permission = MemberPermission.EDIT,
            avatarColor = Color(0xFFFF9800)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SharedWalletDetailsScreenPreview() {
    SharedWalletDetailsScreen(rememberNavController(), "1")
}