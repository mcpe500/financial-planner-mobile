package com.example.financialplannerapp.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape // Added for icon background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Added for icon background
import androidx.compose.ui.draw.shadow // Added for card shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Added for explicit font sizes
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

// Bibit-inspired color palette (consistent with other screens)
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val BibitDarkGreen = Color(0xFF388E3C)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val DangerRed = Color(0xFFE53935) // For logout button
private val LightRed = Color(0xFFFFCDD2) // For logout button container

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
                        fontSize = 22.sp, // Larger title
                        fontWeight = FontWeight.Bold, // Bolder title
                        color = Color.White // White text for better contrast
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Assuming AppSettingsScreen is always pushed from SettingsScreen
                            // so popBackStack is correct to go back to SettingsScreen
                            // If this screen can be accessed directly, adjust logic
                            // or pass a specific back action lambda.
                        }
                    ) {
                        // This TopAppBar is likely within a NestedNavGraph
                        // and a common solution is to provide a back button here.
                        // However, based on the previous context, AppSettingsScreen is a direct
                        // child of the main SettingsScreen route.
                        // If you intend to have a back button that truly navigates back,
                        // you'll need to pass navController as a parameter and use navController.popBackStack()
                        // For now, I'll assume you don't want a back button from App Settings.
                        // If you do, uncomment and adjust:
                        /*
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = translate(Translations.Back),
                            tint = Color.White
                        )
                        */
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BibitGreen, // Green background for TopAppBar
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = SoftGray // Consistent light gray background for the screen
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing between sections
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
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
                    SettingsItemCard(
                        title = translate(Translations.Security),
                        icon = Icons.Default.Security,
                        onClick = onNavigateToSecurity
                    )
                }
            }

            // No Spacer here, increased verticalArrangement in LazyColumn

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
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
                    LanguageSettingCard(
                        currentLanguageCode = currentLanguage,
                        availableLanguages = translationProvider.getAvailableLanguages(),
                        onLanguageSelected = { appSettingsViewModel.setLanguage(it.code) },
                        onShowDialog = { showLanguageDialog = true }
                    )
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
                    CurrencySettingCard(
                        currentCurrency = currencyEnum,
                        onCurrencySelected = { appSettingsViewModel.setCurrency(it.code) },
                        onShowDialog = { showCurrencyDialog = true }
                    )
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
                    NotificationSettingCard(
                        notificationsEnabled = notificationsEnabled,
                        onToggle = { appSettingsViewModel.setNotificationsEnabled(it) }
                    )
                }
            }

            // No Spacer here, increased verticalArrangement in LazyColumn

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
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
                    SettingsItemCard(
                        title = translate(Translations.BackupRestore),
                        subtitle = translate(Translations.BackupRestoreDesc),
                        icon = Icons.Default.CloudUpload, // Changed to CloudUpload for consistency with main settings
                        onClick = { /* TODO */ }
                    )
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
                    SettingsItemCard(
                        title = translate(Translations.HelpCenter),
                        subtitle = translate(Translations.HelpCenterDesc),
                        icon = Icons.Default.HelpOutline,
                        onClick = { /* TODO */ }
                    )
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
                    SettingsItemCard(
                        title = translate(Translations.ContactSupport),
                        subtitle = translate(Translations.ContactSupportDesc),
                        icon = Icons.Default.ContactSupport,
                        onClick = { /* TODO */ }
                    )
                    Divider(color = MediumGray.copy(alpha = 0.2f), thickness = 0.5.dp) // Thinner, softer divider
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
                // Card for logout button for better visual grouping
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp, // Consistent shadow with other cards
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = Color.Black.copy(alpha = 0.05f),
                            spotColor = Color.Black.copy(alpha = 0.05f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = LightRed // Light red background for logout card
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Elevation handled by .shadow
                ) {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp) // Consistent height with other buttons
                            .padding(horizontal = 4.dp, vertical = 4.dp), // Inner padding
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed, // Stronger red for the button itself
                            contentColor = Color.White // White text on red button
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp) // Larger icon
                        )
                        Spacer(modifier = Modifier.width(12.dp)) // Increased spacing
                        Text(
                            text = translate(Translations.Logout),
                            fontSize = 18.sp, // Larger text
                            fontWeight = FontWeight.SemiBold // Stronger font weight
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
        fontSize = 18.sp, // Slightly larger font for section headers
        fontWeight = FontWeight.Bold, // Bolder headers
        color = DarkGray, // Darker gray for section headers
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp, // Consistent shadow with other cards
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // White background for setting sections
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Elevation handled by .shadow
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Make Surface clickable directly
        color = Color.Transparent // Ensure surface background is transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container (with Bibit-themed circular background)
            Box(
                modifier = Modifier
                    .size(48.dp) // Slightly smaller icon container for sub-items
                    .clip(CircleShape)
                    .background(BibitLightGreen.copy(alpha = 0.2f))
                    .padding(10.dp), // Padding inside the circular background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp), // Standard icon size
                    tint = BibitDarkGreen // Darker green tint
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp, // Consistent bodyLarge equivalent
                    fontWeight = FontWeight.Medium,
                    color = DarkGray // Consistent with main body text
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp, // Smaller subtitle
                        color = MediumGray // Consistent with subtitles
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MediumGray, // Consistent with subtitle color
                modifier = Modifier.size(24.dp) // Larger arrow
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowDialog),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BibitLightGreen.copy(alpha = 0.2f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BibitDarkGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.ThemeSetting),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray
                )
                Text(
                    text = "${translate(Translations.ThemeSettingDesc)}: ${getThemeDisplayName(currentTheme)}",
                    fontSize = 13.sp,
                    color = MediumGray
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MediumGray,
                modifier = Modifier.size(24.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowDialog),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BibitLightGreen.copy(alpha = 0.2f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BibitDarkGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.LanguageSetting),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray
                )
                Text(
                    text = "${translate(Translations.LanguageSettingDesc)}: $currentLanguageDisplay",
                    fontSize = 13.sp,
                    color = MediumGray
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MediumGray,
                modifier = Modifier.size(24.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowDialog),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BibitLightGreen.copy(alpha = 0.2f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BibitDarkGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.CurrencySetting),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray
                )
                Text(
                    text = "${translate(Translations.CurrencySettingDesc)}: ${getCurrencyDisplayName(currentCurrency)}",
                    fontSize = 13.sp,
                    color = MediumGray
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MediumGray,
                modifier = Modifier.size(24.dp)
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
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BibitLightGreen.copy(alpha = 0.2f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BibitDarkGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.NotificationsSetting),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray
                )
                Text(
                    text = if (notificationsEnabled)
                        translate(Translations.NotificationsEnabled)
                    else
                        translate(Translations.NotificationsDisabled),
                    fontSize = 13.sp,
                    color = MediumGray
                )
            }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BibitGreen, // Bibit green for checked thumb
                    checkedTrackColor = BibitLightGreen.copy(alpha = 0.6f), // Lighter green for track
                    uncheckedThumbColor = MediumGray, // Gray for unchecked thumb
                    uncheckedTrackColor = MediumGray.copy(alpha = 0.3f) // Lighter gray for unchecked track
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
                fontSize = 20.sp, // Consistent dialog title size
                fontWeight = FontWeight.Bold,
                color = DarkGray
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
                                BibitLightGreen.copy(alpha = 0.2f) // Light green background for selected
                            else
                                Color.White // White for unselected
                        ),
                        shape = RoundedCornerShape(12.dp), // Rounded corners for dialog items
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                    selectedColor = BibitGreen, // Bibit green for selected radio button
                                    unselectedColor = MediumGray // Medium gray for unselected
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = getThemeDisplayName(theme),
                                fontSize = 16.sp,
                                fontWeight = if (theme == currentTheme) FontWeight.SemiBold else FontWeight.Normal,
                                color = DarkGray
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
                    fontWeight = FontWeight.SemiBold, // Bolder text for buttons
                    color = BibitGreen // Bibit green for action buttons
                )
            }
        },
        shape = RoundedCornerShape(16.dp), // Rounded corners for dialog itself
        containerColor = SoftGray // Soft gray background for dialog
    )
}

@Composable
fun LanguageSelectionDialog(
    availableLanguages: List<LanguageOption>,
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (LanguageOption) -> Unit
) {
    val currentLanguageDisplay = availableLanguages.find { it.code == currentLanguageCode }?.displayName ?: currentLanguageCode

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = translate(Translations.SelectLanguage),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
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
                                BibitLightGreen.copy(alpha = 0.2f)
                            else
                                Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                    selectedColor = BibitGreen,
                                    unselectedColor = MediumGray
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = language.displayName,
                                fontSize = 16.sp,
                                fontWeight = if (language.code == currentLanguageCode) FontWeight.SemiBold else FontWeight.Normal,
                                color = DarkGray
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
                    fontWeight = FontWeight.SemiBold,
                    color = BibitGreen
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = SoftGray
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
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
                                BibitLightGreen.copy(alpha = 0.2f)
                            else
                                Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                    selectedColor = BibitGreen,
                                    unselectedColor = MediumGray
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = getCurrencyDisplayName(currency),
                                fontSize = 16.sp,
                                fontWeight = if (currency == currentCurrency) FontWeight.SemiBold else FontWeight.Normal,
                                color = DarkGray
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
                    fontWeight = FontWeight.SemiBold,
                    color = BibitGreen
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = SoftGray
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