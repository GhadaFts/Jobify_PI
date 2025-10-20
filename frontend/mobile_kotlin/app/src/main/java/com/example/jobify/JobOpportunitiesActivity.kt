package com.example.jobify

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JobOpportunitiesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobAdapter
    private val jobList = mutableListOf<JobPost>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_opportunities)

        // Drawer menu setup
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenu = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        // زر فتح drawer
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // عناصر القائمة
        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            // هنا تقدر تعمل action للـ Home
        }

        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            finish()
        }

        findViewById<LinearLayout>(R.id.menuHelpLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            // هنا action للـ Help
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_jobs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JobAdapter(jobList)
        recyclerView.adapter = adapter

        loadFakeJobs()
    }

    private fun loadFakeJobs() {
        jobList.add(
            JobPost(
                "Product Manager",
                "Flipkart",
                "Bangalore, Karnataka",
                "Full-time",
                "Lead product strategy and development for e-commerce platform.",
                "3–5 years",
                "₹15–25 LPA",
                "45 applied",
                listOf("Product Strategy", "Analytics", "User Research", "Agile")
            )
        )
        adapter.notifyDataSetChanged()
    }
}
