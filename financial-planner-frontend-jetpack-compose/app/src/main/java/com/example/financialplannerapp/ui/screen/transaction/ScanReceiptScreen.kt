package com.example.financialplannerapp.ui.screen.transaction

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.data.model.ReceiptOCRState
import com.example.financialplannerapp.ui.viewmodel.ScanReceiptViewModel
import com.example.financialplannerapp.ui.viewmodel.ScanReceiptViewModelFactory

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

// Custom SVG Icons
private val CustomArrowBack: ImageVector
    get() {
        return ImageVector.Builder(
            name = "ArrowBack",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = androidx.compose.ui.graphics.SolidColor(Color.Black),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Butt,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = androidx.compose.ui.graphics.PathFillType.NonZero
            ) {
                moveTo(20.0f, 11.0f)
                horizontalLineTo(7.83f)
                lineToRelative(5.59f, -5.59f)
                lineTo(12.0f, 4.0f)
                lineToRelative(-8.0f, 8.0f)
                lineToRelative(8.0f, 8.0f)
                lineToRelative(1.41f, -1.41f)
                lineTo(7.83f, 13.0f)
                horizontalLineTo(20.0f)
                verticalLineTo(11.0f)
                close()
            }
        }.build()
    }

private val CustomLogin: ImageVector
    get() {
        return ImageVector.Builder(
            name = "Login",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = androidx.compose.ui.graphics.SolidColor(Color.Black),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Butt,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = androidx.compose.ui.graphics.PathFillType.NonZero
            ) {
                moveTo(11.0f, 7.0f)
                lineToRelative(-1.41f, 1.41f)
                lineTo(12.17f, 11.0f)
                horizontalLineTo(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(10.17f)
                lineToRelative(-2.58f, 2.59f)
                lineTo(11.0f, 17.0f)
                lineToRelative(5.0f, -5.0f)
                lineToRelative(-5.0f, -5.0f)
                close()
                moveTo(20.0f, 19.0f)
                horizontalLineTo(12.0f)
                verticalLineTo(21.0f)
                horizontalLineTo(20.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(5.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                horizontalLineTo(12.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(20.0f)
                verticalLineTo(19.0f)
                close()
            }
        }.build()
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanReceiptScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    
    // Initialize ViewModel
    val viewModel: ScanReceiptViewModel = viewModel(
        factory = ScanReceiptViewModelFactory(
            receiptService = application.appContainer.receiptService,
            tokenManager = application.appContainer.tokenManager
        )
    )
    
    // Collect state from ViewModel
    val state by viewModel.state.collectAsState()
    
    // Image picker state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.currentImageUri?.let { uri ->
                selectedImageUri = uri
                viewModel.processReceipt(uri)
            }
        }
    }
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            viewModel.processReceipt(it)
        }
    }
    
    // Permission launchers
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Launch camera
            cameraLauncher.launch(viewModel.createImageUri(context))
        } else {
            Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }
    
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Launch gallery
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Storage permission is required to access photos", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Handle state changes and show toasts
    LaunchedEffect(state) {
        val currentState = state
        when (currentState) {
            is ReceiptOCRState.Success -> {
                Toast.makeText(
                    context, 
                    "Receipt processed successfully! Found ${currentState.data.items.size} items", 
                    Toast.LENGTH_SHORT
                ).show()
            }
            is ReceiptOCRState.Error -> {
                Toast.makeText(context, "Error: ${currentState.message}", Toast.LENGTH_LONG).show()
            }
            is ReceiptOCRState.Unauthenticated -> {
                Toast.makeText(context, "Please log in to scan receipts", Toast.LENGTH_SHORT).show()
                navController.navigate("login")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Scan Receipt",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(CustomArrowBack, contentDescription = "Back")
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
                .background(SoftGray)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val currentState = state) {
                is ReceiptOCRState.Idle -> {
                    // Show scan options
                    ScanOptionsCard(
                        onCameraClick = {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                    cameraLauncher.launch(viewModel.createImageUri(context))
                                }
                                else -> {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        },
                        onGalleryClick = {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                                    galleryLauncher.launch("image/*")
                                }
                                else -> {
                                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    )
                }
                
                is ReceiptOCRState.Processing -> {
                    // Show selected image if available
                    selectedImageUri?.let { uri ->
                        ReceiptPreviewCard(uri)
                    }
                    ProcessingCard()
                }
                
                is ReceiptOCRState.Success -> {
                    // Receipt Preview
                    selectedImageUri?.let { uri ->
                        ReceiptPreviewCard(uri)
                    }

                    // OCR Results
                    OCRResultsCard(currentState.data)

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.resetState()
                                selectedImageUri = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = BibitGreen
                            )
                        ) {
                            Text("Scan Again")
                        }

                        Button(
                            onClick = {
                                // Navigate to add transaction with OCR data
                                navController.navigate("add_transaction_from_ocr")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BibitGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Use Data")
                        }
                    }
                }
                
                is ReceiptOCRState.Error -> {
                    // Show error state
                    selectedImageUri?.let { uri ->
                        ReceiptPreviewCard(uri)
                    }
                    
                    ErrorCard(
                        message = currentState.message,
                        onRetry = {
                            selectedImageUri?.let { uri ->
                                viewModel.processReceipt(uri)
                            }
                        },
                        onScanAgain = {
                            viewModel.resetState()
                            selectedImageUri = null
                        }
                    )
                }
                
                is ReceiptOCRState.Unauthenticated -> {
                    // Show authentication required message
                    UnauthenticatedCard(
                        onLoginClick = {
                            navController.navigate("login")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanOptionsCard(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "Camera",
                modifier = Modifier.size(64.dp),
                tint = BibitGreen
            )

            Text(
                text = "Scan Your Receipt",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            Text(
                text = "Take a photo or upload from gallery to extract transaction details automatically",
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCameraClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BibitGreen
                    )
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera")
                }

                Button(
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }
            }
        }
    }
}

@Composable
private fun ProcessingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = BibitGreen
            )

            Text(
                text = "Processing Receipt...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )

            Text(
                text = "Extracting transaction details from your receipt",
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ReceiptPreviewCard(imageUri: Uri? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Receipt Preview",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Receipt Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(SoftGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = "Receipt",
                            modifier = Modifier.size(48.dp),
                            tint = MediumGray
                        )
                        Text(
                            text = "Receipt Image",
                            color = MediumGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OCRResultsCard(ocrData: com.example.financialplannerapp.data.model.ReceiptOCRData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = "OCR",
                    tint = BibitGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Extracted Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            // Total Amount
            OCRResultItem(
                label = "Total Amount",
                value = "$${String.format("%.2f", ocrData.totalAmount)}",
                icon = Icons.Default.AttachMoney
            )

            // Merchant/Store Name
            if (ocrData.merchantName.isNotEmpty()) {
                OCRResultItem(
                    label = "Merchant",
                    value = ocrData.merchantName,
                    icon = Icons.Default.Store
                )
            }

            // Date
            if (ocrData.date.isNotEmpty()) {
                OCRResultItem(
                    label = "Date",
                    value = ocrData.date,
                    icon = Icons.Default.DateRange
                )
            }

            // Items breakdown
            if (ocrData.items.isNotEmpty()) {
                Text(
                    text = "Items (${ocrData.items.size}):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGray,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                )

                ocrData.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• ${item.name}",
                            fontSize = 12.sp,
                            color = MediumGray,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$${String.format("%.2f", item.price)}",
                            fontSize = 12.sp,
                            color = MediumGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Confidence Score
            if (ocrData.confidence > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Confidence",
                        tint = BibitGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confidence: ${(ocrData.confidence * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                }
            }
        }
    }
}

@Composable
private fun OCRResultItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = BibitGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MediumGray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    onScanAgain: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )

            Text(
                text = "Processing Failed",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            Text(
                text = message,
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onScanAgain,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BibitGreen
                    )
                ) {
                    Text("Scan Again")
                }

                Button(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BibitGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun UnauthenticatedCard(
    onLoginClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                CustomLogin,
                contentDescription = "Login Required",
                modifier = Modifier.size(64.dp),
                tint = BibitGreen
            )

            Text(
                text = "Login Required",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            Text(
                text = "Please log in to your account to scan receipts and extract transaction data.",
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BibitGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Log In")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanReceiptScreenPreview() {
    ScanReceiptScreen(rememberNavController())
}
