package com.example.jobify

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide
import com.example.jobify.network.*
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class RecruiterProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView
    private lateinit var btnEdit: ImageView
    private lateinit var btnEditPhoto: ImageView
    private lateinit var menuProfile: LinearLayout
    private lateinit var menuLogout: LinearLayout
    private lateinit var btnSave: Button

    // Views
    private lateinit var companyImage: ImageView
    private lateinit var txtRecruiterName: TextView
    private lateinit var txtCountry: TextView
    private lateinit var txtPostsCount: TextView
    private lateinit var txtEmployeesCount: TextView
    private lateinit var txtBio: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtDomaine: TextView
    private lateinit var txtAddress: TextView
    private lateinit var phoneLayout: LinearLayout
    private lateinit var domaineLayout: LinearLayout
    private lateinit var addressLayout: LinearLayout
    private lateinit var servicesContainer: FlexboxLayout
    private lateinit var txtNoService: TextView
    private lateinit var btnAddService: ImageView
    private lateinit var progressBar: ProgressBar

    // Social links
    private lateinit var socialLinksView: LinearLayout
    private lateinit var btnTwitter: ImageView
    private lateinit var btnWeb: ImageView
    private lateinit var btnGithub: ImageView
    private lateinit var btnFacebook: ImageView

    // Session manager
    private lateinit var sessionManager: SessionManager

    // Edit mode flag
    private var isEditMode = false
    private var isLoading = false

    // Data - Store the full profile
    private var currentProfile: RecruiterProfile? = null
    private var selectedPhotoUri: Uri? = null

    // Image picker
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            // Display preview
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(companyImage)

            // Upload immediately
            uploadProfilePhoto(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recruiter_profile)

        sessionManager = SessionManager(this)

        initializeViews()
        loadRecruiterData()
        setupDrawerMenu()
        setupEditMode()
    }

    private fun initializeViews() {
        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        btnEdit = findViewById(R.id.btnEdit)
        btnEditPhoto = findViewById(R.id.btnEditPhoto)
        menuProfile = findViewById(R.id.menuProfileLayout)
        menuLogout = findViewById(R.id.menuLogoutLayout)
        btnSave = findViewById(R.id.btnSave)

        // Profile views
        companyImage = findViewById(R.id.companyImage)
        txtRecruiterName = findViewById(R.id.txtRecruiterName)
        txtCountry = findViewById(R.id.txtCountry)
        txtPostsCount = findViewById(R.id.txtPostsCount)
        txtEmployeesCount = findViewById(R.id.txtEmployeesCount)
        txtBio = findViewById(R.id.txtBio)
        txtPhone = findViewById(R.id.txtPhone)
        txtDomaine = findViewById(R.id.txtDomaine)
        txtAddress = findViewById(R.id.txtAddress)
        phoneLayout = findViewById(R.id.phoneLayout)
        domaineLayout = findViewById(R.id.domaineLayout)
        addressLayout = findViewById(R.id.addressLayout)
        servicesContainer = findViewById(R.id.servicesContainer)
        txtNoService = findViewById(R.id.txtNoService)
        btnAddService = findViewById(R.id.btnAddService)
        progressBar = findViewById(R.id.progressBar)

        // Social links
        socialLinksView = findViewById(R.id.socialLinksView)
        btnTwitter = findViewById(R.id.btnTwitter)
        btnWeb = findViewById(R.id.btnWeb)
        btnGithub = findViewById(R.id.btnGithub)
        btnFacebook = findViewById(R.id.btnFacebook)
    }

    private fun setupDrawerMenu() {
        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuLogout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }

        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }
    }

    private fun setupEditMode() {
        btnEdit.setOnClickListener {
            toggleEditMode()
        }

        btnSave.setOnClickListener {
            saveChanges()
        }

        btnEditPhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnAddService.setOnClickListener {
            showAddServiceDialog()
        }

        // Social links clicks
        btnTwitter.setOnClickListener { openLink(currentProfile?.twitter_link) }
        btnWeb.setOnClickListener { openLink(currentProfile?.web_link) }
        btnGithub.setOnClickListener { openLink(currentProfile?.github_link) }
        btnFacebook.setOnClickListener { openLink(currentProfile?.facebook_link) }
    }

    private fun loadRecruiterData() {
        isLoading = true
        showLoading(true)

        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            showError("Not authenticated. Please login again.")
            navigateToLogin()
            return
        }

        // Load profile data
        ApiClient.userService.getUserProfile("Bearer $token").enqueue(object : Callback<RecruiterProfile> {
            override fun onResponse(call: Call<RecruiterProfile>, response: Response<RecruiterProfile>) {
                isLoading = false
                showLoading(false)

                if (response.isSuccessful) {
                    currentProfile = response.body()
                    currentProfile?.let {
                        if (it.role.lowercase().replace("_", "") == "recruiter") {
                            displayRecruiterData(it)
                            // Load job posts count
                            loadJobPostsCount()
                        } else {
                            showError("This page is only for recruiters")
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

            override fun onFailure(call: Call<RecruiterProfile>, t: Throwable) {
                isLoading = false
                showLoading(false)
                Log.e("RecruiterProfile", "Failed to load profile", t)
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun loadJobPostsCount() {
        // Using coroutine to call suspend function
        lifecycleScope.launch {
            try {
                val response = ApiClient.jobService.getMyJobs()
                if (response.isSuccessful) {
                    val jobs = response.body()
                    val jobCount = jobs?.size ?: 0

                    // Update UI on main thread
                    runOnUiThread {
                        txtPostsCount.text = jobCount.toString()
                    }

                    Log.d("RecruiterProfile", "Loaded $jobCount job posts")
                } else {
                    Log.e("RecruiterProfile", "Failed to load jobs: ${response.code()}")
                    // Keep default count (0) if fails
                    runOnUiThread {
                        txtPostsCount.text = "0"
                    }
                }
            } catch (e: Exception) {
                Log.e("RecruiterProfile", "Error loading job posts", e)
                // Keep default count (0) if error
                runOnUiThread {
                    txtPostsCount.text = "0"
                }
            }
        }
    }

    private fun displayRecruiterData(profile: RecruiterProfile) {
        txtRecruiterName.text = profile.fullName
        txtCountry.text = profile.nationality ?: "Not specified"
        txtEmployeesCount.text = profile.employees_number?.toString() ?: "0"
        txtPostsCount.text = "0" // Will be updated by loadJobPostsCount()
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
                .into(companyImage)
        } else {
            companyImage.setImageResource(R.drawable.ic_user_placeholder)
        }

        // Phone
        if (!profile.phone_number.isNullOrEmpty()) {
            txtPhone.text = profile.phone_number
            phoneLayout.visibility = View.VISIBLE
        } else {
            phoneLayout.visibility = View.GONE
        }

        // Domaine
        if (!profile.domaine.isNullOrEmpty()) {
            txtDomaine.text = profile.domaine
            domaineLayout.visibility = View.VISIBLE
        } else {
            domaineLayout.visibility = View.GONE
        }

        // Address
        if (!profile.companyAddress.isNullOrEmpty()) {
            txtAddress.text = profile.companyAddress
            addressLayout.visibility = View.VISIBLE
        } else {
            addressLayout.visibility = View.GONE
        }

        // Social links
        val hasLinks = !profile.twitter_link.isNullOrEmpty() ||
                !profile.web_link.isNullOrEmpty() ||
                !profile.github_link.isNullOrEmpty() ||
                !profile.facebook_link.isNullOrEmpty()

        socialLinksView.visibility = if (hasLinks) View.VISIBLE else View.GONE
        btnTwitter.visibility = if (!profile.twitter_link.isNullOrEmpty()) View.VISIBLE else View.GONE
        btnWeb.visibility = if (!profile.web_link.isNullOrEmpty()) View.VISIBLE else View.GONE
        btnGithub.visibility = if (!profile.github_link.isNullOrEmpty()) View.VISIBLE else View.GONE
        btnFacebook.visibility = if (!profile.facebook_link.isNullOrEmpty()) View.VISIBLE else View.GONE

        // Services
        displayServices(profile.service ?: emptyList())
    }

    private fun displayServices(services: List<String>) {
        servicesContainer.removeAllViews()

        if (services.isEmpty()) {
            txtNoService.visibility = View.VISIBLE
            servicesContainer.visibility = View.GONE
        } else {
            txtNoService.visibility = View.GONE
            servicesContainer.visibility = View.VISIBLE

            services.forEach { service ->
                val chip = Chip(this).apply {
                    text = service
                    isClickable = false
                    isCheckable = false
                    chipBackgroundColor = android.content.res.ColorStateList.valueOf(Color.parseColor("#1F4E5F"))
                    setTextColor(Color.WHITE)

                    if (isEditMode) {
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            removeService(service)
                        }
                    } else {
                        isCloseIconVisible = false
                    }
                }
                servicesContainer.addView(chip)
            }
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            btnEdit.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            btnEditPhoto.visibility = View.VISIBLE
            btnAddService.visibility = View.VISIBLE
            btnSave.visibility = View.VISIBLE
            showEditDialog()
        } else {
            btnEdit.setImageResource(R.drawable.ic_edit)
            btnEditPhoto.visibility = View.GONE
            btnAddService.visibility = View.GONE
            btnSave.visibility = View.GONE
            currentProfile?.let { displayServices(it.service ?: emptyList()) }
        }
    }

    private fun showEditDialog() {
        val profile = currentProfile ?: return

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)

        val edtName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtFullName)
        val edtPhone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtPhone)
        val edtNationality = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtNationality)
        val edtAddress = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtAddress)
        val edtDomaine = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtDomaine)
        val edtEmployees = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtEmployees)
        val edtDescription = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtDescription)
        val edtTwitter = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtTwitter)
        val edtWeb = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtWeb)
        val edtGithub = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtGithub)
        val edtFacebook = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtFacebook)

        // Pre-fill current data
        edtName.setText(profile.fullName)
        edtPhone.setText(profile.phone_number ?: "")
        edtNationality.setText(profile.nationality ?: "")
        edtAddress.setText(profile.companyAddress ?: "")
        edtDomaine.setText(profile.domaine ?: "")
        edtEmployees.setText(profile.employees_number?.toString() ?: "")
        edtDescription.setText(profile.description ?: "")
        edtTwitter.setText(profile.twitter_link ?: "")
        edtWeb.setText(profile.web_link ?: "")
        edtGithub.setText(profile.github_link ?: "")
        edtFacebook.setText(profile.facebook_link ?: "")

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                // Update the current profile with edited data
                currentProfile = profile.copy(
                    fullName = edtName.text.toString(),
                    phone_number = edtPhone.text.toString(),
                    nationality = edtNationality.text.toString(),
                    companyAddress = edtAddress.text.toString(),
                    domaine = edtDomaine.text.toString(),
                    employees_number = edtEmployees.text.toString().toIntOrNull() ?: 0,
                    description = edtDescription.text.toString(),
                    twitter_link = edtTwitter.text.toString(),
                    web_link = edtWeb.text.toString(),
                    github_link = edtGithub.text.toString(),
                    facebook_link = edtFacebook.text.toString()
                )
                currentProfile?.let { displayRecruiterData(it) }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                isEditMode = false
                btnEdit.setImageResource(R.drawable.ic_edit)
                btnEditPhoto.visibility = View.GONE
                btnAddService.visibility = View.GONE
                btnSave.visibility = View.GONE
            }
            .show()
    }

    private fun showAddServiceDialog() {
        val input = EditText(this).apply {
            hint = "Service name"
        }

        AlertDialog.Builder(this)
            .setTitle("Add Service")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val serviceName = input.text.toString().trim()
                val currentServices = currentProfile?.service?.toMutableList() ?: mutableListOf()

                if (serviceName.isNotEmpty() && !currentServices.contains(serviceName)) {
                    currentServices.add(serviceName)
                    currentProfile = currentProfile?.copy(service = currentServices)
                    displayServices(currentServices)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun removeService(service: String) {
        val currentServices = currentProfile?.service?.toMutableList() ?: return
        currentServices.remove(service)
        currentProfile = currentProfile?.copy(service = currentServices)
        displayServices(currentServices)
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
                            val photoUrl = response.body()?.url
                            Toast.makeText(
                                this@RecruiterProfileActivity,
                                "Photo uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Update current profile with new photo URL
                            currentProfile = currentProfile?.copy(photo_profil = photoUrl)
                        } else {
                            showError("Failed to upload photo: ${response.message()}")
                        }

                        // Clean up temp file
                        file.delete()
                    }

                    override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                        showLoading(false)
                        Log.e("RecruiterProfile", "Photo upload failed", t)
                        showError("Failed to upload photo: ${t.message}")
                        file.delete()
                    }
                })
        } catch (e: Exception) {
            Log.e("RecruiterProfile", "Error preparing photo upload", e)
            showError("Error: ${e.message}")
        }
    }

    private fun saveChanges() {
        val profile = currentProfile ?: return
        val token = sessionManager.getAccessToken()

        if (token.isNullOrEmpty()) {
            showError("Not authenticated")
            return
        }

        if (profile.fullName.isBlank()) {
            showError("Full name is required")
            return
        }

        if (profile.email.isBlank()) {
            showError("Email is required")
            return
        }

        showLoading(true)

        val updateRequest = UpdateProfileRequest(
            fullName = profile.fullName,
            email = profile.email,
            twitter_link = profile.twitter_link,
            web_link = profile.web_link,
            github_link = profile.github_link,
            facebook_link = profile.facebook_link,
            description = profile.description,
            phone_number = profile.phone_number,
            nationality = profile.nationality,
            companyAddress = profile.companyAddress,
            domaine = profile.domaine,
            employees_number = profile.employees_number,
            service = profile.service
        )

        ApiClient.userService.updateUserProfile("Bearer $token", updateRequest)
            .enqueue(object : Callback<RecruiterProfile> {
                override fun onResponse(
                    call: Call<RecruiterProfile>,
                    response: Response<RecruiterProfile>
                ) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        currentProfile = response.body()
                        currentProfile?.let { displayRecruiterData(it) }

                        Toast.makeText(
                            this@RecruiterProfileActivity,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        isEditMode = false
                        btnEdit.setImageResource(R.drawable.ic_edit)
                        btnEditPhoto.visibility = View.GONE
                        btnAddService.visibility = View.GONE
                        btnSave.visibility = View.GONE
                    } else {
                        when (response.code()) {
                            400 -> showError("Invalid profile data")
                            401 -> {
                                showError("Session expired")
                                navigateToLogin()
                            }
                            else -> showError("Failed to update: ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<RecruiterProfile>, t: Throwable) {
                    showLoading(false)
                    Log.e("RecruiterProfile", "Update failed", t)
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun openLink(url: String?) {
        if (!url.isNullOrEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show()
            }
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