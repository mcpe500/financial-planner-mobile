// <!-- filepath: app/src/main/java/com/example/financialplannerapp/ui/screens/settings/HelpAndFaqScreen.kt -->
package com.example.financialplannerapp.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.example.financialplannerapp.ui.viewmodels.settings.HelpFaqUiState
import com.example.financialplannerapp.ui.viewmodels.settings.HelpFaqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndFaqScreen(
    navController: NavController,
    viewModel: HelpFaqViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateSyncStatus() // Initial sync status check
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pusat Bantuan & FAQ") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            SyncStatusSection(uiState, onUpdateClick = { viewModel.updateContentFromServer() })
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar(uiState.searchQuery, onQueryChange = { viewModel.onSearchQueryChanged(it) })
            Spacer(modifier = Modifier.height(16.dp))
            ContentTabs(uiState.selectedTab, onTabSelected = { viewModel.onTabSelected(it) })
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading && (uiState.filteredFaqs.isEmpty() && uiState.filteredHelpContents.isEmpty())) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (uiState.selectedTab) {
                    0 -> FaqList(uiState.filteredFaqs)
                    1 -> GuideList(uiState.filteredHelpContents)
                    2 -> ContactContent()
                }
            }
             if (uiState.isOnline) {
                OfflineIndicator(isOnline = uiState.isOnline)
            }
        }
    }
}

@Composable
fun SyncStatusSection(uiState: HelpFaqUiState, onUpdateClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Sync, contentDescription = "Sync Status", tint = Color(uiState.syncStatusColor))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(uiState.syncStatusText, fontWeight = FontWeight.Bold, color = Color(uiState.syncStatusColor))
            Text(uiState.lastUpdateText, style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = onUpdateClick, enabled = uiState.isOnline) {
            Text("Update")
        }
    }
}
@Composable
fun OfflineIndicator(isOnline: Boolean) {
    if (!isOnline) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                "Mode Offline",
                color = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize(), // Adjust size as needed
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Cari bantuan atau FAQ...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun ContentTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("FAQ", "Panduan", "Kontak")
    TabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun FaqList(faqs: List<FAQItem>) {
    if (faqs.isEmpty()) {
        EmptyState("Tidak ada FAQ ditemukan")
        return
    }
    LazyColumn {
        items(faqs, key = { it.id }) { faq ->
            FaqItemComposable(faqItem = faq)
        }
    }
}

@Composable
fun GuideList(guides: List<HelpContent>) {
     if (guides.isEmpty()) {
        EmptyState("Tidak ada Panduan ditemukan")
        return
    }
    LazyColumn {
        items(guides, key = { it.id }) { guide ->
            HelpContentItemComposable(helpContent = guide)
        }
    }
}

@Composable
fun FaqItemComposable(faqItem: FAQItem) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = faqItem.question,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (faqItem.isPopular) {
                    Text(
                        "POPULER",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .wrapContentSize() // Adjust as needed
                    )
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = faqItem.answer, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                if (expanded) "Tap untuk menyembunyikan jawaban" else "Tap untuk melihat jawaban",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun HelpContentItemComposable(helpContent: HelpContent) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                 Icon(
                    Icons.Filled.Info,
                    contentDescription = "Guide Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = helpContent.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = helpContent.content, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                if (expanded) "Tap untuk menyembunyikan panduan" else "Tap untuk membaca panduan lengkap",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun ContactContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Hubungi Tim Dukungan", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
        ContactCard(
            icon = Icons.Filled.ContactMail,
            title = "Email Support",
            detail = "support@financialplanner.com"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ContactCard(
            icon = Icons.Filled.ContactMail, // Replace with call icon
            title = "Telepon Support",
            detail = "+62-XXX-XXXX-XXXX"
        )
    }
}

@Composable
fun ContactCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, detail: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(detail, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Search, contentDescription = "Empty", modifier = Modifier.size(64.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        }
    }
}