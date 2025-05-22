// filepath: d:\Data\VSCode\MDP\project\financial-planner-mobile\financial-planner-frontend\app\src\main\java\com\example\financialplannerapp\fragments\settings\UserProfileFragment.kt
package com.example.financialplannerapp.fragments.settings

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentUserProfileBinding
import com.example.financialplannerapp.db.UserProfileDao
import com.example.financialplannerapp.models.roomdb.UserProfile
import com.example.financialplannerapp.utils.AppDatabase
import com.example.financialplannerapp.utils.NetworkUtils
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager
    private lateinit var userProfileDao: UserProfileDao
    private val TAG = "UserProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        
        // Initialize database
        val db = AppDatabase.getDatabase(requireContext())
        userProfileDao = db.userProfileDao()
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            setupUI()
            loadUserProfile()
            updateSyncButtonState()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated: ${e.message}", e)
            Toast.makeText(context, "Error initializing profile view", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        // Setup UI components
        binding.saveButton.setOnClickListener {
            saveUserProfile()
        }
        
        binding.syncButton.setOnClickListener {
            syncUserProfile()
        }
        
        // Additional UI setup
    }
    
    private fun loadUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = tokenManager.getUserId() ?: "guest_user"
                val profile = userProfileDao.getUserProfile(userId)
                
                if (profile != null) {
                    // Populate UI with profile data
                    binding.nameEditText.setText(profile.name)
                    binding.emailEditText.setText(profile.email)
                    binding.phoneEditText.setText(profile.phone)
                    
                    // Format last synced date nicely
                    val syncText = if (profile.lastSynced != null) {
                        "Terakhir disinkronisasi: ${profile.lastSynced}"
                    } else {
                        "Terakhir disinkronisasi: Belum pernah"
                    }
                    binding.lastSyncTextView.text = syncText
                    
                    // Load avatar image if available
                    if (!profile.avatarUrl.isNullOrEmpty()) {
                        Glide.with(this@UserProfileFragment)
                            .load(profile.avatarUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(binding.profileImage)
                    }
                } else {
                    // No profile in local database yet
                    binding.lastSyncTextView.text = "Terakhir disinkronisasi: Belum pernah"
                    Log.d(TAG, "No local profile found, creating default")
                    
                    // If we have a token, try to fill in email from token info
                    tokenManager.getUserEmail()?.let { email ->
                        binding.emailEditText.setText(email)
                    }
                    
                    tokenManager.getUserName()?.let { name ->
                        binding.nameEditText.setText(name)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile: ${e.message}")
                Toast.makeText(requireContext(), "Error saat memuat profil", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveUserProfile() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        
        // Simple validation
        if (name.isBlank()) {
            binding.nameEditText.error = "Nama tidak boleh kosong"
            return
        }
        
        if (email.isBlank()) {
            binding.emailEditText.error = "Email tidak boleh kosong"
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = tokenManager.getUserId() ?: "guest_user"
                val existingProfile = userProfileDao.getUserProfile(userId)
                
                // Create or update profile in local database
                val profile = UserProfile(
                    userId = userId,
                    name = name,
                    email = email,
                    phone = phone,
                    avatarUrl = existingProfile?.avatarUrl,
                    lastSynced = existingProfile?.lastSynced,
                    needsSync = true // Mark as needing sync
                )
                
                userProfileDao.insertOrUpdate(profile)
                Toast.makeText(requireContext(), "Profil disimpan di perangkat", Toast.LENGTH_SHORT).show()
                
                // Try to sync if we're online
                if (NetworkUtils.isNetworkAvailable(requireContext()) && tokenManager.getToken() != null) {
                    syncUserProfile()
                } else {
                    updateSyncButtonState()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving profile: ${e.message}")
                Toast.makeText(requireContext(), "Error saat menyimpan profil", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun syncUserProfile() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (tokenManager.getToken() == null) {
            Toast.makeText(requireContext(), "Silakan login untuk menyinkronkan profil", Toast.LENGTH_LONG).show()
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.syncProgressBar.visibility = View.VISIBLE
                binding.syncButton.isEnabled = false
                
                val userId = tokenManager.getUserId() ?: return@launch
                val profile = userProfileDao.getUserProfile(userId) ?: return@launch
                
                // Simulate API call with delay (since backend doesn't exist)
                kotlinx.coroutines.delay(1500)
                
                // Update profile with sync time
                val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                val currentTime = dateFormat.format(Date())
                
                profile.lastSynced = currentTime
                profile.needsSync = false
                userProfileDao.insertOrUpdate(profile)
                
                binding.lastSyncTextView.text = "Terakhir disinkronisasi: $currentTime"
                Toast.makeText(requireContext(), "Profil berhasil disinkronkan", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing profile: ${e.message}")
                Toast.makeText(requireContext(), "Error saat menyinkronkan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.syncProgressBar.visibility = View.GONE
                updateSyncButtonState()
            }
        }
    }
    
    private fun updateSyncButtonState() {
        try {
            if (context == null) return
            
            val isOnline = try {
                NetworkUtils.isNetworkAvailable(requireContext())
            } catch (e: Exception) {
                Log.e(TAG, "Error checking network state: ${e.message}", e)
                false
            }
            
            val isLoggedIn = tokenManager.getToken() != null
            
            viewLifecycleOwner.lifecycleScope.launch {
                val userId = tokenManager.getUserId() ?: "guest_user"
                val profile = userProfileDao.getUserProfile(userId)
                val needsSync = profile?.needsSync == true
                
                binding.syncButton.isEnabled = isOnline && isLoggedIn && needsSync
                binding.offlineIndicator.visibility = if (isOnline) View.GONE else View.VISIBLE
                
                if (!isOnline) {
                    binding.syncStatusText.text = "Mode Offline"
                    binding.syncStatusText.setTextColor(Color.RED)
                } else if (!isLoggedIn) {
                    binding.syncStatusText.text = "Login diperlukan untuk sinkronisasi"
                    binding.syncStatusText.setTextColor(Color.YELLOW)
                } else if (needsSync) {
                    binding.syncStatusText.text = "Perubahan belum disinkronkan"
                    binding.syncStatusText.setTextColor(Color.YELLOW)
                } else {
                    binding.syncStatusText.text = "Profil telah tersinkronisasi"
                    binding.syncStatusText.setTextColor(Color.GREEN)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating sync button state: ${e.message}", e)
            // Set default state to avoid UI issues
            binding.syncButton.isEnabled = false
            binding.syncStatusText.text = "Status unavailable"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}