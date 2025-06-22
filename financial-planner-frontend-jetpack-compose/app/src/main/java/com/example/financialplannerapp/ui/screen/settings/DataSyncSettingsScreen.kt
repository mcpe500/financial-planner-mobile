package com.example.financialplannerapp.screen.settings

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch // Added
import kotlinx.coroutines.Dispatchers // Added
import kotlinx.coroutines.withContext // Added
import java.io.IOException // Added
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.ui.viewmodel.DataSyncViewModel
import com.example.financialplannerapp.ui.viewmodel.DataSyncViewModelFactory

private const val TAG_DATA_SYNC = "DataSyncSettingsScreen"

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val BibitDarkGreen = Color(0xFF388E3C)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSyncSettingsScreen(navController: NavController) {
    Log.d(TAG_DATA_SYNC, "DataSyncSettingsScreen composing...")
    
    val context = LocalContext.current
    val app = context.applicationContext as MainApplication
    val viewModel: DataSyncViewModel = viewModel(
        factory = DataSyncViewModelFactory(app)
    )
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val syncResult by viewModel.syncResult.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    // Check backend connectivity on screen load
    LaunchedEffect(Unit) {
        viewModel.checkBackendConnectivity()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sinkronisasi Data",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG_DATA_SYNC, "Back button clicked")
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
            // Connection Status Card
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
                            imageVector = Icons.Filled.NetworkCheck,
                            contentDescription = "Status Koneksi",
                            tint = BibitGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Status Koneksi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        if (isConnected) BibitGreen else Color.Red,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isConnected) "Terhubung" else "Tidak Terhubung",
                                fontSize = 16.sp,
                                color = DarkGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        TextButton(
                            onClick = {
                                viewModel.checkBackendConnectivity()
                            }
                        ) {
                            Text(
                                text = "Cek Ulang Koneksi",
                                color = BibitGreen
                            )
                        }
                    }
                }
            }
            
            // Last Sync Time Card
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
                            imageVector = Icons.Filled.History,
                            contentDescription = "Sinkronisasi Terakhir",
                            tint = BibitGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sinkronisasi Terakhir",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray
                        )
                    }
                    
                    Text(
                        text = if (lastSyncTime.isNotBlank()) lastSyncTime else "Belum pernah disinkronkan",
                        fontSize = 16.sp,
                        color = MediumGray
                    )
                }
            }
            
            // Sync Now Card
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
                            imageVector = Icons.Filled.Sync,
                            contentDescription = "Sinkronkan Sekarang",
                            tint = BibitGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sinkronkan Sekarang",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray
                        )
                    }
                    
                    Text(
                        text = "Sinkronkan data lokal dengan server. Pastikan koneksi internet stabil.",
                        fontSize = 14.sp,
                        color = MediumGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = {
                            if (isConnected) {
                                viewModel.syncAll()
                            } else {
                                Toast.makeText(context, "Tidak dapat sinkronisasi tanpa koneksi internet", Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = isConnected && !isSyncing,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BibitGreen,
                            contentColor = Color.White,
                            disabledContainerColor = MediumGray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Menyinkronkan...")
                        } else {
                            Icon(
                                imageVector = Icons.Filled.CloudSync,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mulai Sinkronisasi")
                        }
                    }
                    if (syncResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = syncResult ?: "",
                            color = if (syncResult?.contains("berhasil") == true) BibitGreen else Color.Red,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Auto Sync Settings Card
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
                            imageVector = Icons.Filled.AutoMode,
                            contentDescription = "Sinkronisasi Otomatis",
                            tint = BibitGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Pengaturan Otomatis",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray
                        )
                    }
                    
                    var autoSyncEnabled by remember { mutableStateOf(true) }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sinkronisasi Otomatis",
                                fontSize = 16.sp,
                                color = DarkGray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Sinkronkan data secara otomatis saat tersambung WiFi",
                                fontSize = 12.sp,
                                color = MediumGray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        
                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { 
                                autoSyncEnabled = it
                                Log.d(TAG_DATA_SYNC, "Auto sync ${if (it) "enabled" else "disabled"}")
                                Toast.makeText(
                                    context,
                                    "Sinkronisasi otomatis ${if (it) "diaktifkan" else "dinonaktifkan"}",
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

@Preview(showBackground = true)
@Composable
fun DataSyncSettingsScreenPreview() {
    DataSyncSettingsScreen(navController = rememberNavController())
}