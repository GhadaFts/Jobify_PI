package com.example.jobify

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.jobify.network.*
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : BaseDrawerActivity() {

    private lateinit var scrollViewRoot: ScrollView
    private lateinit var sessionManager: SessionManager

    // UI Elements
    private lateinit var txtName: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtBio: TextView
    private lateinit var btnNotification: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var experienceContainer: LinearLayout
    private lateinit var txtNoExperience: TextView
    private lateinit var educationContainer: LinearLayout
    private lateinit var txtNoEducation: TextView
    private lateinit var skillsContainer: FlexboxLayout
    private lateinit var txtNoSkill: TextView
    private lateinit var progressBar: ProgressBar

    // Profile data
    private var currentProfile: JobSeekerProfile? = null
    private var isDarkMode = false
    private lateinit var rootLayout: ScrollView
    private var isLoading = false
    
    // Interview data - loaded from backend
    private var interviewNotifications: MutableList<InterviewNotification> = mutableListOf()

    // Image picker
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadProfilePhoto(it)
        }
    }

    data class InterviewNotification(
        val id: Long,
        val companyName: String,
        val interviewDate: String, // ISO format from backend
        val interviewTime: String,
        val location: String,
        val additionalNotes: String,
        val duration: String,
        val interviewType: String,
        val isCompleted: Boolean = false,
        val meetingLink: String = ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate_profile)

        sessionManager = SessionManager(this)

        initViews()
        setupListeners()
        loadProfileData()
        loadInterviewNotifications()
    }

    private fun initViews() {
        scrollViewRoot = findViewById(R.id.scrollViewRoot)
        rootLayout = findViewById(R.id.scrollViewRoot)
        progressBar = findViewById(R.id.progressBar)

        txtName = findViewById(R.id.txtName)
        txtCountry = findViewById(R.id.txtCountry)
        txtBio = findViewById(R.id.txtBio)
        profileImage = findViewById(R.id.profileImage)
        btnNotification = findViewById(R.id.btnNotification)

        // Experience
        experienceContainer = findViewById(R.id.experienceItemsContainer)
        txtNoExperience = findViewById(R.id.txtNoExperience)

        // Education
        educationContainer = findViewById(R.id.educationItemsContainer)
        txtNoEducation = findViewById(R.id.txtNoEducation)

        // Skills
        skillsContainer = findViewById(R.id.skillsContainer)
        txtNoSkill = findViewById(R.id.txtNoSkill)
    }

    private fun loadProfileData() {
        isLoading = true
        showLoading(true)

        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            showError("Not authenticated. Please login again.")
            navigateToLogin()
            return
        }

        ApiClient.userService.getJobSeekerProfile("Bearer $token").enqueue(object : Callback<JobSeekerProfile> {
            override fun onResponse(call: Call<JobSeekerProfile>, response: Response<JobSeekerProfile>) {
                isLoading = false
                showLoading(false)

                if (response.isSuccessful) {
                    currentProfile = response.body()
                    currentProfile?.let {
                        if (it.role.lowercase().replace("_", "") == "jobseeker") {
                            displayProfile(it)
                        } else {
                            showError("This page is only for job seekers")
                        }
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            showError("Session expired. Please login again.")
                            navigateToLogin()
                        }
                        404 -> showError("Profile not found")
                        else -> showError("Failed to load profile: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<JobSeekerProfile>, t: Throwable) {
                isLoading = false
                showLoading(false)
                Log.e("ProfileActivity", "Failed to load profile", t)
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun displayProfile(profile: JobSeekerProfile) {
        txtName.text = profile.fullName
        txtCountry.text = profile.nationality ?: "Not specified"
        txtBio.text = profile.description ?: "No description available"

        // Load profile photo
        if (!profile.photo_profil.isNullOrEmpty()) {
            val imageUrl = if (profile.photo_profil.startsWith("http")) {
                profile.photo_profil
            } else {
                "http://10.0.2.2:8888/auth-service${profile.photo_profil}"
            }

            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.ic_user_placeholder)
        }

        // Display basic info
        findViewById<TextView>(R.id.txtFullName).text = "Full Name: ${profile.fullName}"
        findViewById<TextView>(R.id.txtTitle).text = "Title: ${profile.title ?: "Not specified"}"
        findViewById<TextView>(R.id.txtRole).text = "Role: ${profile.role}"
        findViewById<TextView>(R.id.txtBirthDate).text = "Birth Date: ${profile.date_of_birth ?: "Not specified"}"
        findViewById<TextView>(R.id.txtGender).text = "Gender: ${profile.gender ?: "Not specified"}"

        // Display contact info
        findViewById<TextView>(R.id.txtEmail).text = "Email: ${profile.email}"
        findViewById<TextView>(R.id.txtPhone).text = "Phone: ${profile.phone_number ?: "Not specified"}"
        findViewById<TextView>(R.id.txtNationality).text = "Nationality: ${profile.nationality ?: "Not specified"}"
        findViewById<TextView>(R.id.txtTwitter).text = "Twitter: ${profile.twitter_link ?: "Not specified"}"
        findViewById<TextView>(R.id.txtGithub).text = "GitHub: ${profile.github_link ?: "Not specified"}"
        findViewById<TextView>(R.id.txtFacebook).text = "Facebook: ${profile.facebook_link ?: "Not specified"}"
        findViewById<TextView>(R.id.txtWebsite).text = "Website: ${profile.web_link ?: "Not specified"}"

        // Display experience, education, and skills
        displayExperience(profile.experience ?: emptyList())
        displayEducation(profile.education ?: emptyList())
        displaySkills(profile.skills ?: emptyList())
    }

    private fun displayExperience(experiences: List<Experience>) {
        experienceContainer.removeAllViews()

        if (experiences.isEmpty()) {
            txtNoExperience.visibility = View.VISIBLE
        } else {
            txtNoExperience.visibility = View.GONE

            experiences.forEach { exp ->
                val itemView = layoutInflater.inflate(R.layout.item_experience, experienceContainer, false)

                itemView.findViewById<TextView>(R.id.tvJobPosition).text = exp.position
                itemView.findViewById<TextView>(R.id.tvCompanyName).text = exp.company
                itemView.findViewById<TextView>(R.id.tvAddress).text = exp.description
                itemView.findViewById<TextView>(R.id.tvWorkDuration).text = "${exp.startDate} - ${exp.endDate}"

                // Hide edit/delete buttons in view mode
                itemView.findViewById<ImageView>(R.id.btnEditExperience).visibility = View.GONE
                itemView.findViewById<Button>(R.id.btnDeleteExperience).visibility = View.GONE

                experienceContainer.addView(itemView)
            }
        }
    }

    private fun displayEducation(educations: List<Education>) {
        educationContainer.removeAllViews()

        if (educations.isEmpty()) {
            txtNoEducation.visibility = View.VISIBLE
        } else {
            txtNoEducation.visibility = View.GONE

            educations.forEach { edu ->
                val itemView = layoutInflater.inflate(R.layout.item_education, educationContainer, false)

                itemView.findViewById<TextView>(R.id.tvDegree).text = edu.degree
                itemView.findViewById<TextView>(R.id.tvField).text = edu.field
                itemView.findViewById<TextView>(R.id.tvUniversity).text = edu.school
                itemView.findViewById<TextView>(R.id.tvGraduationDate).text = edu.graduationDate

                // Hide edit/delete buttons in view mode
                itemView.findViewById<ImageView>(R.id.btnEditEducation).visibility = View.GONE
                itemView.findViewById<Button>(R.id.btnRemoveEducation).visibility = View.GONE

                educationContainer.addView(itemView)
            }
        }
    }

    private fun displaySkills(skills: List<String>) {
        skillsContainer.removeAllViews()

        if (skills.isEmpty()) {
            txtNoSkill.visibility = View.VISIBLE
        } else {
            txtNoSkill.visibility = View.GONE

            skills.forEach { skill ->
                val tv = TextView(this)
                tv.text = skill
                tv.setTextColor(Color.WHITE)
                tv.textSize = 14f
                tv.setPadding(32, 16, 32, 16)
                tv.background = ContextCompat.getDrawable(this, R.drawable.bg_item_profile)

                val randomColor = getRandomColor()
                (tv.background as GradientDrawable).setColor(randomColor)

                val params = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(12, 12, 12, 12)
                tv.layoutParams = params

                skillsContainer.addView(tv)
            }
        }
    }

    /**
     * Load interview notifications from backend following the Angular workflow:
     * 1. Get all upcoming interviews for job seeker
     * 2. For each interview, fetch the application details
     * 3. For each application, fetch the job details to get company name
     * 4. Map all data into InterviewNotification format
     * 5. Display in dialog with proper formatting
     */
    private fun loadInterviewNotifications() {
        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            Log.w("ProfileActivity", "No token available for loading interviews")
            return
        }

        // Clear previous interviews to avoid duplicates
        interviewNotifications.clear()

        // Step 1: Get all upcoming interviews
        ApiClient.interviewService.getMyUpcomingInterviews().enqueue(object : Callback<List<InterviewResponseDTO>> {
            override fun onResponse(call: Call<List<InterviewResponseDTO>>, response: Response<List<InterviewResponseDTO>>) {
                if (!response.isSuccessful || response.body() == null) {
                    Log.e("ProfileActivity", "Failed to load interviews: ${response.code()}")
                    interviewNotifications.clear()
                    return
                }

                val interviews = response.body() ?: emptyList()
                Log.d("ProfileActivity", "Loaded ${interviews.size} upcoming interviews")

                if (interviews.isEmpty()) {
                    interviewNotifications.clear()
                    return
                }

                // Step 2 & 3: For each interview, fetch application and job details
                var completedRequests = 0

                interviews.forEach { interview ->
                    // Fetch application details
                    ApiClient.applicationService.getApplicationByIdCall(interview.applicationId)
                        .enqueue(object : Callback<Map<String, Any>> {
                            override fun onResponse(
                                call: Call<Map<String, Any>>,
                                response: Response<Map<String, Any>>
                            ) {
                                if (!response.isSuccessful || response.body() == null) {
                                    Log.e("ProfileActivity", "Failed to load application: ${interview.applicationId}")
                                    completedRequests++
                                    checkAllInterviewsLoaded(completedRequests, interviews.size)
                                    return
                                }

                                val application = response.body()!!
                                // Handle jobOfferId - could be String, Long, or Double
                                val jobOfferId = when (val id = application["jobOfferId"]) {
                                    is String -> id
                                    is Number -> id.toLong().toString()
                                    else -> return@onResponse
                                }
                                
                                Log.d("ProfileActivity", "Extracted jobOfferId: $jobOfferId from application")

                                // Fetch job details to get company name
                                ApiClient.jobService.getJobById(jobOfferId)
                                    .enqueue(object : Callback<Map<String, Any>> {
                                        override fun onResponse(
                                            call: Call<Map<String, Any>>,
                                            response: Response<Map<String, Any>>
                                        ) {
                                            if (response.isSuccessful && response.body() != null) {
                                                val job = response.body()!!
                                                val companyName = job["company"] as? String ?: "Unknown Company"

                                                // Step 4: Map data into InterviewNotification
                                                val notification = mapInterviewToNotification(
                                                    interview = interview,
                                                    companyName = companyName
                                                )

                                                interviewNotifications.add(notification)
                                                Log.d("ProfileActivity", "Added notification for $companyName")
                                            } else {
                                                Log.e("ProfileActivity", "Failed to load job: $jobOfferId")
                                            }

                                            completedRequests++
                                            checkAllInterviewsLoaded(completedRequests, interviews.size)
                                        }

                                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                            Log.e("ProfileActivity", "Failed to fetch job details", t)
                                            completedRequests++
                                            checkAllInterviewsLoaded(completedRequests, interviews.size)
                                        }
                                    })
                            }

                            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                Log.e("ProfileActivity", "Failed to fetch application details", t)
                                completedRequests++
                                checkAllInterviewsLoaded(completedRequests, interviews.size)
                            }
                        })
                }
            }

            override fun onFailure(call: Call<List<InterviewResponseDTO>>, t: Throwable) {
                Log.e("ProfileActivity", "Failed to load interviews", t)
                interviewNotifications.clear()
            }
        })
    }

    /**
     * Check if all interview data has been loaded, to avoid race conditions
     */
    private fun checkAllInterviewsLoaded(completed: Int, total: Int) {
        if (completed == total) {
            Log.d("ProfileActivity", "All interviews loaded: ${interviewNotifications.size}")
            // Sort by date, most recent first
            interviewNotifications.sortByDescending { it.interviewDate }
        }
    }

    /**
     * Map InterviewResponseDTO to InterviewNotification with formatted dates
     * Following the same logic as Angular's job-seeker-sidebar.ts
     */
    private fun mapInterviewToNotification(
        interview: InterviewResponseDTO,
        companyName: String
    ): InterviewNotification {
        val isoDateTime = interview.scheduledDate // ISO format from backend
        val interviewDate = formatInterviewDate(isoDateTime)
        val interviewTime = formatInterviewTime(isoDateTime)

        // Determine if interview is completed based on date
        val isCompleted = isInterviewCompleted(isoDateTime)

        return InterviewNotification(
            id = interview.id,
            companyName = companyName,
            interviewDate = interviewDate, // Formatted: "Jan 15, 2024"
            interviewTime = interviewTime, // Formatted: "02:30 PM"
            location = interview.location ?: "Not specified",
            additionalNotes = interview.notes ?: "",
            duration = "${interview.duration} mins",
            interviewType = getInterviewTypeDisplay(interview),
            isCompleted = isCompleted,
            meetingLink = interview.meetingLink ?: ""
        )
    }

    /**
     * Check if interview is in the past
     */
    private fun isInterviewCompleted(isoDateTime: String): Boolean {
        return try {
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val interviewDate = dateFormat.parse(isoDateTime) ?: return false
            val now = Calendar.getInstance().time
            interviewDate.before(now)
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error parsing date: $isoDateTime", e)
            false
        }
    }

    /**
     * Format ISO datetime to display date: "Jan 15, 2024"
     */
    private fun formatInterviewDate(isoDateTime: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(isoDateTime) ?: return isoDateTime

            val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error formatting date: $isoDateTime", e)
            isoDateTime
        }
    }

    /**
     * Format ISO datetime to display time: "02:30 PM"
     */
    private fun formatInterviewTime(isoDateTime: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(isoDateTime) ?: return isoDateTime

            val outputFormat = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error formatting time: $isoDateTime", e)
            isoDateTime
        }
    }

    /**
     * Get human-readable interview type from InterviewType enum
     */
    private fun getInterviewTypeDisplay(interview: InterviewResponseDTO): String {
        return when (interview.interviewType.uppercase()) {
            "REMOTE", "VIDEO", "ONLINE" -> "Online Interview"
            "ON_SITE", "IN_PERSON", "LOCAL" -> "Local Interview"
            else -> interview.interviewType
        }
    }

    private fun uploadProfilePhoto(uri: Uri) {
        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            showError("Not authenticated")
            return
        }

        try {
            // Create a temporary file from URI
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "profile_photo.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            // Create multipart request
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

            showLoading(true)

            ApiClient.userService.uploadProfilePhoto("Bearer $token", multipartBody)
                .enqueue(object : Callback<PhotoUploadResponse> {
                    override fun onResponse(
                        call: Call<PhotoUploadResponse>,
                        response: Response<PhotoUploadResponse>
                    ) {
                        showLoading(false)

                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Photo uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Reload profile
                            loadProfileData()
                        } else {
                            showError("Failed to upload photo: ${response.message()}")
                        }

                        file.delete()
                    }

                    override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                        showLoading(false)
                        Log.e("ProfileActivity", "Photo upload failed", t)
                        showError("Failed to upload photo: ${t.message}")
                        file.delete()
                    }
                })
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error preparing photo upload", e)
            showError("Error: ${e.message}")
        }
    }

    private fun setupListeners() {
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnTheme = findViewById<ImageView>(R.id.btnTheme)
        val btnEditName = findViewById<ImageView>(R.id.btnEditName)

        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        btnEditName.setOnClickListener {
            // Open edit profile activity or show edit dialog
            Toast.makeText(this, "Edit profile feature - coming soon", Toast.LENGTH_SHORT).show()
        }
        btnNotification.setOnClickListener { 
            // Reload interviews before showing dialog to ensure latest data
            loadInterviewNotifications()
            // Longer delay to allow nested API calls to complete (3 calls per interview)
            Handler(Looper.getMainLooper()).postDelayed({
                showInterviewNotificationsDialog()
            }, 2000)  // 2 second delay for nested API calls
        }

        // Click on profile image to change
        profileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        setupDrawerMenu()
        setupDarkMode(btnTheme)
    }

    private fun showInterviewNotificationsDialog() {
        Log.d("ProfileActivity", "showInterviewNotificationsDialog called with ${interviewNotifications.size} interviews")
        
        val dialogView = layoutInflater.inflate(R.layout.dialog_interview_notifications, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val container = dialogView.findViewById<LinearLayout>(R.id.notificationsContainer)
        
        if (container == null) {
            Log.e("ProfileActivity", "notificationsContainer is null in dialog layout")
            Toast.makeText(this, "Error loading dialog", Toast.LENGTH_SHORT).show()
            return
        }

        // Clear any existing views
        container.removeAllViews()

        // Check if there are any interviews
        if (interviewNotifications.isEmpty()) {
            Log.d("ProfileActivity", "No interviews to display")
            val emptyView = TextView(this)
            emptyView.text = "No upcoming interviews scheduled"
            emptyView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            emptyView.setPadding(32, 64, 32, 64)
            emptyView.setTextColor(Color.parseColor("#999999"))
            emptyView.textSize = 16f
            container.addView(emptyView)
        } else {
            Log.d("ProfileActivity", "Displaying ${interviewNotifications.size} interviews")
            // Display all interview notifications
            interviewNotifications.forEach { notification ->
                Log.d("ProfileActivity", "Adding notification: ${notification.companyName}")
                val notificationCard = layoutInflater.inflate(R.layout.item_interview_notification, container, false)

                try {
                    notificationCard.findViewById<TextView>(R.id.tvCompanyName).text = notification.companyName
                    notificationCard.findViewById<TextView>(R.id.tvDuration).text = notification.duration
                    notificationCard.findViewById<TextView>(R.id.tvType).text = notification.interviewType
                    notificationCard.findViewById<TextView>(R.id.tvDate).text = notification.interviewDate
                    notificationCard.findViewById<TextView>(R.id.tvTime).text = notification.interviewTime
                    notificationCard.findViewById<TextView>(R.id.tvLocation).text = notification.location
                    notificationCard.findViewById<TextView>(R.id.tvNotes).text = notification.additionalNotes

                    val btnJoin = notificationCard.findViewById<Button>(R.id.btnJoin)
                    val tvCompleted = notificationCard.findViewById<TextView>(R.id.tvCompleted)
                    val btnDetails = notificationCard.findViewById<Button>(R.id.btnDetails)
                    val typeIcon = notificationCard.findViewById<ImageView>(R.id.iconType)

                    if (notification.isCompleted) {
                        btnJoin.visibility = View.GONE
                        tvCompleted.visibility = View.VISIBLE
                        tvCompleted.text = "Completed"
                    } else {
                        btnJoin.visibility = View.VISIBLE
                        tvCompleted.visibility = View.GONE
                    }

                    // Determine icon based on interview type
                    if (notification.interviewType.contains("Online", ignoreCase = true)) {
                        typeIcon.setImageResource(R.drawable.ic_videocam)
                    } else {
                        typeIcon.setImageResource(R.drawable.ic_location)
                    }

                    // Set up join button for online interviews
                    if (notification.interviewType.contains("Online", ignoreCase = true) && !notification.isCompleted) {
                        if (notification.meetingLink.isNotEmpty()) {
                            btnJoin.setOnClickListener {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notification.meetingLink))
                                    startActivity(intent)
                                    dialog.dismiss()
                                } catch (e: Exception) {
                                    Toast.makeText(this, "Cannot open link: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            btnJoin.isEnabled = false
                            btnJoin.alpha = 0.5f
                            btnJoin.text = "No link"
                        }
                    } else {
                        btnJoin.isEnabled = false
                        btnJoin.alpha = 0.5f
                    }

                    // Show interview details
                    btnDetails.setOnClickListener {
                        showInterviewDetailsDialog(notification)
                    }

                    container.addView(notificationCard)
                    Log.d("ProfileActivity", "Successfully added notification card")
                } catch (e: Exception) {
                    Log.e("ProfileActivity", "Error adding notification card", e)
                }
            }
        }

        dialog.show()
    }

    /**
     * Show detailed view of an interview
     */
    private fun showInterviewDetailsDialog(notification: InterviewNotification) {
        val details = """
Interview Details

Company: ${notification.companyName}
Date: ${notification.interviewDate}
Time: ${notification.interviewTime}
Duration: ${notification.duration}
Type: ${notification.interviewType}
Location: ${notification.location}
${if (notification.meetingLink.isNotEmpty()) "Meeting Link: ${notification.meetingLink}\n" else ""}
Notes: ${if (notification.additionalNotes.isNotEmpty()) notification.additionalNotes else "No additional notes"}
Status: ${if (notification.isCompleted) "Completed" else "Scheduled"}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("${notification.companyName} - Interview")
            .setMessage(details)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun getRandomColor(): Int {
        val colors = listOf(
            Color.parseColor("#FF6B6B"),
            Color.parseColor("#4ECDC4"),
            Color.parseColor("#45B7D1"),
            Color.parseColor("#FFA07A"),
            Color.parseColor("#98D8C8"),
            Color.parseColor("#6C5CE7"),
            Color.parseColor("#A29BFE"),
            Color.parseColor("#FD79A8")
        )
        return colors.random()
    }

    private fun setupDrawerMenu() {
        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }
    }

    private fun setupDarkMode(btnTheme: ImageView) {
        val darkModeLayout = findViewById<LinearLayout>(R.id.menuDarkModeLayout)
        val darkModeIcon = findViewById<ImageView>(R.id.menuDarkModeIcon)

        darkModeLayout.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }
        btnTheme.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }
    }

    private fun toggleDarkMode(btnTheme: ImageView, darkModeIcon: ImageView) {
        if (isDarkMode) {
            btnTheme.setImageResource(R.drawable.ic_sun)
            darkModeIcon.setImageResource(R.drawable.ic_dark_mode)
            rootLayout.setBackgroundColor(Color.parseColor("#F5F7FA"))
            txtName.setTextColor(Color.parseColor("#1F4E5F"))
            txtCountry.setTextColor(Color.parseColor("#6E7A8A"))
            txtBio.setTextColor(Color.parseColor("#1F4E5F"))
            isDarkMode = false
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon)
            darkModeIcon.setImageResource(R.drawable.ic_sun)
            rootLayout.setBackgroundColor(Color.parseColor("#1F1F1F"))
            txtName.setTextColor(Color.parseColor("#FFFFFF"))
            txtCountry.setTextColor(Color.parseColor("#BBBBBB"))
            txtBio.setTextColor(Color.parseColor("#FFFFFF"))
            isDarkMode = true
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToLogin() {
        sessionManager.clearSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finishAffinity()
    }

    private fun performLogout() {
        try {
            sessionManager.clearSession()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finishAffinity()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Logout error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}