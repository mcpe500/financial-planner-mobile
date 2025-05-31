package com.example.financialplannerapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween, // To push skip to bottom and content towards center/top
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.2f)) // Pushes content down a bit

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.6f) // Main content area
        ) {
            Text(
                text = "Welcome to Financial Planner!",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Let's get you started on your financial journey.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text("Get Started")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onLoginClick) {
                Text("Already have an account? Login")
            }
        }

        TextButton(
            onClick = onSkipClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp) // Padding from the very bottom
        ) {
            Text("Skip for now")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    FinancialPlannerAppTheme {
        OnboardingScreen(
            onGetStartedClick = {},
            onLoginClick = {},
            onSkipClick = {}
        )
    }
}