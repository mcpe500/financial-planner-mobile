package com.example.financialplannerapp.activities.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.financialplannerapp.activities.MainActivity
import com.example.financialplannerapp.databinding.ActivityLoginBinding
import com.example.financialplannerapp.utils.TokenManager
import com.google.android.gms.common.SignInButton

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Check if already logged in
        if (tokenManager.getToken() != null) {
            navigateToMain()
            return
        }

        // Set up Google Sign-In button
        binding.signInButton.setSize(SignInButton.SIZE_WIDE)
        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }

        binding.passcodeButton.setOnClickListener(){
         navigateToPasscode()
        }

        // Handle deep link if the activity was started from a URL
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun signInWithGoogle() {
        // Open web browser to your backend's Google auth endpoint
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://10.0.2.2:3000/api/auth/google"))
        startActivity(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Handle the custom URL scheme from your backend
        val data = intent.data
        if (data != null && "finplanner" == data.scheme) {
            val token = data.getQueryParameter("token")
            Log.d(TAG, "Received token: $token")
            if (token != null) {
                // Save the token
                tokenManager.saveToken(token)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToPasscode(){
        val intent = Intent(this, PasscodeActivity::class.java)
        startActivity(intent)
        finish()
    }
}