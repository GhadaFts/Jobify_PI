package com.example.jobify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.databinding.ActivityLoginBinding
import com.example.jobify.repository.AuthRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var authRepository: AuthRepository
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        authRepository = AuthRepository()

        // Check if user is already logged in
        checkExistingSession()

        setupListeners()
    }

    private fun checkExistingSession() {
        if (sessionManager.isLoggedIn() && !sessionManager.isTokenExpired()) {
            val role = sessionManager.getUserRole()
            navigateBasedOnRole(role ?: "")
        }
    }

    private fun setupListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                performLogin(email, password)
            }
        }

        // Forgot password click
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(
                this,
                "Please contact support to reset your password",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Sign up click
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Clear previous errors
        binding.tvError.visibility = View.GONE
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        // Validate email
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
            isValid = false
        }

        // Validate password
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        }

        return isValid
    }

    private fun performLogin(email: String, password: String) {
        if (isLoading) return

        setLoading(true)

        authRepository.login(
            email = email,
            password = password,
            onSuccess = { loginResponse ->
                Log.d("LoginActivity", "Login successful, loading profile...")

                // Now get user profile. Ensure tokens are non-null before proceeding.
                val accessToken = loginResponse.accessToken ?: ""
                val refreshToken = loginResponse.refreshToken ?: ""
                val expiresIn = loginResponse.expiresIn ?: 0

                if (accessToken.isBlank()) {
                    setLoading(false)
                    showError("Login failed: missing access token from server")
                } else {
                    authRepository.getUserProfile(
                    accessToken = accessToken,
                    onSuccess = { userProfile ->
                        // Save complete session (use safe defaults for nullable profile fields)
                        sessionManager.saveUserSession(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            expiresIn = expiresIn,
                            email = userProfile.email ?: "",
                            name = userProfile.fullName ?: "",
                            role = userProfile.role ?: "",
                            keycloakId = userProfile.keycloakId ?: ""
                        )

                        setLoading(false)

                        Toast.makeText(
                            this,
                            "Welcome back, ${userProfile.fullName ?: "user"}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate based on role (use empty string if missing)
                        navigateBasedOnRole(userProfile.role ?: "")
                    },
                    onError = { error ->
                        setLoading(false)
                        Log.e("LoginActivity", "Failed to load profile: $error")
                        showError("Login successful but failed to load profile: $error")
                    }
                    )
                }
            },
            onError = { error ->
                setLoading(false)
                Log.e("LoginActivity", "Login failed: $error")
                showError(error)
            }
        )
    }

    private fun navigateBasedOnRole(role: String) {
        val intent = when (role.lowercase()) {
            "jobseeker", "job_seeker" -> Intent(this, JobOpportunitiesActivity::class.java)
            "recruiter" -> Intent(this, PostsActivity::class.java)
            "admin" -> {
                // Navigate to admin dashboard if you have one
                Intent(this, MainActivity::class.java)
            }
            else -> Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        isLoading = loading
        binding.btnLogin.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE

        // Disable inputs during loading
        binding.etEmail.isEnabled = !loading
        binding.etPassword.isEnabled = !loading
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }
}