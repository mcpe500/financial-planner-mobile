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
import com.example.financialplannerapp.ui.screens.auth.RegisterScreenContent
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme
import com.example.financialplannerapp.ui.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    // private val viewModel: RegisterViewModel by viewModels() // Will be used later

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                FinancialPlannerAppTheme {
                    // Pass ViewModel and NavController actions later
                    RegisterScreenContent(
                        // onRegisterClick = { name, email, password -> viewModel.onRegisterClicked(name, email, password, password) }, // Assuming confirmPassword is the same for now
                        // onNavigateToLogin = { findNavController().navigate(R.id.action_registerFragment_to_loginFragment) }
                    )
                }
            }
        }
    }
}