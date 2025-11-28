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

abstract class BaseDrawerActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var btnMenu: ImageView
    protected lateinit var btnTheme: ImageView

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupDrawer()
        setupBackPressHandler() // ✅ Ajoute cette ligne
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

    // ✅ NOUVEAU: Gestion moderne du bouton retour
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
        // Add your logout logic here
        showMessage("Logged out")
    }

    protected open fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // ❌ SUPPRIMER cette vieille fonction
    // override fun onBackPressed() { ... }
}