package com.example.financialplannerapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.financialplannerapp.activities.auth.LoginActivity
import com.example.financialplannerapp.api.RetrofitClient
import com.example.financialplannerapp.databinding.ActivityMainBinding
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Check if logged in
        if (tokenManager.getToken() == null) {
            redirectToLogin()
            return
        }

        // Set up logout button
        binding.logoutButton.setOnClickListener {
            tokenManager.clearToken()
            redirectToLogin()
        }

        // Load user data
        loadUserData()
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val authHeader = tokenManager.getAuthHeader()
                if (authHeader != null) {
                    val response = RetrofitClient.authService.getCurrentUser(authHeader)
                    if (response.isSuccessful && response.body() != null) {
                        val userData = response.body()?.user
                        binding.userInfoTextView.text = "Welcome ${userData?.name}!\nEmail: ${userData?.email}"
                    } else {
                        Toast.makeText(this@MainActivity, "Error loading user data", Toast.LENGTH_SHORT).show()
                        tokenManager.clearToken()
                        redirectToLogin()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Network error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}