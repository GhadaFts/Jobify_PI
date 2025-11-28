package com.example.jobify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.databinding.ActivitySignupBinding
import com.example.jobify.network.LoginResponse
import com.example.jobify.repository.AuthRepository

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository()
        sessionManager = SessionManager(this)
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
                R.id.rbJobSeeker -> "job_seeker"
                R.id.rbRecruiter -> "recruiter"
                else -> "job_seeker"
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

        setLoading(true)

        // Step 1: Register the user
        authRepository.register(
            fullName = fullName,
            email = email,
            password = password,
            role = role,
            onSuccess = { registerResponse ->
                // Step 2: Auto-login after successful registration
                performAutoLogin(email, password, fullName, role)
            },
            onError = { errorMessage ->
                setLoading(false)
                showError(errorMessage)
            }
        )
    }

    private fun performAutoLogin(
        email: String,
        password: String,
        fullName: String,
        role: String
    ) {
        authRepository.login(
            email = email,
            password = password,
            onSuccess = { loginResponse ->
                // Fetch user profile to get complete user data
                fetchUserProfile(loginResponse, fullName, email, role)
            },
            onError = { errorMessage ->
                setLoading(false)
                // Registration succeeded but login failed
                // Show message and redirect to login
                Toast.makeText(
                    this,
                    "Account created! Please login to continue.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                finish()
            }
        )
    }

    private fun fetchUserProfile(
        loginResponse: LoginResponse,
        fallbackName: String,
        fallbackEmail: String,
        fallbackRole: String
    ) {
        authRepository.getUserProfile(
            accessToken = loginResponse.accessToken,
            onSuccess = { userProfile ->
                // Save complete session with tokens and user data
                sessionManager.saveUserSession(
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken,
                    expiresIn = loginResponse.expiresIn,
                    email = userProfile.email,
                    name = userProfile.fullName,
                    role = userProfile.role,
                    keycloakId = userProfile.keycloakId
                )

                setLoading(false)
                navigateToProfileSetup(userProfile.fullName, userProfile.email, userProfile.role)
            },
            onError = { error ->
                // Profile fetch failed, but we can still proceed with fallback data
                sessionManager.saveUserSession(
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken,
                    expiresIn = loginResponse.expiresIn,
                    email = fallbackEmail,
                    name = fallbackName,
                    role = fallbackRole,
                    keycloakId = ""
                )

                setLoading(false)
                navigateToProfileSetup(fallbackName, fallbackEmail, fallbackRole)
            }
        )
    }

    private fun navigateToProfileSetup(fullName: String, email: String, role: String) {
        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

        val intent = if (role == "job_seeker") {
            Intent(this, JobSeekerProfileInitialActivity::class.java)
        } else {
            Intent(this, RecruiterProfileInitialActivity::class.java)
        }

        intent.putExtra("fullName", fullName)
        intent.putExtra("email", email)
        intent.putExtra("role", role)

        startActivity(intent)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        isLoading = loading
        binding.btnSignup.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE

        // Disable all input fields during loading
        binding.etFullName.isEnabled = !loading
        binding.etEmail.isEnabled = !loading
        binding.etPassword.isEnabled = !loading
        binding.etConfirmPassword.isEnabled = !loading
        binding.rgRole.isEnabled = !loading
        binding.rbJobSeeker.isEnabled = !loading
        binding.rbRecruiter.isEnabled = !loading
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE

        // Auto-hide error after 5 seconds
        binding.tvError.postDelayed({
            binding.tvError.visibility = View.GONE
        }, 5000)
    }
}