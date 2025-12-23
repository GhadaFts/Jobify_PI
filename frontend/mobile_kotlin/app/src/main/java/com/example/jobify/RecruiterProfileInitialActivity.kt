package com.example.jobify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.databinding.ActivityRecruiterProfileInitialBinding
import com.example.jobify.network.ApiClient
import com.example.jobify.network.PhotoUploadResponse
import com.example.jobify.network.RecruiterProfile
import com.example.jobify.network.UpdateProfileRequest
import com.google.android.material.chip.Chip
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class RecruiterProfileInitialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecruiterProfileInitialBinding
    private lateinit var sessionManager: SessionManager
    private var currentStep = 1
    private var imageUri: Uri? = null
    private var uploadedPhotoUrl: String? = null
    private val services = mutableListOf<String>()
    private var isLoading = false

    private val countryCodeMap = mapOf(
        "French" to "+33",
        "American" to "+1",
        "Canadian" to "+1",
        "British" to "+44",
        "German" to "+49",
        "Indian" to "+91"
    )

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            if (imageUri != null) {
                binding.ivCompanyLogo.setImageURI(imageUri)
                binding.ivCompanyLogo.visibility = View.VISIBLE
                binding.ivUploadIcon.visibility = View.GONE
                
                // Upload photo immediately
                uploadProfilePhoto(imageUri!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecruiterProfileInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)

        setupUI()
        setupListeners()
        updateStepUI()
    }

    private fun setupUI() {
        // Setup nationality spinner
        val nationalities = arrayOf("Select Nationality", "French", "American", "Canadian", "British", "German", "Indian")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nationalities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNationality.adapter = adapter
    }

    private fun setupListeners() {
        // Step navigation
        binding.btnPersonal.setOnClickListener { goToStep(1) }
        binding.btnProfile.setOnClickListener { goToStep(2) }
        binding.btnSocialLinks.setOnClickListener { goToStep(3) }

        // Logo upload
        binding.layoutLogoUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        // Nationality change listener
        binding.spinnerNationality.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val nationality = parent?.getItemAtPosition(position).toString()
                    binding.etCountryCode.setText(countryCodeMap[nationality] ?: "")
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        // Next button for step 1
        binding.btnNextStep1.setOnClickListener {
            if (validateBasicInfo()) {
                goToStep(2)
            }
        }

        // Add service
        binding.btnAddService.setOnClickListener {
            addService()
        }

        // Next button for step 2
        binding.btnNextStep2.setOnClickListener {
            goToStep(3)
        }

        // Save and complete
        binding.btnSaveComplete.setOnClickListener {
            if (validateBasicInfo()) {
                goToStep(4)
            }
        }

        // Go to Dashboard
        binding.btnGoToDashboard.setOnClickListener {
            if (validateBasicInfo()) {
                saveProfileToBackend()
            } else {
                Toast.makeText(this, "Please complete all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToStep(step: Int) {
        if (step > 1 && !validateBasicInfo() && currentStep == 1) {
            Toast.makeText(this, "Please complete Basic Information first", Toast.LENGTH_SHORT).show()
            return
        }

        currentStep = step
        updateStepUI()
        updateProgress()
    }

    private fun updateStepUI() {
        // Hide all steps
        binding.layoutStep1.visibility = View.GONE
        binding.layoutStep2.visibility = View.GONE
        binding.layoutStep3.visibility = View.GONE
        binding.layoutStep4.visibility = View.GONE

        // Show current step
        when (currentStep) {
            1 -> binding.layoutStep1.visibility = View.VISIBLE
            2 -> binding.layoutStep2.visibility = View.VISIBLE
            3 -> binding.layoutStep3.visibility = View.VISIBLE
            4 -> binding.layoutStep4.visibility = View.VISIBLE
        }

        // Update tab indicators
        updateTabIndicators()
    }

    private fun updateTabIndicators() {
        // Reset all tabs
        binding.btnPersonal.setTextColor(getColor(android.R.color.darker_gray))
        binding.btnProfile.setTextColor(getColor(android.R.color.darker_gray))
        binding.btnSocialLinks.setTextColor(getColor(android.R.color.darker_gray))

        binding.viewPersonalIndicator.visibility = View.INVISIBLE
        binding.viewProfileIndicator.visibility = View.INVISIBLE
        binding.viewSocialIndicator.visibility = View.INVISIBLE

        // Highlight current tab
        when (currentStep) {
            1 -> {
                binding.btnPersonal.setTextColor(getColor(R.color.colorPrimary))
                binding.viewPersonalIndicator.visibility = View.VISIBLE
            }
            2 -> {
                binding.btnProfile.setTextColor(getColor(R.color.colorPrimary))
                binding.viewProfileIndicator.visibility = View.VISIBLE
            }
            3 -> {
                binding.btnSocialLinks.setTextColor(getColor(R.color.colorPrimary))
                binding.viewSocialIndicator.visibility = View.VISIBLE
            }
        }
    }

    private fun updateProgress() {
        val progress = (currentStep * 100) / 4
        binding.progressBar.progress = progress
        binding.tvProgress.text = "$progress% Completed"
    }

    private fun validateBasicInfo(): Boolean {
        var isValid = true

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a company logo", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.etCompanyName.text.isNullOrEmpty()) {
            binding.tilCompanyName.error = "Company name is required"
            isValid = false
        } else {
            binding.tilCompanyName.error = null
        }

        if (binding.spinnerNationality.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select nationality", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.etPhone.text.isNullOrEmpty()) {
            binding.tilPhone.error = "Phone number is required"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        if (binding.etCompanyAddress.text.isNullOrEmpty()) {
            binding.tilCompanyAddress.error = "Company address is required"
            isValid = false
        } else {
            binding.tilCompanyAddress.error = null
        }

        return isValid
    }

    private fun addService() {
        val serviceName = binding.etService.text.toString().trim()
        if (serviceName.isEmpty()) {
            Toast.makeText(this, "Please enter a service", Toast.LENGTH_SHORT).show()
            return
        }

        services.add(serviceName)

        val chip = Chip(this)
        chip.text = serviceName
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.chipGroupServices.removeView(chip)
            services.remove(serviceName)
        }

        binding.chipGroupServices.addView(chip)
        binding.etService.text?.clear()
        Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show()
    }
    
    private fun uploadProfilePhoto(uri: Uri) {
        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show()
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

            setLoading(true)

            ApiClient.userService.uploadProfilePhoto("Bearer $token", multipartBody)
                .enqueue(object : Callback<PhotoUploadResponse> {
                    override fun onResponse(
                        call: Call<PhotoUploadResponse>,
                        response: Response<PhotoUploadResponse>
                    ) {
                        setLoading(false)

                        if (response.isSuccessful) {
                            uploadedPhotoUrl = response.body()?.url
                            Toast.makeText(
                                this@RecruiterProfileInitialActivity,
                                "Photo uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("ProfileInitial", "Photo uploaded: $uploadedPhotoUrl")
                        } else {
                            Toast.makeText(
                                this@RecruiterProfileInitialActivity,
                                "Failed to upload photo: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // Clean up temp file
                        file.delete()
                    }

                    override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                        setLoading(false)
                        Log.e("ProfileInitial", "Photo upload failed", t)
                        Toast.makeText(
                            this@RecruiterProfileInitialActivity,
                            "Failed to upload photo: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        file.delete()
                    }
                })
        } catch (e: Exception) {
            Log.e("ProfileInitial", "Error preparing photo upload", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveProfileToBackend() {
        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        // Get user email from session (required field)
        val email = sessionManager.getUserEmail()
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Email not found. Please login again.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        // Prepare profile data
        val companyName = binding.etCompanyName.text.toString().trim()
        val nationality = binding.spinnerNationality.selectedItem.toString()
        val countryCode = binding.etCountryCode.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val phoneNumber = "$countryCode$phone"
        val companyAddress = binding.etCompanyAddress.text.toString().trim()
        val domaine = binding.etSpeciality.text.toString().trim()
        val employeesNumber = binding.etEmployeesNumber.text.toString().toIntOrNull() ?: 0
        val biography = binding.etDescription.text.toString().trim()
        val webLink = binding.etWebLink.text.toString().trim()
        val githubLink = binding.etGithub.text.toString().trim()
        val twitterLink = binding.etTwitter.text.toString().trim()
        val facebookLink = binding.etFacebook.text.toString().trim()

        val updateRequest = UpdateProfileRequest(
            fullName = companyName,
            email = email,
            twitter_link = twitterLink.ifEmpty { null },
            web_link = webLink.ifEmpty { null },
            github_link = githubLink.ifEmpty { null },
            facebook_link = facebookLink.ifEmpty { null },
            description = biography.ifEmpty { null },
            phone_number = phoneNumber.ifEmpty { null },
            nationality = if (nationality != "Select Nationality") nationality else null,
            companyAddress = companyAddress.ifEmpty { null },
            domaine = domaine.ifEmpty { null },
            employees_number = employeesNumber,
            service = if (services.isNotEmpty()) services else null
        )

        setLoading(true)

        ApiClient.userService.updateUserProfile("Bearer $token", updateRequest)
            .enqueue(object : Callback<RecruiterProfile> {
                override fun onResponse(
                    call: Call<RecruiterProfile>,
                    response: Response<RecruiterProfile>
                ) {
                    setLoading(false)

                    if (response.isSuccessful) {
                        val profile = response.body()
                        
                        // Save photo URL to session - use uploaded URL or response photo
                        val photoUrl = uploadedPhotoUrl ?: profile?.photo_profil
                        photoUrl?.let { 
                            sessionManager.saveUserPhoto(it)
                            Log.d("ProfileInitial", "Photo URL saved to session: $it")
                        }
                        
                        Toast.makeText(
                            this@RecruiterProfileInitialActivity,
                            "Profile saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Navigate to dashboard
                        startActivity(Intent(this@RecruiterProfileInitialActivity, PostsActivity::class.java))
                        finish()
                    } else {
                        when (response.code()) {
                            400 -> Toast.makeText(
                                this@RecruiterProfileInitialActivity,
                                "Invalid profile data",
                                Toast.LENGTH_SHORT
                            ).show()
                            401 -> {
                                Toast.makeText(
                                    this@RecruiterProfileInitialActivity,
                                    "Session expired",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigateToLogin()
                            }
                            else -> Toast.makeText(
                                this@RecruiterProfileInitialActivity,
                                "Failed to save profile: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<RecruiterProfile>, t: Throwable) {
                    setLoading(false)
                    Log.e("ProfileInitial", "Failed to save profile", t)
                    Toast.makeText(
                        this@RecruiterProfileInitialActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    
    private fun setLoading(loading: Boolean) {
        isLoading = loading
        binding.btnGoToDashboard.isEnabled = !loading
        binding.btnNextStep1.isEnabled = !loading
        binding.btnNextStep2.isEnabled = !loading
        binding.btnSaveComplete.isEnabled = !loading
    }
    
    private fun navigateToLogin() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}