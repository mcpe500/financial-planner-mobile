package com.example.financialplannerapp.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.financialplannerapp.ui.screens.main.HomeScreenContent
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme
import com.example.financialplannerapp.ui.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                FinancialPlannerAppTheme {
                    // Pass ViewModel interactions later
                    HomeScreenContent(
                        // homeUiState = viewModel.uiState.collectAsState().value,
                        // onRefresh = { viewModel.loadHomeScreenData() }
                    )
                }
            }
        }
    }
}