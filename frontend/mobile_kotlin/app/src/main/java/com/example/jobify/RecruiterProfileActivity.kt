package com.example.jobify

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.flexbox.FlexboxLayout
import java.util.*

class RecruiterProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView
    private lateinit var menuProfile: LinearLayout
    private lateinit var menuLogout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recruiter_profile)

        // --- Drawer Menu ---
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        menuProfile = findViewById(R.id.menuProfileLayout)
        menuLogout = findViewById(R.id.menuLogoutLayout)

        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, RecruiterProfileActivity::class.java))
        }
        menuLogout.setOnClickListener {
            // Close drawer first
            drawerLayout.closeDrawer(GravityCompat.START)

            // Perform logout after drawer closes
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }

        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            startActivity(Intent(this, PostsActivity::class.java))
        }

    }
    private fun performLogout() {
        try {
            // Clear session
            val sessionManager = SessionManager(this)
            sessionManager.clearSession()

            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate to MainActivity (splash) which will redirect to login
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

            // Finish current activity
            finishAffinity() // This ensures all activities are cleared

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Logout error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}
