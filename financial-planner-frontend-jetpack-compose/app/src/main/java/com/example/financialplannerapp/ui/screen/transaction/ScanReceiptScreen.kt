package com.example.financialplannerapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanReceiptScreen(navController: NavController) {
    var hasScannedReceipt by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Mock OCR results
    val ocrResults = remember {
        mapOf(
            "amount" to "25.50",
            "merchant" to "Coffee Shop ABC",
            "date" to "May 27, 2024",
            "items" to listOf("Cappuccino - $4.50", "Sandwich - $8.00", "Tax - $1.00", "Total - $25.50")
        )
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            if (!hasScannedReceipt) {
                // Camera/Upload Section
                CameraUploadCard(
                    onCameraClick = {
                        isProcessing = true
                        // Simulate processing delay
//                        kotlinx.coroutines.GlobalScope.launch {
//                            kotlinx.coroutines.delay(2000)
//                            hasScannedReceipt = true
//                            isProcessing = false
//                        }
                    },
                    onGalleryClick = {
                        isProcessing = true
                        // Simulate processing delay
//                        kotlinx.coroutines.GlobalScope.launch {
//                            kotlinx.coroutines.delay(2000)
//                            hasScannedReceipt = true
//                            isProcessing = false
//                        }
                    }
                )

                if (isProcessing) {
                    ProcessingCard()
                }
            } else {
                // Receipt Preview
                ReceiptPreviewCard()

                // OCR Results
                OCRResultsCard(ocrResults)

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            hasScannedReceipt = false
                            isProcessing = false
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
        }
    }
}

@Composable
private fun CameraUploadCard(
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
private fun ReceiptPreviewCard() {
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

            // Mock receipt image placeholder
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

@Composable
private fun OCRResultsCard(ocrResults: Map<String, Any>) {
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

            // Amount
            OCRResultItem(
                label = "Amount",
                value = "$${ocrResults["amount"]}",
                icon = Icons.Default.AttachMoney
            )

            // Merchant
            OCRResultItem(
                label = "Merchant",
                value = ocrResults["merchant"] as String,
                icon = Icons.Default.Store
            )

            // Date
            OCRResultItem(
                label = "Date",
                value = ocrResults["date"] as String,
                icon = Icons.Default.DateRange
            )

            // Items breakdown
            Text(
                text = "Items:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            (ocrResults["items"] as List<String>).forEach { item ->
                Text(
                    text = "• $item",
                    fontSize = 12.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )
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

@Preview(showBackground = true)
@Composable
fun ScanReceiptScreenPreview() {
    ScanReceiptScreen(rememberNavController())
}
