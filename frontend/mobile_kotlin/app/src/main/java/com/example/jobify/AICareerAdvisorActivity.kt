package com.example.jobify

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class AICareerAdvisorActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var rootLayout: ScrollView
    private lateinit var btnTheme: ImageView
    private var isDarkMode = false

    private lateinit var inputInstructions: EditText
    private lateinit var inputCountry: EditText
    private lateinit var inputEducation: EditText
    private lateinit var inputCertificate: EditText
    private lateinit var inputSkills: EditText
    private lateinit var btnGetAdvice: Button
    private lateinit var tvAdviceResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ai_career_advisor)

        drawerLayout = findViewById(R.id.drawerLayout)
        rootLayout = findViewById(R.id.scrollViewRoot)
        btnTheme = findViewById(R.id.btnTheme)
        inputCountry = findViewById(R.id.inputCountry)
        inputEducation = findViewById(R.id.inputEducation)
        inputCertificate = findViewById(R.id.inputCertificate)
        btnGetAdvice = findViewById(R.id.btnGetAdvice)
        tvAdviceResult = findViewById(R.id.tvAdviceResult)
        val skillsContainer = findViewById<LinearLayout>(R.id.skillsContainer)
        val btnAddSkill = findViewById<Button>(R.id.btnAddSkill)
        val btnGetAdvice = findViewById<Button>(R.id.btnGetAdvice)
        val tvAdviceResult = findViewById<TextView>(R.id.tvAdviceResult)

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)

        // Dynamic Instruction Field

        // Dynamic Skill Field
        btnAddSkill.setOnClickListener {
            val newSkill = EditText(this)
            newSkill.hint = "Add another skill..."
            newSkill.setPadding(10, 10, 10, 10)
            newSkill.setBackgroundResource(android.R.drawable.edit_text)
            skillsContainer.addView(newSkill)
        }
        // فتح المينيو
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }



        // ---------------- MENU ACTIONS ----------------
        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuCorrectCVLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, CvCorrectionActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.menuInterviewTrainingLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, InterviewPreparationActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuJobMarketAnalyseLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, AICareerAdvisorActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuHelpLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "Help section coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            // Close drawer first
            drawerLayout.closeDrawer(GravityCompat.START)

            // Perform logout after drawer closes
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }

        // Simulated AI Result
        btnGetAdvice.setOnClickListener {
            tvAdviceResult.visibility = TextView.VISIBLE
            tvAdviceResult.text = """
                🔍 Career Analysis Complete:
                • Focus on improving your portfolio visibility.
                • Learn emerging frameworks (e.g., Spring Boot, React).
                • Highlight teamwork & communication in interviews.
                • Target companies hiring in Tunisia tech scene.
            """.trimIndent()
        }

        // ---------------- DARK MODE ----------------
        val darkModeLayout = findViewById<LinearLayout>(R.id.menuDarkModeLayout)
        val darkModeIcon = findViewById<ImageView>(R.id.menuDarkModeIcon)

        darkModeLayout?.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }
        btnTheme.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }

        // Drawer Animation
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                rootLayout.alpha = 1 - slideOffset * 0.5f
                rootLayout.translationX = drawerView.width * slideOffset * 0.3f
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                rootLayout.alpha = 1f
                rootLayout.translationX = 0f
            }
            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    // ----------------- AI ANALYSIS -----------------


    // ----------------- DARK MODE FUNCTION -----------------
    private fun toggleDarkMode(btnTheme: ImageView, darkModeIcon: ImageView?) {
        if (isDarkMode) {
            btnTheme.setImageResource(R.drawable.ic_sun)
            darkModeIcon?.setImageResource(R.drawable.ic_dark_mode)
            rootLayout.setBackgroundColor(Color.parseColor("#F5F7FA"))
            tvAdviceResult.setTextColor(Color.parseColor("#333333"))
            isDarkMode = false
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon)
            darkModeIcon?.setImageResource(R.drawable.ic_sun)
            rootLayout.setBackgroundColor(Color.parseColor("#1F1F1F"))
            tvAdviceResult.setTextColor(Color.parseColor("#FFFFFF"))
            isDarkMode = true
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
