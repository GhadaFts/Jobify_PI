package com.example.jobify

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.jobify.repository.AuthRepository

abstract class BaseDrawerActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var btnMenu: ImageView
    protected lateinit var btnTheme: ImageView
    private lateinit var sessionManager: SessionManager

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        sessionManager = SessionManager(this)
        setupDrawer()
        setupBackPressHandler()
    }

    private fun setupDrawer() {
        // Initialize drawer views
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        btnTheme = findViewById(R.id.btnTheme)

        // Menu button
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Theme toggle
        btnTheme.setOnClickListener {
            toggleTheme()
        }

        // Setup all menu items
        setupMenuItems()
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupMenuItems() {
        findViewById<LinearLayout>(R.id.menuHomeLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            // Navigate to home if not already there
        }

        findViewById<LinearLayout>(R.id.menuProfileLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (this !is ProfileActivity) {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }

        findViewById<LinearLayout>(R.id.menuCorrectCVLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (this !is CvCorrectionActivity) {
                startActivity(Intent(this, CvCorrectionActivity::class.java))
            }
        }

        findViewById<LinearLayout>(R.id.menuInterviewTrainingLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (this !is InterviewPreparationActivity) {
                startActivity(Intent(this, InterviewPreparationActivity::class.java))
            }
        }

        findViewById<LinearLayout>(R.id.menuJobMarketAnalyseLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            if (this !is AICareerAdvisorActivity) {
                startActivity(Intent(this, AICareerAdvisorActivity::class.java))
            }
        }

        findViewById<LinearLayout>(R.id.menuLogoutLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            handleLogout()
        }
    }

    private fun toggleTheme() {
        val current = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (current == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            showMessage("Light mode activated")
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            showMessage("Dark mode activated")
        }
    }

    private fun handleLogout() {
        val authRepository = AuthRepository()
        val accessToken = sessionManager.getAccessToken()
        val refreshToken = sessionManager.getRefreshToken()

        if (accessToken != null && refreshToken != null) {
            authRepository.logout(
                accessToken = accessToken,
                refreshToken = refreshToken,
                onSuccess = {
                    sessionManager.clearSession()
                    showMessage("Logged out successfully")
                    navigateToLogin()
                },
                onError = {
                    sessionManager.clearSession()
                    showMessage("Logged out")
                    navigateToLogin()
                }
            )
        } else {
            sessionManager.clearSession()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    protected open fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}