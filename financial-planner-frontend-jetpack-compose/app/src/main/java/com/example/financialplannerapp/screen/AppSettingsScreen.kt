package com.example.financialplannerapp.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

private const val TAG_APP_SETTINGS = "AppSettingsScreen"

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val BibitDarkGreen = Color(0xFF388E3C)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(navController: NavController) {
    Log.d(TAG_APP_SETTINGS, "AppSettingsScreen composing...")
    
    val context = LocalContext.current
    
    // State for settings
    var selectedTheme by remember { mutableStateOf("Sistem") }
    var selectedLanguage by remember { mutableStateOf("Bahasa Indonesia") }
    var selectedCurrency by remember { mutableStateOf("IDR - Rupiah") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    
    val themeOptions = listOf("Terang", "Gelap", "Sistem")
    val languageOptions = listOf("Bahasa Indonesia", "English", "中文")
    val currencyOptions = listOf("IDR - Rupiah", "USD - Dollar", "EUR - Euro", "JPY - Yen")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan Aplikasi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG_APP_SETTINGS, "Back button clicked")
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BibitGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Setting
            SettingCard(
                title = "Tema Aplikasi",
                icon = Icons.Filled.Palette
            ) {
                Column {
                    Text(
                        text = "Pilih tema yang diinginkan",
                        fontSize = 14.sp,
                        color = MediumGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    themeOptions.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == theme,
                                onClick = { 
                                    selectedTheme = theme
                                    Log.d(TAG_APP_SETTINGS, "Theme changed to: $theme")
                                    Toast.makeText(context, "Tema diubah ke $theme", Toast.LENGTH_SHORT).show()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = BibitGreen)
                            )
                            Text(
                                text = theme,
                                fontSize = 16.sp,
                                color = DarkGray,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // Language Setting
            SettingCard(
                title = "Bahasa",
                icon = Icons.Filled.Language
            ) {
                Column {
                    Text(
                        text = "Pilih bahasa aplikasi",
                        fontSize = 14.sp,
                        color = MediumGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedLanguage,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BibitGreen,
                                unfocusedBorderColor = MediumGray
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            languageOptions.forEach { language ->
                                DropdownMenuItem(
                                    text = { Text(language) },
                                    onClick = {
                                        selectedLanguage = language
                                        expanded = false
                                        Log.d(TAG_APP_SETTINGS, "Language changed to: $language")
                                        Toast.makeText(context, "Bahasa diubah ke $language", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Currency Setting
            SettingCard(
                title = "Mata Uang Default",
                icon = Icons.Filled.AttachMoney
            ) {
                Column {
                    Text(
                        text = "Pilih mata uang default untuk aplikasi",
                        fontSize = 14.sp,
                        color = MediumGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCurrency,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BibitGreen,
                                unfocusedBorderColor = MediumGray
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            currencyOptions.forEach { currency ->
                                DropdownMenuItem(
                                    text = { Text(currency) },
                                    onClick = {
                                        selectedCurrency = currency
                                        expanded = false
                                        Log.d(TAG_APP_SETTINGS, "Currency changed to: $currency")
                                        Toast.makeText(context, "Mata uang diubah ke $currency", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Notifications Setting
            SettingCard(
                title = "Notifikasi Lokal",
                icon = Icons.Filled.Notifications
            ) {
                Column {
                    Text(
                        text = "Aktifkan notifikasi untuk pengingat dan update",
                        fontSize = 14.sp,
                        color = MediumGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aktifkan Notifikasi",
                            fontSize = 16.sp,
                            color = DarkGray
                        )
                        
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { 
                                notificationsEnabled = it
                                Log.d(TAG_APP_SETTINGS, "Notifications ${if (it) "enabled" else "disabled"}")
                                Toast.makeText(
                                    context, 
                                    "Notifikasi ${if (it) "diaktifkan" else "dinonaktifkan"}", 
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BibitGreen,
                                checkedTrackColor = BibitLightGreen
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = BibitGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }
            
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppSettingsScreenPreview() {
    AppSettingsScreen(navController = rememberNavController())
}