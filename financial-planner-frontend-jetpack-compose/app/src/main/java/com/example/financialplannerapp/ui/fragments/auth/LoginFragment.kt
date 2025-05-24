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
import com.example.financialplannerapp.ui.screens.auth.LoginScreenContent
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme
import com.example.financialplannerapp.ui.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    // private val viewModel: LoginViewModel by viewModels() // Will be used later

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                FinancialPlannerAppTheme {
                    // Pass ViewModel and NavController actions later
                    LoginScreenContent(
                        // onLoginClick = { email, password -> viewModel.onLoginClicked(email, password) },
                        // onNavigateToRegister = { findNavController().navigate(R.id.action_loginFragment_to_registerFragment) },
                        // onNavigateToForgotPassword = { findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment) }
                    )
                }
            }
        }
    }
}