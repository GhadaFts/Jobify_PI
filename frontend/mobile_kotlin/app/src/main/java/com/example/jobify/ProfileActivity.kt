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

    // Image picker
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadProfilePhoto(it)
        }
    }

    // Mock interview notifications (you can load these from backend later)
    private val interviewNotifications = listOf(
        InterviewNotification(
            id = "1",
            companyName = "TekUp",
            interviewDate = "Nov 21, 2025",
            interviewTime = "1:00 AM",
            location = "Online Meeting",
            additionalNotes = "Please have your portfolio ready. Technical assessment will include live coding.",
            duration = "60 mins",
            interviewType = "Online",
            isCompleted = true,
            meetingLink = "https://meet.google.com/tekup-interview-link"
        ),
        InterviewNotification(
            id = "2",
            companyName = "Tech Solutions SARL",
            interviewDate = "Jan 12, 2026",
            interviewTime = "2:30 PM",
            location = "Tech Park, Ariana, Tunisia",
            additionalNotes = "Bring your ID and previous work samples. Dress code: Business casual.",
            duration = "45 mins",
            interviewType = "Local",
            isCompleted = false
        )
    )

    data class InterviewNotification(
        val id: String,
        val companyName: String,
        val interviewDate: String,
        val interviewTime: String,
        val location: String,
        val additionalNotes: String,
        val duration: String,
        val interviewType: String,
        val isCompleted: Boolean,
        val meetingLink: String = ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate_profile)

        sessionManager = SessionManager(this)

        initViews()
        setupListeners()
        loadProfileData()
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
        btnNotification.setOnClickListener { showInterviewNotificationsDialog() }

        // Click on profile image to change
        profileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        setupDrawerMenu()
        setupDarkMode(btnTheme)
    }

    private fun showInterviewNotificationsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_interview_notifications, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val container = dialogView.findViewById<LinearLayout>(R.id.notificationsContainer)

        interviewNotifications.forEach { notification ->
            val notificationCard = layoutInflater.inflate(R.layout.item_interview_notification, container, false)

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

            if (notification.interviewType == "Online") {
                typeIcon.setImageResource(R.drawable.ic_videocam)
            } else {
                typeIcon.setImageResource(R.drawable.ic_location)
            }

            if (notification.interviewType == "Online" && !notification.isCompleted) {
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
            }

            btnDetails.setOnClickListener {
                Toast.makeText(this, "Details for ${notification.companyName}", Toast.LENGTH_SHORT).show()
            }

            container.addView(notificationCard)
        }

        dialog.show()
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