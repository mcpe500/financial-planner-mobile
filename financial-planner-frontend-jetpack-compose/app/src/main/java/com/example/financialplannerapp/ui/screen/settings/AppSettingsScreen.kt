package com.example.financialplannerapp.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.financialplannerapp.core.util.translate
import com.example.financialplannerapp.data.model.Currency
import com.example.financialplannerapp.data.model.LanguageOption
import com.example.financialplannerapp.data.model.ThemeSetting
import com.example.financialplannerapp.data.model.Translations
import com.example.financialplannerapp.service.LocalTranslationProvider
import com.example.financialplannerapp.ui.viewmodel.AppSettingsViewModel
import com.example.financialplannerapp.ui.viewmodel.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    appSettingsViewModel: AppSettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current)),
    onNavigateToPersonalProfile: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToAppInfo: () -> Unit,
    onLogout: () -> Unit
) {
    val currentTheme by appSettingsViewModel.theme.collectAsState()
    val currentLanguage by appSettingsViewModel.language.collectAsState()
    val currentCurrency by appSettingsViewModel.currency.collectAsState()
    val notificationsEnabled by appSettingsViewModel.notificationsEnabled.collectAsState()

    // Convert string values to enum types
    val themeEnum = ThemeSetting.fromString(currentTheme)
    val currencyEnum = Currency.fromCode(currentCurrency) ?: Currency.USD

    val translationProvider = LocalTranslationProvider.current

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = translate(Translations.AppSettings),
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profile Section
            item {
                SectionHeader(title = translate(Translations.SettingsProfile))
            }
            item {
                SettingsSectionCard {
                    SettingsItemCard(
                        title = translate(Translations.SettingsProfile),
                        icon = Icons.Default.Person,
                        onClick = onNavigateToPersonalProfile
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsItemCard(
                        title = translate(Translations.Security),
                        icon = Icons.Default.Security,
                        onClick = onNavigateToSecurity
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // App Settings Section
            item {
                SectionHeader(title = translate(Translations.Settings))
            }
            item {
                SettingsSectionCard {
                    ThemeSettingCard(
                        currentTheme = themeEnum,
                        onThemeSelected = { appSettingsViewModel.setTheme(it.value) },
                        onShowDialog = { showThemeDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    LanguageSettingCard(
                        currentLanguageCode = currentLanguage,
                        availableLanguages = translationProvider.getAvailableLanguages(),
                        onLanguageSelected = { appSettingsViewModel.setLanguage(it.code) },
                        onShowDialog = { showLanguageDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    CurrencySettingCard(
                        currentCurrency = currencyEnum,
                        onCurrencySelected = { appSettingsViewModel.setCurrency(it.code) },
                        onShowDialog = { showCurrencyDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    NotificationSettingCard(
                        notificationsEnabled = notificationsEnabled,
                        onToggle = { appSettingsViewModel.setNotificationsEnabled(it) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Support & Information Section
            item {
                SectionHeader(title = translate(Translations.AppInfo))
            }
            item {
                SettingsSectionCard {
                    SettingsItemCard(
                        title = translate(Translations.DataSync),
                        subtitle = translate(Translations.DataSyncDesc),
                        icon = Icons.Default.Sync,
                        onClick = { /* TODO */ }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsItemCard(
                        title = translate(Translations.BackupRestore),
                        subtitle = translate(Translations.BackupRestoreDesc),
                        icon = Icons.Default.Restore,
                        onClick = { /* TODO */ }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsItemCard(
                        title = translate(Translations.HelpCenter),
                        subtitle = translate(Translations.HelpCenterDesc),
                        icon = Icons.Default.HelpOutline,
                        onClick = { /* TODO */ }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsItemCard(
                        title = translate(Translations.ContactSupport),
                        subtitle = translate(Translations.ContactSupportDesc),
                        icon = Icons.Default.ContactSupport,
                        onClick = { /* TODO */ }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    SettingsItemCard(
                        title = translate(Translations.AppInfo),
                        icon = Icons.Default.Info,
                        onClick = onNavigateToAppInfo
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Logout Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Logout, 
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = translate(Translations.Logout),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = themeEnum,
                onDismiss = { showThemeDialog = false },
                onThemeSelected = {
                    appSettingsViewModel.setTheme(it.value)
                    showThemeDialog = false
                }
            )
        }

        if (showLanguageDialog) {
            LanguageSelectionDialog(
                availableLanguages = translationProvider.getAvailableLanguages(),
                currentLanguageCode = currentLanguage,
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = {
                    appSettingsViewModel.setLanguage(it.code)
                    showLanguageDialog = false
                }
            )
        }

        if (showCurrencyDialog) {
            CurrencySelectionDialog(
                currentCurrency = currencyEnum,
                onDismiss = { showCurrencyDialog = false },
                onCurrencySelected = {
                    appSettingsViewModel.setCurrency(it.code)
                    showCurrencyDialog = false
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItemCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ThemeSettingCard(
    currentTheme: ThemeSetting,
    onThemeSelected: (ThemeSetting) -> Unit,
    onShowDialog: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onShowDialog,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.ThemeSetting),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${translate(Translations.ThemeSettingDesc)}: ${getThemeDisplayName(currentTheme)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LanguageSettingCard(
    currentLanguageCode: String,
    availableLanguages: List<LanguageOption>,
    onLanguageSelected: (LanguageOption) -> Unit,
    onShowDialog: () -> Unit
) {
    val currentLanguageDisplay = availableLanguages.find { it.code == currentLanguageCode }?.displayName ?: currentLanguageCode
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onShowDialog,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.LanguageSetting),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${translate(Translations.LanguageSettingDesc)}: $currentLanguageDisplay",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CurrencySettingCard(
    currentCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit,
    onShowDialog: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onShowDialog,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.CurrencySetting),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${translate(Translations.CurrencySettingDesc)}: ${getCurrencyDisplayName(currentCurrency)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun NotificationSettingCard(
    notificationsEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.NotificationsSetting),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (notificationsEnabled)
                        translate(Translations.NotificationsEnabled)
                    else
                        translate(Translations.NotificationsDisabled),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeSetting,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeSetting) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = translate(Translations.ThemeSetting),
                fontWeight = FontWeight.SemiBold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeSetting.values().forEach { theme ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (theme == currentTheme) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == currentTheme),
                                onClick = { onThemeSelected(theme) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = getThemeDisplayName(theme),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (theme == currentTheme) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = translate(Translations.Cancel),
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun LanguageSelectionDialog(
    availableLanguages: List<LanguageOption>,
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (LanguageOption) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = translate(Translations.SelectLanguage),
                fontWeight = FontWeight.SemiBold
            ) 
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableLanguages.size) { index ->
                    val language = availableLanguages[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (language.code == currentLanguageCode) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (language.code == currentLanguageCode),
                                onClick = { onLanguageSelected(language) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (language.code == currentLanguageCode) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = translate(Translations.Cancel),
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun CurrencySelectionDialog(
    currentCurrency: Currency,
    onDismiss: () -> Unit,
    onCurrencySelected: (Currency) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = translate(Translations.SelectCurrency),
                fontWeight = FontWeight.SemiBold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Currency.values().forEach { currency ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(currency) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currency == currentCurrency) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (currency == currentCurrency),
                                onClick = { onCurrencySelected(currency) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = getCurrencyDisplayName(currency),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (currency == currentCurrency) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = translate(Translations.Cancel),
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun getThemeDisplayName(theme: ThemeSetting): String {
    return when (theme) {
        ThemeSetting.LIGHT -> translate(Translations.ThemeLight)
        ThemeSetting.DARK -> translate(Translations.ThemeDark)
        ThemeSetting.SYSTEM -> translate(Translations.ThemeSystem)
    }
}

@Composable
fun getCurrencyDisplayName(currency: Currency): String {
    return when (currency) {
        Currency.IDR -> "Indonesian Rupiah (IDR)"
        Currency.USD -> "US Dollar (USD)"
        Currency.EUR -> "Euro (EUR)"
        Currency.JPY -> "Japanese Yen (JPY)"
        // Add other currencies as needed
    }
}
