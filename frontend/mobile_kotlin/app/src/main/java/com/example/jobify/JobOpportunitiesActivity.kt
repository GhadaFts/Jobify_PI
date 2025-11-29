package com.example.jobify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jobify.network.ApiClient
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class JobOpportunitiesActivity : BaseDrawerActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobAdapter
    private val allJobsList = mutableListOf<JobPost>()
    private val appliedJobsList = mutableListOf<JobPost>()
    private var currentTab = 0

    companion object {
        const val APPLICATION_REQUEST_CODE = 1001
        const val TAG = "JobOpportunitiesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_opportunities)

        initViews()
        setupTabLayout()
        setupRecyclerView()

        // Load data sequentially: first jobs, then applications
        loadAllData()
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tab_layout)
        recyclerView = findViewById(R.id.recycler_jobs)
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                when (currentTab) {
                    0 -> displayJobs(allJobsList)
                    1 -> displayJobs(appliedJobsList)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Update tab counts
        updateTabCounts()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JobAdapter(mutableListOf()) { job ->
            // Handle Apply button click
            openApplicationDialog(job)
        }
        recyclerView.adapter = adapter
    }

    /**
     * Load all data sequentially - FIXED VERSION
     * First load jobs, then load applications
     */
    private fun loadAllData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Step 1: Load all jobs first
                val jobsResponse = ApiClient.jobService.getAllJobs()

                if (jobsResponse.isSuccessful) {
                    val jobsData = jobsResponse.body() ?: emptyList()
                    Log.d(TAG, "✅ Loaded ${jobsData.size} jobs from backend")

                    val jobs = jobsData.mapNotNull { jobMap ->
                        try {
                            convertMapToJobPost(jobMap)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to convert job: ${e.message}")
                            null
                        }
                    }

                    withContext(Dispatchers.Main) {
                        allJobsList.clear()
                        allJobsList.addAll(jobs)
                        displayJobs(allJobsList)
                        updateTabCounts()
                    }

                    // Step 2: Now load applications (after jobs are loaded)
                    loadAppliedJobsInternal()
                } else {
                    Log.e(TAG, "❌ Failed to load jobs: ${jobsResponse.code()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@JobOpportunitiesActivity,
                            "Failed to load jobs",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Fallback to fake jobs
                        loadFakeJobs()
                        displayJobs(allJobsList)
                        updateTabCounts()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading jobs: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@JobOpportunitiesActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Fallback to fake jobs
                    loadFakeJobs()
                    displayJobs(allJobsList)
                    updateTabCounts()
                }
            }
        }
    }

    /**
     * Convert backend map to JobPost object
     */
    private fun convertMapToJobPost(map: Map<String, Any>): JobPost {
        return JobPost(
            id = (map["id"] as? Number)?.toInt() ?: 0,
            title = map["title"] as? String ?: "Unknown",
            jobPosition = map["jobPosition"] as? String ?: "",
            experience = map["experience"] as? String ?: "",
            salary = map["salary"] as? String ?: "",
            description = map["description"] as? String ?: "",
            type = map["type"] as? String ?: "Full-time",
            createdAt = parseDate(map["createdAt"] as? String),
            status = map["status"] as? String ?: "Active",
            requirements = (map["requirements"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            skills = (map["skills"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            published = map["published"] as? Boolean ?: true
        )
    }

    /**
     * Parse date string to Date object
     */
    private fun parseDate(dateStr: String?): Date {
        if (dateStr == null) return Date()
        return try {
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    /**
     * Fallback: Load fake jobs if backend is unavailable
     */
    private fun loadFakeJobs() {
        allJobsList.clear()

        allJobsList.add(
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
                    "Bachelor's degree in Business or related field",
                    "Experience with agile product development",
                    "Strong analytical and leadership skills"
                ),
                skills = listOf("Product Strategy", "Analytics", "User Research", "Agile"),
                published = true
            )
        )

        allJobsList.add(
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
    }

    /**
     * Internal method to load applied jobs (called after jobs are loaded)
     * This must be called from IO dispatcher
     */
    private suspend fun loadAppliedJobsInternal() {
        try {
            val response = ApiClient.applicationService.getMyApplications()

            if (response.isSuccessful) {
                val applicationsData = response.body() ?: emptyList()
                Log.d(TAG, "✅ Loaded ${applicationsData.size} applications")

                // For each application, find the corresponding job
                val appliedJobs = mutableListOf<JobPost>()

                for (appMap in applicationsData) {
                    val jobOfferId = (appMap["jobOfferId"] as? Number)?.toInt()
                    Log.d(TAG, "Looking for job with ID: $jobOfferId in ${allJobsList.size} jobs")

                    if (jobOfferId != null) {
                        // Find the job in allJobsList
                        val job = allJobsList.find { it.id == jobOfferId }
                        if (job != null) {
                            Log.d(TAG, "✅ Found job: ${job.title}")
                            appliedJobs.add(job.copy(status = "Applied"))
                        } else {
                            Log.w(TAG, "⚠️ Job with ID $jobOfferId not found in allJobsList")
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    appliedJobsList.clear()
                    appliedJobsList.addAll(appliedJobs)
                    Log.d(TAG, "Applied jobs list size: ${appliedJobsList.size}")
                    updateTabCounts()

                    if (currentTab == 1) {
                        displayJobs(appliedJobsList)
                    }
                }
            } else {
                Log.e(TAG, "❌ Failed to load applications: ${response.code()}")
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error body: $errorBody")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading applications: ${e.message}", e)
        }
    }

    /**
     * Public method to reload applied jobs (called after submitting an application)
     */
    private fun loadAppliedJobs() {
        lifecycleScope.launch(Dispatchers.IO) {
            loadAppliedJobsInternal()
        }
    }

    private fun displayJobs(jobs: List<JobPost>) {
        Log.d(TAG, "Displaying ${jobs.size} jobs")
        adapter.updateJobs(jobs)
    }

    private fun openApplicationDialog(job: JobPost) {
        val intent = Intent(this, ApplicationDialogActivity::class.java)
        intent.putExtra(ApplicationDialogActivity.EXTRA_JOB_POST, job)
        startActivityForResult(intent, APPLICATION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == APPLICATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val success = data?.getBooleanExtra("success", false) ?: false

            if (success) {
                Toast.makeText(this, "Application submitted successfully!", Toast.LENGTH_SHORT).show()

                // Reload applied jobs
                loadAppliedJobs()

                // If currently viewing applied jobs, refresh the list
                if (currentTab == 1) {
                    displayJobs(appliedJobsList)
                }
            }
        }
    }

    private fun updateTabCounts() {
        val allJobsTab = tabLayout.getTabAt(0)
        val appliedTab = tabLayout.getTabAt(1)

        allJobsTab?.text = "All jobs (${allJobsList.size})"
        appliedTab?.text = "Applied (${appliedJobsList.size})"
    }
}