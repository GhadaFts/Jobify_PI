package com.example.jobify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class JobOpportunitiesActivity : BaseDrawerActivity() {

    private lateinit var scrollViewRoot: ScrollView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobAdapter
    private val jobList = mutableListOf<JobPost>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_opportunities)

        initViews()

        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_jobs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JobAdapter(jobList)
        recyclerView.adapter = adapter

        loadFakeJobs()
        loadSavedPosts() // ✅ زيدنا هذا السطر
    }

    private fun initViews() {
        scrollViewRoot = findViewById(R.id.scrollViewRoot)
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
