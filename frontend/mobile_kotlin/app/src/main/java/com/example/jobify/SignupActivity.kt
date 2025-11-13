package com.example.jobify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Signup button click
        binding.btnSignup.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val role = when (binding.rgRole.checkedRadioButtonId) {
                R.id.rbJobSeeker -> "jobseeker"
                R.id.rbRecruiter -> "recruiter"
                else -> "jobseeker"
            }

            if (validateInputs(fullName, email, password, confirmPassword)) {
                performSignup(fullName, email, password, role)
            }
        }

        // Login click
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Clear previous errors
        binding.tvError.visibility = View.GONE
        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        // Validate full name
        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            isValid = false
        } else if (fullName.length < 3) {
            binding.tilFullName.error = "Name must be at least 3 characters"
            isValid = false
        }

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

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    private fun performSignup(
        fullName: String,
        email: String,
        password: String,
        role: String
    ) {
        if (isLoading) return

        isLoading = true
        binding.btnSignup.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        // Simulate API call
        binding.btnSignup.postDelayed({
            // Here you would make your actual API call
            // For now, we'll simulate successful registration

            isLoading = false
            binding.btnSignup.isEnabled = true
            binding.progressBar.visibility = View.GONE

            // Show success message
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

            // Navigate to appropriate profile setup based on role
            val intent = if (role == "jobseeker") {
                Intent(this, JobSeekerProfileInitialActivity::class.java)
            } else {
                Intent(this, RecruiterProfileInitialActivity::class.java)
            }

            // Pass user data to profile activity if needed
            intent.putExtra("fullName", fullName)
            intent.putExtra("email", email)
            intent.putExtra("role", role)

            startActivity(intent)
            finish()

            // If registration fails, show error:
            // showError("Registration failed. Please try again.")
        }, 2000)
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }
}