package com.example.financialplannerapp.screen

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.config.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG_USER_PROFILE = "UserProfileSettingsScreen"

// Custom serializer to handle both string and number for monthlyIncome
object FlexibleStringSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FlexibleString", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: String?) {
        if (value != null) {
            encoder.encodeString(value)
        } else {
            encoder.encodeNull()
        }
    }
    
    override fun deserialize(decoder: Decoder): String? {
        return when (val jsonDecoder = decoder as? JsonDecoder) {
            null -> decoder.decodeString()
            else -> {
                val element = jsonDecoder.decodeJsonElement()
                when {
                    element is JsonPrimitive && element.isString -> element.content
                    element is JsonPrimitive -> element.content // This handles numbers as strings
                    else -> null
                }
            }
        }
    }
}

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val BibitDarkGreen = Color(0xFF388E3C)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

@Serializable
data class UserProfileUpdateRequest(
    val name: String,
    val phone: String?,
    val dateOfBirth: String?,
    val occupation: String?,
    val monthlyIncome: String?,
    val financialGoals: String?
)

@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String = "", // Make message optional with default value
    val data: UserProfileResponse? = null
)

@Serializable
data class UserProfileResponse(
    val name: String,
    val email: String,
    val phone: String?,
    val dateOfBirth: String?,
    val occupation: String?,
    @Serializable(with = FlexibleStringSerializer::class)
    val monthlyIncome: String?,
    val financialGoals: String?,
    val updatedAt: String
)

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val occupation: String = "",
    val monthlyIncome: String = "",
    val financialGoals: String = "",
    val lastSyncTime: String = "",
    val isDataModified: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileSettingsScreen(navController: NavController, tokenManager: TokenManager? = null) {
    Log.d(TAG_USER_PROFILE, "UserProfileSettingsScreen composing...")
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Validate TokenManager on start
    LaunchedEffect(Unit) {
        if (tokenManager == null) {
            Log.w(TAG_USER_PROFILE, "TokenManager is null - operating in offline mode")
        } else {
            Log.d(TAG_USER_PROFILE, "TokenManager available - Name: ${tokenManager.getUserName()}, Email: ${tokenManager.getUserEmail()}")
        }
    }
    
    // State for user profile data
    var userProfile by remember { 
        mutableStateOf(
            UserProfile(
                name = tokenManager?.getUserName() ?: "Guest User",
                email = tokenManager?.getUserEmail() ?: "No email available",
                phone = "",
                dateOfBirth = "",
                occupation = "",
                monthlyIncome = "",
                financialGoals = "",
                lastSyncTime = "",
                isDataModified = false
            )
        )
    }
    
    // Loading states
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    
    // Edit mode state
    var isEditMode by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(true) } // Mock connection status
    
    // Temporary edit states
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editDateOfBirth by remember { mutableStateOf("") }
    var editOccupation by remember { mutableStateOf("") }
    var editMonthlyIncome by remember { mutableStateOf("") }
    var editFinancialGoals by remember { mutableStateOf("") }

    // Function to load profile from backend
    fun loadUserProfile() {
        Log.d(TAG_USER_PROFILE, "Starting loadUserProfile - TokenManager: $tokenManager")
        
        // Always first load basic info from TokenManager
        val currentName = tokenManager?.getUserName() ?: "Guest User"
        val currentEmail = tokenManager?.getUserEmail() ?: "No email available"
        
        Log.d(TAG_USER_PROFILE, "Basic user info - Name: $currentName, Email: $currentEmail")
        
        // Update basic profile info first
        userProfile = userProfile.copy(
            name = currentName,
            email = currentEmail
        )
        
        // Try to get token and handle gracefully if not available
        val token = try {
            tokenManager?.getToken()
        } catch (e: Exception) {
            Log.e(TAG_USER_PROFILE, "Error getting token from TokenManager", e)
            null
        }
        
        Log.d(TAG_USER_PROFILE, "Token retrieval - Token available: ${!token.isNullOrBlank()}, Token length: ${token?.length ?: 0}")
        
        if (token.isNullOrBlank()) {
            Log.w(TAG_USER_PROFILE, "No valid authentication token available - using offline mode")
            isLoading = false
            loadError = null
            isConnected = false
            
            // Set profile in offline mode
            userProfile = userProfile.copy(
                lastSyncTime = "Belum pernah disinkronkan",
                isDataModified = false
            )
            
            // Update edit states
            editName = userProfile.name
            editPhone = userProfile.phone
            editDateOfBirth = userProfile.dateOfBirth
            editOccupation = userProfile.occupation
            editMonthlyIncome = userProfile.monthlyIncome
            editFinancialGoals = userProfile.financialGoals
            
            Toast.makeText(context, "Mode offline - data disimpan lokal saja", Toast.LENGTH_LONG).show()
            return
        }

        isLoading = true
        loadError = null
        Log.d(TAG_USER_PROFILE, "Loading user profile from backend...")

        coroutineScope.launch {
            try {
                val result = getUserProfileFromServer(token)
                
                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (result.success) {
                        result.data?.let { responseData ->
                            userProfile = userProfile.copy(
                                name = responseData.name,
                                email = responseData.email,
                                phone = responseData.phone ?: "",
                                dateOfBirth = responseData.dateOfBirth ?: "",
                                occupation = responseData.occupation ?: "",
                                monthlyIncome = responseData.monthlyIncome ?: "",
                                financialGoals = responseData.financialGoals ?: "",
                                lastSyncTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()),
                                isDataModified = false
                            )
                            
                            // Update edit states with loaded data
                            editName = userProfile.name
                            editPhone = userProfile.phone
                            editDateOfBirth = userProfile.dateOfBirth
                            editOccupation = userProfile.occupation
                            editMonthlyIncome = userProfile.monthlyIncome
                            editFinancialGoals = userProfile.financialGoals
                        }
                        Log.d(TAG_USER_PROFILE, "Profile loaded successfully")
                    } else {
                        loadError = result.message
                        Toast.makeText(context, "Gagal memuat profil: ${result.message}", Toast.LENGTH_LONG).show()
                        Log.e(TAG_USER_PROFILE, "Profile load failed: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    loadError = e.message
                    Toast.makeText(context, "Gagal memuat profil: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG_USER_PROFILE, "Profile load error", e)
                }
            }
        }
    }

    // Function to check server connectivity
    fun checkServerConnection() {
        Log.d(TAG_USER_PROFILE, "Checking server connection to ${Config.BASE_URL}")
        
        coroutineScope.launch {
            try {
                val result = testServerConnection()
                
                withContext(Dispatchers.Main) {
                    isConnected = result.success
                    val message = if (result.success) {
                        "Terhubung ke server: ${Config.BASE_URL}"
                    } else {
                        "Tidak dapat terhubung ke server: ${result.message}"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    Log.d(TAG_USER_PROFILE, "Connection test result: ${result.success} - ${result.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isConnected = false
                    Toast.makeText(context, "Error testing connection: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG_USER_PROFILE, "Connection test error", e)
                }
            }
        }
    }

    // Load profile when screen first loads
    LaunchedEffect(Unit) {
        Log.d(TAG_USER_PROFILE, "LaunchedEffect triggered, loading profile...")
        loadUserProfile()
    }

    // Cleanup resources when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            // Cancel any ongoing operations
            Log.d(TAG_USER_PROFILE, "UserProfileSettingsScreen disposed, cleaning up resources")
        }
    }

    // Function to handle actual API call
    fun performSync() {
        if (!isConnected) {
            Toast.makeText(context, "Tidak dapat sinkronisasi tanpa koneksi internet", Toast.LENGTH_LONG).show()
            return
        }
        
        val token = tokenManager?.getToken()
        if (token.isNullOrBlank()) {
            Toast.makeText(context, "Autentikasi dibutuhkan. Silakan login kembali.", Toast.LENGTH_LONG).show()
            Log.e(TAG_USER_PROFILE, "No authentication token available for sync")
            return
        }

        // Validate edit data before syncing
        if (isEditMode) {
            val validation = validateAndSanitizeInput(
                editName,
                editPhone,
                editDateOfBirth,
                editOccupation,
                editMonthlyIncome,
                editFinancialGoals
            )
            
            if (!validation.first) {
                Toast.makeText(context, validation.second, Toast.LENGTH_LONG).show()
                return
            }
        }

        isSyncing = true
        Log.d(TAG_USER_PROFILE, "Starting profile sync with backend...")
        Toast.makeText(context, "Memulai sinkronisasi profil...", Toast.LENGTH_SHORT).show()

        val profileUpdateData = UserProfileUpdateRequest(
            name = if (isEditMode) editName else userProfile.name,
            phone = if (isEditMode) editPhone else userProfile.phone,
            dateOfBirth = if (isEditMode) editDateOfBirth else userProfile.dateOfBirth,
            occupation = if (isEditMode) editOccupation else userProfile.occupation,
            monthlyIncome = if (isEditMode) editMonthlyIncome else userProfile.monthlyIncome,
            financialGoals = if (isEditMode) editFinancialGoals else userProfile.financialGoals
        )
        
        Log.d(TAG_USER_PROFILE, "Profile update data to be sent: name=${profileUpdateData.name}, phone=${profileUpdateData.phone}, dateOfBirth=${profileUpdateData.dateOfBirth}, occupation=${profileUpdateData.occupation}, monthlyIncome=${profileUpdateData.monthlyIncome}, financialGoals=${profileUpdateData.financialGoals}")
        
        coroutineScope.launch {
            try {
                val result = syncProfileToServer(token, profileUpdateData)
                
                withContext(Dispatchers.Main) {
                    if (result.success) {
                        // Update profile with response data
                        result.data?.let { responseData ->
                            userProfile = userProfile.copy(
                                name = responseData.name,
                                phone = responseData.phone ?: "",
                                dateOfBirth = responseData.dateOfBirth ?: userProfile.dateOfBirth,
                                occupation = responseData.occupation ?: "",
                                monthlyIncome = responseData.monthlyIncome ?: "",
                                financialGoals = responseData.financialGoals ?: "",
                                lastSyncTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()),
                                isDataModified = false
                            )
                        }
                        
                        // Exit edit mode on successful sync
                        if (isEditMode) isEditMode = false
                        
                        Toast.makeText(context, "Profil berhasil disinkronkan", Toast.LENGTH_SHORT).show()
                        Log.d(TAG_USER_PROFILE, "Profile sync successful")
                    } else {
                        Toast.makeText(context, "Gagal sinkronisasi: ${result.message}", Toast.LENGTH_LONG).show()
                        Log.e(TAG_USER_PROFILE, "Profile sync failed: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal sinkronisasi: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG_USER_PROFILE, "Profile sync error", e)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isSyncing = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil Pengguna",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG_USER_PROFILE, "Back button clicked")
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
                actions = {
                    if (isEditMode) {
                        // Save button
                        TextButton(
                            onClick = {
                                // Validate input before saving
                                val validation = validateAndSanitizeInput(
                                    editName,
                                    editPhone,
                                    editDateOfBirth,
                                    editOccupation,
                                    editMonthlyIncome,
                                    editFinancialGoals
                                )
                                
                                if (!validation.first) {
                                    Toast.makeText(context, validation.second, Toast.LENGTH_LONG).show()
                                    return@TextButton
                                }
                                
                                // Sanitize inputs
                                val sanitizedName = sanitizeString(editName)
                                val sanitizedPhone = sanitizeString(editPhone)
                                val sanitizedDateOfBirth = sanitizeString(editDateOfBirth)
                                val sanitizedOccupation = sanitizeString(editOccupation)
                                val sanitizedMonthlyIncome = sanitizeString(editMonthlyIncome)
                                val sanitizedFinancialGoals = sanitizeString(editFinancialGoals)
                                
                                // Save changes to local storage (SQLite simulation)
                                userProfile = userProfile.copy(
                                    name = sanitizedName,
                                    phone = sanitizedPhone,
                                    dateOfBirth = sanitizedDateOfBirth,
                                    occupation = sanitizedOccupation,
                                    monthlyIncome = sanitizedMonthlyIncome,
                                    financialGoals = sanitizedFinancialGoals,
                                    isDataModified = true
                                )
                                isEditMode = false
                                Log.d(TAG_USER_PROFILE, "Profile data saved locally with validation")
                                Toast.makeText(context, "Data disimpan. Tekan 'Sinkronkan ke Server' untuk menyimpan ke cloud.", Toast.LENGTH_LONG).show()
                            }
                        ) {
                            Text("Simpan", color = BibitGreen, fontWeight = FontWeight.Medium)
                        }
                        
                        // Cancel button
                        TextButton(
                            onClick = {
                                // Reset edit fields
                                editName = userProfile.name
                                editPhone = userProfile.phone
                                editDateOfBirth = userProfile.dateOfBirth
                                editOccupation = userProfile.occupation
                                editMonthlyIncome = userProfile.monthlyIncome
                                editFinancialGoals = userProfile.financialGoals
                                isEditMode = false
                                Log.d(TAG_USER_PROFILE, "Edit mode cancelled")
                            }
                        ) {
                            Text("Batal", color = MediumGray)
                        }
                    } else {
                        // Edit button
                        IconButton(
                            onClick = {
                                isEditMode = true
                                Log.d(TAG_USER_PROFILE, "Edit mode enabled")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profil",
                                tint = BibitGreen
                            )
                        }
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
            // Loading State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = BibitGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Memuat profil...",
                        fontSize = 16.sp,
                        color = MediumGray
                    )
                }
            }
        } else if (loadError != null) {
            // Error State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Gagal memuat profil",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loadError ?: "Unknown error",
                        fontSize = 14.sp,
                        color = MediumGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { loadUserProfile() },
                        colors = ButtonDefaults.buttonColors(containerColor = BibitGreen)
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }
        } else {
            // Profile Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Profile Header Card
            ProfileHeaderCard(
                userProfile = userProfile,
                isConnected = isConnected,
                onConnectionCheck = { checkServerConnection() }
            )
            
            // Profile Information Cards
            ProfileInfoCard(
                title = "Informasi Personal",
                icon = Icons.Filled.Person,
                content = {
                    ProfileTextField(
                        label = "Nama Lengkap",
                        value = if (isEditMode) editName else userProfile.name,
                        isEditMode = isEditMode,
                        onValueChange = { editName = it }
                    )
                    
                    ProfileTextField(
                        label = "Email",
                        value = userProfile.email,
                        isEditMode = false, // Email cannot be edited
                        onValueChange = { }
                    )
                    
                    ProfileTextField(
                        label = "Nomor Telepon",
                        value = if (isEditMode) editPhone else userProfile.phone,
                        isEditMode = isEditMode,
                        onValueChange = { editPhone = it }
                    )
                    
                    ProfileTextField(
                        label = "Tanggal Lahir",
                        value = if (isEditMode) editDateOfBirth else userProfile.dateOfBirth,
                        isEditMode = isEditMode,
                        onValueChange = { editDateOfBirth = it }
                    )
                }
            )
            
            // Professional Information Card
            ProfileInfoCard(
                title = "Informasi Profesional",
                icon = Icons.Filled.Work,
                content = {
                    ProfileTextField(
                        label = "Pekerjaan",
                        value = if (isEditMode) editOccupation else userProfile.occupation,
                        isEditMode = isEditMode,
                        onValueChange = { editOccupation = it }
                    )
                    
                    ProfileTextField(
                        label = "Pendapatan Bulanan (IDR)",
                        value = if (isEditMode) editMonthlyIncome else userProfile.monthlyIncome,
                        isEditMode = isEditMode,
                        onValueChange = { editMonthlyIncome = it }
                    )
                }
            )
            
            // Financial Goals Card
            ProfileInfoCard(
                title = "Tujuan Keuangan",
                icon = Icons.Filled.TrendingUp,
                content = {
                    ProfileTextField(
                        label = "Tujuan Keuangan",
                        value = if (isEditMode) editFinancialGoals else userProfile.financialGoals,
                        isEditMode = isEditMode,
                        onValueChange = { editFinancialGoals = it },
                        maxLines = 4
                    )
                }
            )
            
            // Sync Status Card
            SyncStatusCard(
                userProfile = userProfile,
                isConnected = isConnected,
                isSyncing = isSyncing,
                onSyncNow = { performSync() }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun UserProfileSettingsScreenPreview() {
    UserProfileSettingsScreen(navController = rememberNavController())
}

// Test server connection with actual HTTP request
suspend fun testServerConnection(): ApiResponse {
    return withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            Log.d(TAG_USER_PROFILE, "Testing connection to: ${Config.BASE_URL}")
            
            val url = URL("${Config.BASE_URL}/api/health") // Health check endpoint
            connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("User-Agent", "FinancialPlannerApp")
            }
            
            val responseCode = connection.responseCode
            Log.d(TAG_USER_PROFILE, "Server response code: $responseCode")
            
            when (responseCode) {
                200 -> {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG_USER_PROFILE, "Server response: $response")
                    ApiResponse(success = true, message = "Server is reachable")
                }
                404 -> {
                    // If health endpoint doesn't exist, try the base URL
                    ApiResponse(success = true, message = "Server is reachable (no health endpoint)")
                }
                else -> {
                    ApiResponse(success = false, message = "Server returned code: $responseCode")
                }
            }
        } catch (e: ConnectException) {
            Log.e(TAG_USER_PROFILE, "Connection refused: ${e.message}")
            ApiResponse(success = false, message = "Connection refused - server may be down")
        } catch (e: SocketTimeoutException) {
            Log.e(TAG_USER_PROFILE, "Connection timeout: ${e.message}")
            ApiResponse(success = false, message = "Connection timeout - server not responding")
        } catch (e: UnknownHostException) {
            Log.e(TAG_USER_PROFILE, "Unknown host: ${e.message}")
            ApiResponse(success = false, message = "Cannot resolve host - check internet connection")
        } catch (e: Exception) {
            Log.e(TAG_USER_PROFILE, "Connection test failed", e)
            ApiResponse(success = false, message = "Connection failed: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }
}

suspend fun getUserProfileFromServer(token: String): ApiResponse {
    return withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("${Config.BASE_URL}/api/profile")
            connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("Authorization", "Bearer $token")
                connectTimeout = Config.CONNECT_TIMEOUT.toInt()
                readTimeout = Config.READ_TIMEOUT.toInt()
            }

            Log.d(TAG_USER_PROFILE, "Getting profile from: $url")

            val responseCode = connection.responseCode
            Log.d(TAG_USER_PROFILE, "Response code: $responseCode")

            if (responseCode in 200..299) {
                // Success response
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG_USER_PROFILE, "Success response: $responseBody")
                
                val json = Json { ignoreUnknownKeys = true }
                val apiResponse = json.decodeFromString(ApiResponse.serializer(), responseBody)
                apiResponse
            } else {
                // Error response
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                Log.e(TAG_USER_PROFILE, "Error response ($responseCode): $errorBody")
                
                ApiResponse(
                    success = false,
                    message = "Server error: $responseCode - $errorBody"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG_USER_PROFILE, "Network error during profile fetch", e)
            ApiResponse(
                success = false,
                message = "Network error: ${e.message}"
            )
        } finally {
            connection?.disconnect()
        }
    }
}

// HTTP API call function using HttpURLConnection (existing implementation)
suspend fun syncProfileToServer(token: String, profileData: UserProfileUpdateRequest): ApiResponse {
    return withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("${Config.BASE_URL}/api/profile/update")
            connection = url.openConnection() as HttpURLConnection
            
            connection.apply {
                requestMethod = "PUT"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $token")
                connectTimeout = Config.CONNECT_TIMEOUT.toInt()
                readTimeout = Config.READ_TIMEOUT.toInt()
            }

            // Convert profile data to JSON
            val json = Json { ignoreUnknownKeys = true }
            val jsonBody = json.encodeToString(UserProfileUpdateRequest.serializer(), profileData)
            
            Log.d(TAG_USER_PROFILE, "Sending request to: $url")
            Log.d(TAG_USER_PROFILE, "Request body: $jsonBody")

            // Write request body
            connection.outputStream.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonBody)
                    writer.flush()
                }
            }

            val responseCode = connection.responseCode
            Log.d(TAG_USER_PROFILE, "Response code: $responseCode")

            if (responseCode in 200..299) {
                // Success response
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG_USER_PROFILE, "Success response: $responseBody")
                
                val apiResponse = json.decodeFromString(ApiResponse.serializer(), responseBody)
                apiResponse
            } else {
                // Error response
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                Log.e(TAG_USER_PROFILE, "Error response ($responseCode): $errorBody")
                
                ApiResponse(
                    success = false,
                    message = "Server error: $responseCode - $errorBody"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG_USER_PROFILE, "Network error during profile sync", e)
            ApiResponse(
                success = false,
                message = "Network error: ${e.message}"
            )
        } finally {
            connection?.disconnect()
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    userProfile: UserProfile,
    isConnected: Boolean,
    onConnectionCheck: () -> Unit
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
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(BibitLightGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.name.take(2).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Text(
                        text = userProfile.email,
                        fontSize = 14.sp,
                        color = MediumGray
                    )
                }
            }
            
            // Connection Status
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
                        text = if (isConnected) "Online" else "Offline",
                        fontSize = 14.sp,
                        color = DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                TextButton(onClick = onConnectionCheck) {
                    Text("Cek Koneksi", color = BibitGreen)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(
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

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    isEditMode: Boolean,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MediumGray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        if (isEditMode && label != "Email") {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                maxLines = maxLines,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BibitGreen,
                    unfocusedBorderColor = MediumGray
                )
            )
        } else {
            Text(
                text = value,
                fontSize = 16.sp,
                color = DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftGray, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                maxLines = maxLines
            )
            if (label == "Email") {
                Text(
                    text = "Email tidak dapat diubah",
                    fontSize = 10.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SyncStatusCard(
    userProfile: UserProfile,
    isConnected: Boolean,
    isSyncing: Boolean,
    onSyncNow: () -> Unit
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
                    imageVector = Icons.Filled.CloudSync,
                    contentDescription = "Sinkronisasi",
                    tint = BibitGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sinkronisasi Data",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }
            
            // Last Sync Info
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Sinkronisasi Terakhir:",
                    fontSize = 12.sp,
                    color = MediumGray
                )
                Text(
                    text = userProfile.lastSyncTime,
                    fontSize = 14.sp,
                    color = DarkGray,
                    fontWeight = FontWeight.Medium
                )
                
                if (userProfile.isDataModified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Perubahan Belum Disinkronkan",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ada perubahan yang belum disinkronkan",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }
            
            // Sync Button
            Button(
                onClick = onSyncNow,
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
                } else if (!isConnected) {
                    Icon(
                        imageVector = Icons.Filled.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Offline - Tidak dapat sinkronisasi")
                } else {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sinkronkan ke Server")
                }
            }
            
            // Offline notice
            if (!isConnected) {
                Text(
                    text = "💾 Data disimpan secara lokal dan akan disinkronkan saat online",
                    fontSize = 12.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Helper functions
private fun isValidPhoneNumber(phone: String): Boolean {
    // Accept Indonesian phone numbers: +62, 08, or 62
    val phoneRegex = Regex("^(\\+62|62|0)[0-9]{8,13}$")
    return phoneRegex.matches(phone.replace("\\s+".toRegex(), ""))
}

private fun isValidDate(dateString: String): Boolean {
    // Accept DD/MM/YYYY or YYYY-MM-DD formats
    val ddmmyyyy = Regex("^(\\d{2})/(\\d{2})/(\\d{4})$")
    val yyyymmdd = Regex("^(\\d{4})-(\\d{2})-(\\d{2})$")
    
    return ddmmyyyy.matches(dateString) || yyyymmdd.matches(dateString)
}

// Input validation and sanitization
private fun validateAndSanitizeInput(
    name: String,
    phone: String,
    dateOfBirth: String,
    occupation: String,
    monthlyIncome: String,
    financialGoals: String
): Pair<Boolean, String> {
    // Name validation
    if (name.isBlank() || name.length < 2 || name.length > 100) {
        return false to "Nama harus diisi (2-100 karakter)"
    }
    
    // Phone validation
    if (phone.isNotBlank() && !isValidPhoneNumber(phone)) {
        return false to "Format nomor telepon tidak valid"
    }
    
    // Date validation
    if (dateOfBirth.isNotBlank() && !isValidDate(dateOfBirth)) {
        return false to "Format tanggal tidak valid (gunakan DD/MM/YYYY)"
    }
    
    // Occupation validation
    if (occupation.length > 100) {
        return false to "Pekerjaan maksimal 100 karakter"
    }
    
    // Monthly income validation
    if (monthlyIncome.isNotBlank()) {
        val income = monthlyIncome.toDoubleOrNull()
        if (income == null || income < 0 || income > 999999999) {
            return false to "Pendapatan bulanan tidak valid"
        }
    }
    
    // Financial goals validation
    if (financialGoals.length > 500) {
        return false to "Tujuan keuangan maksimal 500 karakter"
    }
    
    return true to "Valid"
}

// Sanitize string input
private fun sanitizeString(input: String): String {
    return input.trim().replace(Regex("<[^>]*>"), "") // Remove HTML tags
}