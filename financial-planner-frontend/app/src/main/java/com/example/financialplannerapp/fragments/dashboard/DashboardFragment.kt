package com.example.financialplannerapp.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.financialplannerapp.activities.MainActivity
import com.example.financialplannerapp.databinding.FragmentDashboardBinding
import com.example.financialplannerapp.utils.AppDatabase
import com.example.financialplannerapp.utils.TokenManager
import com.example.financialplannerapp.viewmodels.DashboardViewModel
import com.example.financialplannerapp.viewmodels.DashboardViewModelFactory

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi manual (tanpa Hilt)
        val tokenManager = TokenManager(requireContext())
        val userProfileDao = AppDatabase.getDatabase(requireContext()).userProfileDao()
        val factory = DashboardViewModelFactory(tokenManager, userProfileDao)
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        viewModel.loadUserName()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Load nama user

        // Observe dan tampilkan di UI
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.welcomeText.text = "Hi, $name"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
