package com.example.financialplannerapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financialplannerapp.adapters.FAQAdapter
import com.example.financialplannerapp.adapters.HelpContentAdapter
import com.example.financialplannerapp.databinding.FragmentHelpAndFAQBinding
import com.example.financialplannerapp.ui.viewmodels.ViewModelFactory
import com.example.financialplannerapp.ui.viewmodels.settings.HelpAndFaqViewModel
import kotlinx.coroutines.launch

class HelpAndFAQFragment : Fragment() {

    private var _binding: FragmentHelpAndFAQBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HelpAndFaqViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }

    private lateinit var faqAdapter: FAQAdapter
    private lateinit var helpContentAdapter: HelpContentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpAndFAQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearchView()
        observeViewModel()

        // Initial load can be triggered by ViewModel's init block or explicitly here
        // viewModel.loadInitialData() // Already called in ViewModel's init
    }

    private fun setupRecyclerViews() {
        faqAdapter = FAQAdapter(emptyList())
        binding.recyclerViewFaqs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = faqAdapter
        }

        helpContentAdapter = HelpContentAdapter(emptyList())
        binding.recyclerViewHelpArticles.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = helpContentAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchViewHelp.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.search(it) }
                binding.searchViewHelp.clearFocus() // Hide keyboard
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.clearSearch()
                }
                // Optionally, implement live search:
                // newText?.let { viewModel.search(it) }
                return true
            }
        })

        // Handle clear button click on SearchView (X icon)
        val closeButton: View? = binding.searchViewHelp.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            binding.searchViewHelp.setQuery("", false) // Clear query
            binding.searchViewHelp.clearFocus()
            viewModel.clearSearch() // Reload initial data
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Update FAQ list
                    faqAdapter.updateData(state.faqs)
                    binding.textViewNoFaqs.visibility = if (state.faqs.isEmpty() && !state.isLoading && state.searchQuery.isNotEmpty()) View.VISIBLE else View.GONE


                    // Update Help Articles list
                    helpContentAdapter.updateData(state.helpArticles)
                    binding.textViewNoHelpArticles.visibility = if (state.helpArticles.isEmpty() && !state.isLoading && state.searchQuery.isNotEmpty()) View.VISIBLE else View.GONE


                    // Handle loading state
                    binding.progressBarHelp.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    // Handle errors
                    state.error?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        // Optionally, you might want to clear the error in the ViewModel after showing it
                    }

                    // Update visibility of sections based on search results and loading state
                    if (state.isLoading) {
                        binding.textViewFaqsTitle.visibility = View.GONE
                        binding.recyclerViewFaqs.visibility = View.GONE
                        binding.textViewHelpArticlesTitle.visibility = View.GONE
                        binding.recyclerViewHelpArticles.visibility = View.GONE
                    } else {
                        binding.textViewFaqsTitle.visibility = if (state.faqs.isNotEmpty() || state.searchQuery.isEmpty()) View.VISIBLE else View.GONE
                        binding.recyclerViewFaqs.visibility = if (state.faqs.isNotEmpty() || state.searchQuery.isEmpty()) View.VISIBLE else View.GONE

                        binding.textViewHelpArticlesTitle.visibility = if (state.helpArticles.isNotEmpty() || state.searchQuery.isEmpty()) View.VISIBLE else View.GONE
                        binding.recyclerViewHelpArticles.visibility = if (state.helpArticles.isNotEmpty() || state.searchQuery.isEmpty()) View.VISIBLE else View.GONE

                        if (state.searchQuery.isNotEmpty()) {
                             binding.textViewFaqsTitle.text = "FAQs matching \"${state.searchQuery}\""
                             binding.textViewHelpArticlesTitle.text = "Help Articles matching \"${state.searchQuery}\""
                        } else {
                            binding.textViewFaqsTitle.text = "Frequently Asked Questions" // Reset to default
                            binding.textViewHelpArticlesTitle.text = "Help Articles & Guides" // Reset to default
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}