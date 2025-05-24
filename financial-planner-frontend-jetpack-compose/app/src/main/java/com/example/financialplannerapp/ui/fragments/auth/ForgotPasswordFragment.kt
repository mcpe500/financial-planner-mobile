package com.example.financialplannerapp.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Import for by viewModels()
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.ui.screens.auth.ForgotPasswordScreenContent
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme
import com.example.financialplannerapp.ui.viewmodels.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    // private val viewModel: ForgotPasswordViewModel by viewModels() // Will be used later

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                FinancialPlannerAppTheme {
                    // Pass ViewModel and NavController actions later
                    ForgotPasswordScreenContent(
                        // onSendResetLinkClick = { email -> viewModel.onSendResetLinkClicked(email) },
                        // onNavigateBackToLogin = { findNavController().popBackStack() } // Or navigate to login explicitly
                    )
                }
            }
        }
    }
}