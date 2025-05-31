package com.example.financialplannerapp.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceInputScreen(navController: NavController) {
    var isRecording by remember { mutableStateOf(false) }
    var hasRecorded by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }

    // Mock transcription result
    val transcriptionResult = "I spent 25 dollars and 50 cents on lunch at Coffee Shop ABC today for food category"

    // Recording timer effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingDuration++
            }
        } else {
            recordingDuration = 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Voice Input",
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
            if (!hasRecorded && !isProcessing) {
                // Recording Instructions
                InstructionsCard()

                // Recording Interface
                RecordingCard(
                    isRecording = isRecording,
                    recordingDuration = recordingDuration,
                    onStartRecording = { isRecording = true },
                    onStopRecording = {
                        isRecording = false
                        isProcessing = true
                        // Simulate processing
//                        kotlinx.coroutines.GlobalScope.launch {
//                            delay(2000)
//                            isProcessing = false
//                            hasRecorded = true
//                        }
                    }
                )
            } else if (isProcessing) {
                ProcessingCard()
            } else {
                // Transcription Results
                TranscriptionResultCard(transcriptionResult)

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            hasRecorded = false
                            isProcessing = false
                            recordingDuration = 0
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BibitGreen
                        )
                    ) {
                        Text("Record Again")
                    }

                    Button(
                        onClick = {
                            navController.navigate("add_transaction_from_voice")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BibitGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Use Transcription")
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = "Microphone",
                modifier = Modifier.size(48.dp),
                tint = BibitGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Voice Transaction Input",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Speak naturally about your transaction. For example:",
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExampleText("\"I spent 25 dollars on lunch at McDonald's\"")
                ExampleText("\"Received 3000 dollars salary from work\"")
                ExampleText("\"Paid 85 dollars electricity bill\"")
            }
        }
    }
}

@Composable
private fun ExampleText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = BibitGreen,
        modifier = Modifier
            .background(
                BibitLightGreen.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun RecordingCard(
    isRecording: Boolean,
    recordingDuration: Int,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Recording Button
            FloatingActionButton(
                onClick = if (isRecording) onStopRecording else onStartRecording,
                modifier = Modifier.size(80.dp),
                containerColor = if (isRecording) Color.Red else BibitGreen,
                contentColor = Color.White
            ) {
                Icon(
                    if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Recording Status
            if (isRecording) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Recording...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red
                    )

                    Text(
                        text = "${recordingDuration}s",
                        fontSize = 16.sp,
                        color = MediumGray
                    )

                    // Animated recording indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        Color.Red.copy(alpha = if ((recordingDuration + index) % 3 == 0) 1f else 0.3f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Tap to start recording",
                    fontSize = 16.sp,
                    color = MediumGray,
                    textAlign = TextAlign.Center
                )
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
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = BibitGreen
            )

            Text(
                text = "Processing Audio...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )

            Text(
                text = "Converting your speech to text",
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TranscriptionResultCard(transcription: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.RecordVoiceOver,
                    contentDescription = "Transcription",
                    tint = BibitGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Transcription Result",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray
                )
            }

            Text(
                text = transcription,
                fontSize = 14.sp,
                color = DarkGray,
                modifier = Modifier
                    .background(
                        SoftGray,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Extracted Information:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mock extracted data
            ExtractedInfoItem("Amount", "$25.50")
            ExtractedInfoItem("Merchant", "Coffee Shop ABC")
            ExtractedInfoItem("Category", "Food")
            ExtractedInfoItem("Type", "Expense")
        }
    }
}

@Composable
private fun ExtractedInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MediumGray
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceInputScreenPreview() {
    VoiceInputScreen(rememberNavController())
}
