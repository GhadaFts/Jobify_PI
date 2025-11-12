package com.example.jobify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isLoading = false

    // Static users for demo
    private val staticUsers = listOf(
        User("jobseeker@jobify.com", "jobseeker123", "jobseeker", "John Seeker"),
        User("recruiter@jobify.com", "recruiter123", "recruiter", "Sarah Recruiter")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        showLoginHints()
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
            showLoginHints()
        }

        // Sign up click
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun showLoginHints() {
        val hint = """
            Demo Accounts:
            
            Job Seeker:
            Email: jobseeker@jobify.com
            Password: jobseeker123
            
            Recruiter:
            Email: recruiter@jobify.com
            Password: recruiter123
        """.trimIndent()

        Toast.makeText(this, hint, Toast.LENGTH_LONG).show()
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
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun performLogin(email: String, password: String) {
        if (isLoading) return

        isLoading = true
        binding.btnLogin.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        // Simulate API call with delay
        binding.btnLogin.postDelayed({
            isLoading = false
            binding.btnLogin.isEnabled = true
            binding.progressBar.visibility = View.GONE

            // Check against static users
            val user = staticUsers.find { it.email == email && it.password == password }

            if (user != null) {
                // Save user session using SessionManager
                val sessionManager = SessionManager(this)
                sessionManager.saveUserSession(user.email, user.name, user.role)

                Toast.makeText(
                    this,
                    "Welcome back, ${user.name}!",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate based on role
                navigateBasedOnRole(user.role)
            } else {
                showError("Invalid email or password. Tap 'Forgot Password?' to see demo accounts.")
            }
        }, 1500)
    }

    private fun navigateBasedOnRole(role: String) {
        val intent = when (role) {
            "jobseeker" -> Intent(this, JobOpportunitiesActivity::class.java)
            "recruiter" -> Intent(this, PostsActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun saveUserSession(user: User) {
        // Save to SharedPreferences
        val prefs = getSharedPreferences("JobifyPrefs", MODE_PRIVATE)
        prefs.edit().apply {
            putString("user_email", user.email)
            putString("user_name", user.name)
            putString("user_role", user.role)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }

    // Data class for User
    data class User(
        val email: String,
        val password: String,
        val role: String,
        val name: String
    )
}



