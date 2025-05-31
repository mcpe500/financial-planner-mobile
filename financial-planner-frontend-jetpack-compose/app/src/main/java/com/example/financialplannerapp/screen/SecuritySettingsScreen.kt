package com.example.financialplannerapp.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.financialplannerapp.data.DatabaseManager
import com.example.financialplannerapp.data.SecurityData

private const val TAG_SECURITY = "SecuritySettingsScreen"

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val DarkGray = Color(0xFF424242)
private val MediumGray = Color(0xFF9E9E9E)
private val SoftGray = Color(0xFFF5F5F5)
private val WarningOrange = Color(0xFFFF9800)
private val ErrorRed = Color(0xFFF44336)

// Security Settings Data Classes - Using new DatabaseManager
data class SecuritySettings(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val pinHash: String? = null,
    val autoLockTimeout: Int = 5, // minutes
    val isAutoLockEnabled: Boolean = true
)

// Convert between SecurityData and SecuritySettings for UI compatibility
fun SecurityData.toSecuritySettings() = SecuritySettings(
    isPinEnabled = isPinEnabled,
    isBiometricEnabled = isBiometricEnabled,
    pinHash = pinHash,
    autoLockTimeout = autoLockTimeout,
    isAutoLockEnabled = isAutoLockEnabled
)

fun SecuritySettings.toSecurityData(userId: String) = SecurityData(
    userId = userId,
    isPinEnabled = isPinEnabled,
    isBiometricEnabled = isBiometricEnabled,
    pinHash = pinHash,
    autoLockTimeout = autoLockTimeout,
    isAutoLockEnabled = isAutoLockEnabled
)

// Database helper functions
class SecurityDatabaseHelper(private val context: Context) {
    private val databaseManager = DatabaseManager.getInstance(context)
    private val securityRepository = databaseManager.securityRepository
    
    suspend fun getSecuritySettings(userId: String): SecuritySettings {
        return securityRepository.getSecuritySettings(userId)?.toSecuritySettings() 
            ?: SecuritySettings()
    }
    
    suspend fun saveSecuritySettings(userId: String, settings: SecuritySettings) {
        securityRepository.updateSecuritySettings(settings.toSecurityData(userId))
    }
    
    suspend fun savePinHash(userId: String, pinHash: String) {
        securityRepository.savePinHash(userId, pinHash)
    }
    
    suspend fun removePinHash(userId: String) {
        securityRepository.removePinHash(userId)
    }
    
    suspend fun verifyPin(userId: String, inputPin: String): Boolean {
        return securityRepository.verifyPin(userId, inputPin)
    }
}

/*
// ============================================================================
// ROOM DATABASE IMPLEMENTATION (COMMENTED - READY FOR PRODUCTION)
// ============================================================================

// When ready to switch to Room database, simply:
// 1. Change USE_ROOM_DATABASE = true in DatabaseManager.kt
// 2. Uncomment the Room implementation in DatabaseManager.kt
// 3. Add Room dependencies to build.gradle:
//    implementation "androidx.room:room-runtime:2.5.0"
//    implementation "androidx.room:room-ktx:2.5.0"
//    kapt "androidx.room:room-compiler:2.5.0"
// 4. Add kapt plugin to build.gradle: apply plugin: 'kotlin-kapt'

// No changes needed in this file - the SecurityDatabaseHelper will automatically
// use Room database when the flag is switched in DatabaseManager.kt

*/

// Legacy database object for backward compatibility (will be removed)
object SecurityDatabase {
    private val helper = mutableMapOf<String, SecurityDatabaseHelper>()
    
    fun getHelper(context: Context, userId: String = "default_user"): SecurityDatabaseHelper {
        return helper.getOrPut(userId) { SecurityDatabaseHelper(context) }
    }
}

// Simple PIN hashing function (in production, use proper hashing)
fun hashPin(pin: String): String {
    return pin.hashCode().toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(navController: NavController) {
    Log.d(TAG_SECURITY, "SecuritySettingsScreen composing...")
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Database helper
    val databaseHelper = remember { SecurityDatabaseHelper(context) }
    val userId = "default_user" // In real app, get from TokenManager
    
    // Security settings state
    var securitySettings by remember { mutableStateOf(SecuritySettings()) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showConfirmDisableDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var isSettingPin by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    
    // Load security settings on startup
    LaunchedEffect(Unit) {
        securitySettings = databaseHelper.getSecuritySettings(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Keamanan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG_SECURITY, "Back button clicked")
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
            // Header
            SecurityHeaderCard()
            
            // PIN Settings
            PinSecurityCard(
                securitySettings = securitySettings,
                onTogglePin = { enabled ->
                    if (enabled) {
                        showPinDialog = true
                        isSettingPin = true
                    } else {
                        showConfirmDisableDialog = true
                    }
                }
            )
            
            // Biometric Settings (simulated)
            BiometricSecurityCard(
                securitySettings = securitySettings,
                onToggleBiometric = { enabled ->
                    coroutineScope.launch {
                        val updatedSettings = securitySettings.copy(isBiometricEnabled = enabled)
                        databaseHelper.saveSecuritySettings(userId, updatedSettings)
                        securitySettings = updatedSettings
                        Toast.makeText(
                            context,
                            if (enabled) "Autentikasi biometrik diaktifkan" else "Autentikasi biometrik dinonaktifkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
            
            // Auto Lock Settings
            AutoLockSettingsCard(
                securitySettings = securitySettings,
                onToggleAutoLock = { enabled ->
                    coroutineScope.launch {
                        val updatedSettings = securitySettings.copy(isAutoLockEnabled = enabled)
                        databaseHelper.saveSecuritySettings(userId, updatedSettings)
                        securitySettings = updatedSettings
                    }
                },
                onTimeoutChange = { timeout ->
                    coroutineScope.launch {
                        val updatedSettings = securitySettings.copy(autoLockTimeout = timeout)
                        databaseHelper.saveSecuritySettings(userId, updatedSettings)
                        securitySettings = updatedSettings
                    }
                }
            )
        }
        
        // PIN Setup Dialog
        if (showPinDialog) {
            PinSetupDialog(
                pinInput = pinInput,
                confirmPinInput = confirmPinInput,
                pinError = pinError,
                isSettingPin = isSettingPin,
                onPinInputChange = { pinInput = it },
                onConfirmPinInputChange = { confirmPinInput = it },
                onConfirm = {
                    when {
                        pinInput.length < 4 -> {
                            pinError = "PIN minimal 4 digit"
                        }
                        pinInput != confirmPinInput -> {
                            pinError = "PIN tidak sama"
                        }
                        else -> {
                            coroutineScope.launch {
                                val hashedPin = hashPin(pinInput)
                                databaseHelper.savePinHash(userId, hashedPin)
                                securitySettings = databaseHelper.getSecuritySettings(userId)
                                Toast.makeText(context, "PIN berhasil diatur", Toast.LENGTH_SHORT).show()
                                
                                // Reset states
                                showPinDialog = false
                                pinInput = ""
                                confirmPinInput = ""
                                pinError = ""
                                isSettingPin = false
                            }
                        }
                    }
                },
                onDismiss = {
                    showPinDialog = false
                    pinInput = ""
                    confirmPinInput = ""
                    pinError = ""
                    isSettingPin = false
                }
            )
        }
        
        // Confirm disable PIN dialog
        if (showConfirmDisableDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDisableDialog = false },
                title = { Text("Nonaktifkan PIN") },
                text = { Text("Apakah Anda yakin ingin menonaktifkan PIN? Aplikasi akan menjadi kurang aman.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                databaseHelper.removePinHash(userId)
                                securitySettings = databaseHelper.getSecuritySettings(userId)
                                showConfirmDisableDialog = false
                                Toast.makeText(context, "PIN berhasil dinonaktifkan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Ya", color = ErrorRed)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmDisableDialog = false }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
private fun SecurityHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Background gradient effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(BibitGreen.copy(alpha = 0.1f), BibitLightGreen.copy(alpha = 0.05f))
                        ),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(BibitGreen, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Keamanan",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Keamanan Aplikasi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGray
                        )
                        Text(
                            text = "Lindungi data keuangan Anda",
                            fontSize = 14.sp,
                            color = MediumGray
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SoftGray, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storage,
                        contentDescription = "Local Storage",
                        tint = BibitGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Semua pengaturan keamanan disimpan secara lokal dan aman di perangkat Anda",
                        fontSize = 13.sp,
                        color = DarkGray,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PinSecurityCard(
    securitySettings: SecuritySettings,
    onTogglePin: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (securitySettings.isPinEnabled) BibitGreen.copy(alpha = 0.2f) else MediumGray.copy(alpha = 0.2f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Pin,
                        contentDescription = "PIN",
                        tint = if (securitySettings.isPinEnabled) BibitGreen else MediumGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PIN Aplikasi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Text(
                        text = "Lindungi aplikasi dengan PIN 4-6 digit",
                        fontSize = 14.sp,
                        color = MediumGray
                    )
                }
            }
            
            // Status indicator with enhanced design
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (securitySettings.isPinEnabled) BibitGreen.copy(alpha = 0.1f) else SoftGray,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (securitySettings.isPinEnabled) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (securitySettings.isPinEnabled) BibitGreen else MediumGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (securitySettings.isPinEnabled) "PIN Aktif" else "PIN Tidak Aktif",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (securitySettings.isPinEnabled) BibitGreen else MediumGray
                        )
                    }
                    if (securitySettings.isPinEnabled) {
                        Text(
                            text = "🛡️ Aplikasi terlindungi dengan PIN",
                            fontSize = 12.sp,
                            color = BibitGreen,
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    } else {
                        Text(
                            text = "⚠️ Aplikasi tidak memiliki proteksi PIN",
                            fontSize = 12.sp,
                            color = WarningOrange,
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    }
                }
                
                Switch(
                    checked = securitySettings.isPinEnabled,
                    onCheckedChange = onTogglePin,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BibitGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MediumGray
                    )
                )
            }
        }
    }
}

@Composable
private fun BiometricSecurityCard(
    securitySettings: SecuritySettings,
    onToggleBiometric: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (securitySettings.isBiometricEnabled) BibitGreen.copy(alpha = 0.2f) else MediumGray.copy(alpha = 0.2f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = "Biometrik",
                        tint = if (securitySettings.isBiometricEnabled) BibitGreen else MediumGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Autentikasi Biometrik",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Text(
                        text = "Gunakan sidik jari atau Face ID",
                        fontSize = 14.sp,
                        color = MediumGray
                    )
                }
            }
            
            // Status indicator with enhanced design
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (securitySettings.isBiometricEnabled) BibitGreen.copy(alpha = 0.1f) else SoftGray,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (securitySettings.isBiometricEnabled) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (securitySettings.isBiometricEnabled) BibitGreen else MediumGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (securitySettings.isBiometricEnabled) "Biometrik Aktif" else "Biometrik Tidak Aktif",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (securitySettings.isBiometricEnabled) BibitGreen else MediumGray
                        )
                    }
                    Row(
                        modifier = Modifier.padding(start = 28.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Simulasi - Integrasi sistem akan datang",
                            fontSize = 12.sp,
                            color = WarningOrange
                        )
                    }
                }
                
                Switch(
                    checked = securitySettings.isBiometricEnabled,
                    onCheckedChange = onToggleBiometric,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BibitGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MediumGray
                    )
                )
            }
        }
    }
}

@Composable
private fun AutoLockSettingsCard(
    securitySettings: SecuritySettings,
    onToggleAutoLock: (Boolean) -> Unit,
    onTimeoutChange: (Int) -> Unit
) {
    val timeoutOptions = listOf(1, 2, 5, 10, 15, 30)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (securitySettings.isAutoLockEnabled) BibitGreen.copy(alpha = 0.2f) else MediumGray.copy(alpha = 0.2f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = "Auto Lock",
                        tint = if (securitySettings.isAutoLockEnabled) BibitGreen else MediumGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kunci Otomatis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Text(
                        text = "Kunci aplikasi saat tidak aktif",
                        fontSize = 14.sp,
                        color = MediumGray
                    )
                }
            }
            
            // Main toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (securitySettings.isAutoLockEnabled) BibitGreen.copy(alpha = 0.1f) else SoftGray,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (securitySettings.isAutoLockEnabled) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (securitySettings.isAutoLockEnabled) BibitGreen else MediumGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (securitySettings.isAutoLockEnabled) "Kunci Otomatis Aktif" else "Kunci Otomatis Tidak Aktif",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (securitySettings.isAutoLockEnabled) BibitGreen else MediumGray
                        )
                    }
                    if (securitySettings.isAutoLockEnabled) {
                        Text(
                            text = "⏰ Otomatis kunci setelah ${securitySettings.autoLockTimeout} menit",
                            fontSize = 12.sp,
                            color = BibitGreen,
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    }
                }
                
                Switch(
                    checked = securitySettings.isAutoLockEnabled,
                    onCheckedChange = onToggleAutoLock,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BibitGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MediumGray
                    )
                )
            }
            
            // Timeout selection (only visible when auto-lock is enabled)
            if (securitySettings.isAutoLockEnabled) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Waktu Tunggu:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    
                    OutlinedTextField(
                        value = "${securitySettings.autoLockTimeout} menit",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = "Dropdown",
                                    tint = BibitGreen
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BibitGreen,
                            unfocusedBorderColor = MediumGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
                    ) {
                        timeoutOptions.forEach { timeout ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Timer,
                                            contentDescription = null,
                                            tint = BibitGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("$timeout menit")
                                    }
                                },
                                onClick = {
                                    onTimeoutChange(timeout)
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (timeout == securitySettings.autoLockTimeout) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = BibitGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinSetupDialog(
    pinInput: String,
    confirmPinInput: String,
    pinError: String,
    isSettingPin: Boolean,
    onPinInputChange: (String) -> Unit,
    onConfirmPinInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = "Security",
                    tint = BibitGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Atur PIN Aplikasi",
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Buat PIN 4-6 digit untuk melindungi aplikasi Anda dari akses yang tidak sah",
                    fontSize = 14.sp,
                    color = MediumGray,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                // PIN Strength Indicator
                if (pinInput.isNotEmpty()) {
                    val strength = when {
                        pinInput.length < 4 -> "Terlalu Pendek"
                        pinInput.length == 4 -> "Lemah"
                        pinInput.length == 5 -> "Sedang"
                        pinInput.length >= 6 -> "Kuat"
                        else -> "Lemah"
                    }
                    
                    val strengthColor = when {
                        pinInput.length < 4 -> ErrorRed
                        pinInput.length == 4 -> WarningOrange
                        pinInput.length == 5 -> BibitLightGreen
                        pinInput.length >= 6 -> BibitGreen
                        else -> WarningOrange
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(strengthColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                pinInput.length < 4 -> Icons.Filled.Warning
                                pinInput.length >= 6 -> Icons.Filled.CheckCircle
                                else -> Icons.Filled.Info
                            },
                            contentDescription = null,
                            tint = strengthColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kekuatan PIN: $strength",
                            fontSize = 12.sp,
                            color = strengthColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) onPinInputChange(it) },
                    label = { Text("Masukkan PIN") },
                    placeholder = { Text("Minimal 4 digit") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = BibitGreen
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = confirmPinInput,
                    onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) onConfirmPinInputChange(it) },
                    label = { Text("Konfirmasi PIN") },
                    placeholder = { Text("Masukkan ulang PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BibitGreen,
                        focusedLabelColor = BibitGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = BibitGreen
                        )
                    }
                )
                
                if (pinError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ErrorRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pinError,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Security tips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BibitGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = BibitGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Tips Keamanan:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BibitGreen
                        )
                        Text(
                            text = "• Gunakan kombinasi angka yang unik\n• Hindari tanggal lahir atau angka berurutan\n• PIN yang lebih panjang lebih aman",
                            fontSize = 11.sp,
                            color = DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = pinInput.isNotEmpty() && confirmPinInput.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen,
                    disabledContainerColor = MediumGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Atur PIN")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = MediumGray)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SecuritySettingsScreenPreview() {
    SecuritySettingsScreen(navController = rememberNavController())
}