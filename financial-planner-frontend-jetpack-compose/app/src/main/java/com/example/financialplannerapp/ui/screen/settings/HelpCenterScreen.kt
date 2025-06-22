package com.example.financialplannerapp.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class FaqItem(
    val title: String,
    val content: String,
    val icon: ImageVector
)

private val faqItems = listOf(
    FaqItem(
        title = "What are Wallets?",
        content = "Wallets represent your different sources of funds, such as a physical cash wallet, a bank account, an e-wallet (like PayPal or GoPay), or an investment account. By separating your funds into different wallets, you can accurately track where your money is coming from and going to.",
        icon = Icons.Default.AccountBalanceWallet
    ),
    FaqItem(
        title = "How do I track transactions?",
        content = "You can add a transaction by tapping the '+' button on the dashboard or within a specific wallet. Each transaction can be categorized as income or expense, assigned to a wallet, and given a specific category (e.g., 'Food', 'Salary'). This helps you see exactly where your money is being spent.",
        icon = Icons.Default.SwapVert
    ),
    FaqItem(
        title = "How do Budgets work?",
        content = "Budgets help you control your spending. You can set a monthly spending limit for a specific category (e.g., '$200 for Groceries'). The app will then show you how much you've spent in that category and how much you have left, helping you stay within your limits.",
        icon = Icons.Default.TrackChanges
    ),
    FaqItem(
        title = "What is the purpose of Financial Goals?",
        content = "Financial Goals allow you to set long-term targets, like saving for a vacation, a new car, or a down payment on a house. You can set a target amount and a target date, and then track your progress by contributing funds towards that goal over time.",
        icon = Icons.Default.Flag
    ),
    FaqItem(
        title = "What is Data Sync?",
        content = "Data Sync automatically keeps your financial data updated across all your devices. If you log a transaction on your phone, it will appear on your tablet automatically. Manual Backup & Restore options are also available for extra security, allowing you to save a snapshot of your data at any time.",
        icon = Icons.Default.Sync
    ),
    FaqItem(
        title = "How do I get started with the app?",
        content = "To get started, first create your wallets to represent your different sources of money. Then, add your initial balance to each wallet. From there, you can start tracking your daily transactions, set up budgets for spending categories, and create financial goals for your long-term savings targets.",
        icon = Icons.Default.PlayArrow
    ),
    FaqItem(
        title = "How do I change the app theme?",
        content = "The app automatically follows your device's theme settings. If your device is set to Light mode, the app will display in Light mode. If your device is set to Dark mode, the app will display in Dark mode. To change the app theme, simply change your device's theme settings in your phone's display settings.",
        icon = Icons.Default.Palette
    ),
    FaqItem(
        title = "How accurate are the spending reports?",
        content = "The spending reports are based on the transactions you manually enter into the app. For the most accurate reports, make sure to log all your transactions promptly and categorize them correctly. The app provides visual charts and analytics to help you understand your spending patterns.",
        icon = Icons.Default.Analytics
    ),
    FaqItem(
        title = "Can I use the app offline?",
        content = "Yes, you can use most features of the app offline. You can add transactions, view your wallets, and manage budgets without an internet connection. However, data sync and backup features require an internet connection to work properly.",
        icon = Icons.Default.WifiOff
    ),
    FaqItem(
        title = "How do I delete my account?",
        content = "To delete your account, go to App Settings and scroll to the bottom. You'll find the 'Delete Account' option. Please note that this action is permanent and will delete all your financial data. Make sure to export your data first if you want to keep a backup.",
        icon = Icons.Default.DeleteForever
    ),
    FaqItem(
        title = "How do I update my profile information?",
        content = "You can update your profile information in User Profile Settings. Here you can change your name, email, phone number, and profile picture. Make sure to save your changes, and the updated information will be synced across all your devices.",
        icon = Icons.Default.Person
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    var expandedCardIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "How can we help you?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Find answers to common questions and learn how to use the app effectively",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                Text(
                    "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp, top = 4.dp, start = 4.dp)
                )
            }

            itemsIndexed(faqItems) { index, faqItem ->
                ExpandableFaqCard(
                    faqItem = faqItem,
                    isExpanded = expandedCardIndex == index,
                    onClick = {
                        expandedCardIndex = if (expandedCardIndex == index) null else index
                    }
                )
            }

            // Contact Support Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ContactSupport,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Still need help?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Contact our support team for personalized assistance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navController.navigate("contactSupportSettings")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Contact Support")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableFaqCard(
    faqItem: FaqItem,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = faqItem.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = faqItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Text(
                        text = faqItem.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}