package com.example.jobify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class JobOpportunitiesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobAdapter
    private val jobList = mutableListOf<JobPost>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_opportunities)

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenu = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Drawer item clicks
        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuCorrectCVLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, CvCorrectionActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            // Close drawer first
            drawerLayout.closeDrawer(GravityCompat.START)

            // Perform logout after drawer closes
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }

        findViewById<LinearLayout>(R.id.menuHelpLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<LinearLayout>(R.id.menuJobMarketAnalyseLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, AICareerAdvisorActivity::class.java))
        }
        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_jobs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JobAdapter(jobList)
        recyclerView.adapter = adapter

        loadFakeJobs()
        loadSavedPosts() // ✅ زيدنا هذا السطر
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

    private fun loadFakeJobs() {
        jobList.add(
            JobPost(
                id = 1,
                title = "Product Manager",
                jobPosition = "Senior Product Manager",
                experience = "3–5 years",
                salary = "₹15–25 LPA",
                description = "Lead product strategy and development for our growing e-commerce platform.",
                type = "Full-time",
                createdAt = Date(),
                status = "Active",
                requirements = listOf(
                    "Bachelor’s degree in Business or related field",
                    "Experience with agile product development",
                    "Strong analytical and leadership skills"
                ),
                skills = listOf("Product Strategy", "Analytics", "User Research", "Agile"),
                published = true
            )
        )

        jobList.add(
            JobPost(
                id = 2,
                title = "Mobile Developer",
                jobPosition = "Android Developer",
                experience = "2–4 years",
                salary = "₹10–18 LPA",
                description = "Develop and maintain Android apps with Kotlin and Jetpack Compose.",
                type = "Full-time",
                createdAt = Date(),
                status = "Active",
                requirements = listOf(
                    "Proficient in Kotlin and Android SDK",
                    "Experience with REST APIs",
                    "Knowledge of clean architecture patterns"
                ),
                skills = listOf("Kotlin", "Jetpack Compose", "MVVM", "Firebase"),
                published = true
            )
        )

        adapter.notifyDataSetChanged()
    }

    // ✅ دالة جديدة لاسترجاع الـ posts المتسجّلين
    private fun loadSavedPosts() {
        val sharedPref = getSharedPreferences("job_posts", Context.MODE_PRIVATE)
        val allPosts = sharedPref.all

        for ((id, value) in allPosts) {
            val parts = (value as String).split("|")
            if (parts.size >= 6) {
                val post = JobPost(
                    id = id.toInt(),
                    title = parts[0],
                    jobPosition = parts[1],
                    experience = parts[2],
                    salary = parts[3],
                    description = parts[4],
                    type = parts[5],
                    createdAt = Date(),
                    status = "Active",
                    requirements = emptyList(),
                    skills = emptyList(),
                    published = true
                )
                jobList.add(post)
            }
        }

        adapter.notifyDataSetChanged()
    }
}
