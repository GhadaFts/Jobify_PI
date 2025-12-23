package com.example.jobify

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.jobify.databinding.ActivityJobSeekerProfileInitialBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import com.example.jobify.network.ApiClient
import com.example.jobify.model.PhotoUploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import android.util.Log


class JobSeekerProfileInitialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobSeekerProfileInitialBinding
    private lateinit var sessionManager: SessionManager
    private var currentStep = 1
    private var imageUri: Uri? = null
    private val skills = mutableListOf<String>()
    private val experiences = mutableListOf<Experience>()
    private val educations = mutableListOf<Education>()
    private var uploadedPhotoUrl: String? = null
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
            binding.ivProfilePhoto.setImageURI(imageUri)
            binding.ivProfilePhoto.visibility = View.VISIBLE
            binding.ivUploadIcon.visibility = View.GONE
            
            // Upload photo immediately
            imageUri?.let { uploadProfilePhoto(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobSeekerProfileInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupUI()
        setupListeners()
        updateStepUI()
    }

    private fun setupUI() {
        // Setup nationality spinner with all countries
        val nationalities = arrayOf(
            "Select Nationality", "Afghan", "Albanian", "Algerian", "American", "Andorran",
            "Angolan", "Argentine", "Armenian", "Australian", "Austrian", "Azerbaijani",
            "Bahamian", "Bahraini", "Bangladeshi", "Barbadian", "Belarusian", "Belgian",
            "Belizean", "Beninese", "Bhutanese", "Bolivian", "Bosnian", "Brazilian",
            "British", "Bruneian", "Bulgarian", "Burkinabe", "Burmese", "Burundian",
            "Cambodian", "Cameroonian", "Canadian", "Cape Verdean", "Central African",
            "Chadian", "Chilean", "Chinese", "Colombian", "Comoran", "Congolese",
            "Costa Rican", "Croatian", "Cuban", "Cypriot", "Czech", "Danish",
            "Djiboutian", "Dominican", "Dutch", "East Timorese", "Ecuadorean", "Egyptian",
            "Emirati", "Equatorial Guinean", "Eritrean", "Estonian", "Ethiopian",
            "Fijian", "Filipino", "Finnish", "French", "Gabonese", "Gambian", "Georgian",
            "German", "Ghanaian", "Greek", "Grenadian", "Guatemalan", "Guinean",
            "Guyanese", "Haitian", "Honduran", "Hungarian", "Icelandic", "Indian",
            "Indonesian", "Iranian", "Iraqi", "Irish", "Israeli", "Italian", "Ivorian",
            "Jamaican", "Japanese", "Jordanian", "Kazakh", "Kenyan", "Kuwaiti",
            "Kyrgyz", "Laotian", "Latvian", "Lebanese", "Liberian", "Libyan",
            "Liechtensteiner", "Lithuanian", "Luxembourger", "Macedonian", "Malagasy",
            "Malawian", "Malaysian", "Maldivian", "Malian", "Maltese", "Mauritanian",
            "Mauritian", "Mexican", "Moldovan", "Monacan", "Mongolian", "Montenegrin",
            "Moroccan", "Mozambican", "Namibian", "Nepalese", "New Zealander",
            "Nicaraguan", "Nigerian", "Nigerien", "North Korean", "Norwegian", "Omani",
            "Pakistani", "Palauan", "Palestinian", "Panamanian", "Papua New Guinean",
            "Paraguayan", "Peruvian", "Polish", "Portuguese", "Qatari", "Romanian",
            "Russian", "Rwandan", "Saint Lucian", "Salvadoran", "Samoan", "Saudi",
            "Scottish", "Senegalese", "Serbian", "Seychellois", "Sierra Leonean",
            "Singaporean", "Slovak", "Slovenian", "Solomon Islander", "Somali",
            "South African", "South Korean", "Spanish", "Sri Lankan", "Sudanese",
            "Surinamese", "Swazi", "Swedish", "Swiss", "Syrian", "Taiwanese", "Tajik",
            "Tanzanian", "Thai", "Togolese", "Tongan", "Trinidadian", "Tunisian",
            "Turkish", "Turkmen", "Tuvaluan", "Ugandan", "Ukrainian", "Uruguayan",
            "Uzbek", "Venezuelan", "Vietnamese", "Welsh", "Yemeni", "Zambian", "Zimbabwean"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nationalities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNationality.adapter = adapter

        // Setup gender spinner
        val genders = arrayOf("Select Gender", "Male", "Female", "Other")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter
    }

    private fun setupListeners() {
        // Step navigation
        binding.btnPersonal.setOnClickListener { goToStep(1) }
        binding.btnProfile.setOnClickListener { goToStep(2) }
        binding.btnSocialLinks.setOnClickListener { goToStep(3) }

        // Photo upload
        binding.layoutPhotoUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        // Date picker
        binding.etDateOfBirth.setOnClickListener {
            showDatePicker()
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
            if (validatePersonalInfo()) {
                goToStep(2)
            }
        }

        // Add skill
        binding.btnAddSkill.setOnClickListener {
            addSkill()
        }

        // Add experience
        binding.btnAddExperience.setOnClickListener {
            addExperience()
        }

        // Add education
        binding.btnAddEducation.setOnClickListener {
            addEducation()
        }

        // Next button for step 2
        binding.btnNextStep2.setOnClickListener {
            goToStep(3)
        }

        // Save and complete
        binding.btnSaveComplete.setOnClickListener {
            if (validatePersonalInfo()) {
                goToStep(4)
            }
        }

        // Browse jobs
        binding.btnBrowseJobs.setOnClickListener {
            if (validatePersonalInfo()) {
                saveProfileToBackend()
            } else {
                Toast.makeText(this, "Please complete all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToStep(step: Int) {
        if (step > 1 && !validatePersonalInfo() && currentStep == 1) {
            Toast.makeText(this, "Please complete Personal Information first", Toast.LENGTH_SHORT).show()
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
                displayExperiences()
                displayEducations()
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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = Calendar.getInstance()
            date.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.etDateOfBirth.setText(dateFormat.format(date.time))
        }, year, month, day).show()
    }

    private fun validatePersonalInfo(): Boolean {
        var isValid = true

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a profile photo", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.etFullName.text.isNullOrEmpty()) {
            binding.tilFullName.error = "Full name is required"
            isValid = false
        } else {
            binding.tilFullName.error = null
        }

        if (binding.etTitle.text.isNullOrEmpty()) {
            binding.tilTitle.error = "Title is required"
            isValid = false
        } else {
            binding.tilTitle.error = null
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

        if (binding.spinnerGender.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.etDateOfBirth.text.isNullOrEmpty()) {
            binding.tilDateOfBirth.error = "Date of birth is required"
            isValid = false
        } else {
            binding.tilDateOfBirth.error = null
        }

        return isValid
    }

    private fun addSkill() {
        val skillName = binding.etSkill.text.toString().trim()
        if (skillName.isEmpty()) {
            Toast.makeText(this, "Please enter a skill", Toast.LENGTH_SHORT).show()
            return
        }

        skills.add(skillName)

        val chip = Chip(this)
        chip.text = skillName
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.chipGroupSkills.removeView(chip)
            skills.remove(skillName)
        }

        binding.chipGroupSkills.addView(chip)
        binding.etSkill.text?.clear()
        Toast.makeText(this, "Skill added successfully", Toast.LENGTH_SHORT).show()
    }

    private fun addExperience() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_experience, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val inputStartDate = dialogView.findViewById<EditText>(R.id.inputStartDate)
        val inputEndDate = dialogView.findViewById<EditText>(R.id.inputEndDate)
        val btnAddExperience = dialogView.findViewById<Button>(R.id.btnAddExperience)

        setupDatePicker(inputStartDate)
        setupDatePicker(inputEndDate)

        btnAddExperience.setOnClickListener {
            val position = dialogView.findViewById<EditText>(R.id.inputJobPosition).text.toString()
            val company = dialogView.findViewById<EditText>(R.id.inputCompanyName).text.toString()
            val address = dialogView.findViewById<EditText>(R.id.inputAddress).text.toString()
            val startDate = inputStartDate.text.toString()
            val endDate = inputEndDate.text.toString()

            if (validateExperienceFields(position, company, startDate, endDate)) {
                val experience = Experience(position, company, address, startDate, endDate)
                experiences.add(experience)
                displayExperiences()
                Toast.makeText(this, "Experience added successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun addEducation() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_education, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val inputEduStart = dialogView.findViewById<EditText>(R.id.inputEduStart)
        val inputEduEnd = dialogView.findViewById<EditText>(R.id.inputEduEnd)
        val btnAddEducation = dialogView.findViewById<Button>(R.id.btnAddEducation)

        setupDatePicker(inputEduStart)
        setupDatePicker(inputEduEnd)

        btnAddEducation.setOnClickListener {
            val university = dialogView.findViewById<EditText>(R.id.inputUniversity).text.toString()
            val degree = dialogView.findViewById<EditText>(R.id.inputDegree).text.toString()
            val field = dialogView.findViewById<EditText>(R.id.inputField).text.toString()
            val startDate = inputEduStart.text.toString()
            val endDate = inputEduEnd.text.toString()

            if (validateEducationFields(university, degree, field, startDate, endDate)) {
                val education = Education(university, degree, field, "$startDate - $endDate")
                educations.add(education)
                displayEducations()
                Toast.makeText(this, "Education added successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
    private fun displayExperiences() {
        binding.layoutExperiencesContainer.removeAllViews()

        experiences.forEachIndexed { index, experience ->
            val experienceView = layoutInflater.inflate(R.layout.item_experience, null)

            experienceView.findViewById<TextView>(R.id.tvJobPosition).text = experience.position
            experienceView.findViewById<TextView>(R.id.tvCompanyName).text = experience.company
            experienceView.findViewById<TextView>(R.id.tvAddress).text = experience.description
            experienceView.findViewById<TextView>(R.id.tvWorkDuration).text = "${experience.startDate} - ${experience.endDate}"

            experienceView.findViewById<Button>(R.id.btnDeleteExperience).setOnClickListener {
                experiences.removeAt(index)
                displayExperiences() // Refresh the list
                Toast.makeText(this, "Experience removed", Toast.LENGTH_SHORT).show()
            }

            binding.layoutExperiencesContainer.addView(experienceView)
        }
    }

    private fun displayEducations() {
        binding.layoutEducationsContainer.removeAllViews()

        educations.forEachIndexed { index, education ->
            val educationView = layoutInflater.inflate(R.layout.item_education, null)

            educationView.findViewById<TextView>(R.id.tvDegree).text = education.degree
            educationView.findViewById<TextView>(R.id.tvField).text = education.field
            educationView.findViewById<TextView>(R.id.tvUniversity).text = education.school
            educationView.findViewById<TextView>(R.id.tvGraduationDate).text = education.graduationDate

            educationView.findViewById<Button>(R.id.btnRemoveEducation).setOnClickListener {
                educations.removeAt(index)
                displayEducations() // Refresh the list
                Toast.makeText(this, "Education removed", Toast.LENGTH_SHORT).show()
            }

            binding.layoutEducationsContainer.addView(educationView)
        }
    }
    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = Calendar.getInstance()
                date.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                editText.setText(dateFormat.format(date.time))
            }, year, month, day).show()
        }
    }

    private fun validateExperienceFields(
        position: String,
        company: String,
        startDate: String,
        endDate: String
    ): Boolean {
        if (position.isEmpty()) {
            Toast.makeText(this, "Please enter job position", Toast.LENGTH_SHORT).show()
            return false
        }
        if (company.isEmpty()) {
            Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (startDate.isEmpty()) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show()
            return false
        }
        if (endDate.isEmpty()) {
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateEducationFields(
        university: String,
        degree: String,
        field: String,
        startDate: String,
        endDate: String
    ): Boolean {
        if (university.isEmpty()) {
            Toast.makeText(this, "Please enter university", Toast.LENGTH_SHORT).show()
            return false
        }
        if (degree.isEmpty()) {
            Toast.makeText(this, "Please enter degree", Toast.LENGTH_SHORT).show()
            return false
        }
        if (field.isEmpty()) {
            Toast.makeText(this, "Please enter field of study", Toast.LENGTH_SHORT).show()
            return false
        }
        if (startDate.isEmpty()) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show()
            return false
        }
        if (endDate.isEmpty()) {
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }



    data class Experience(
        var position: String,
        var company: String,
        var startDate: String,
        var endDate: String,
        var description: String
    )

    data class Education(
        var school: String,
        var degree: String,
        var field: String,
        var graduationDate: String
    )

    private fun uploadProfilePhoto(uri: Uri) {
        if (isLoading) return
        setLoading(true)

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val token = sessionManager.getAccessToken()
            if (token == null) {
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                navigateToLogin()
                return
            }

            ApiClient.userService.uploadProfilePhoto("Bearer $token", body).enqueue(object : Callback<PhotoUploadResponse> {
                override fun onResponse(call: Call<PhotoUploadResponse>, response: Response<PhotoUploadResponse>) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        uploadedPhotoUrl = response.body()?.url
                        Log.d("PhotoUpload", "Photo uploaded successfully: $uploadedPhotoUrl")
                        Toast.makeText(this@JobSeekerProfileInitialActivity, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("PhotoUpload", "Upload failed: ${response.code()} - ${response.message()}")
                        Toast.makeText(this@JobSeekerProfileInitialActivity, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                    }
                    file.delete()
                }

                override fun onFailure(call: Call<PhotoUploadResponse>, t: Throwable) {
                    setLoading(false)
                    Log.e("PhotoUpload", "Upload error: ${t.message}", t)
                    Toast.makeText(this@JobSeekerProfileInitialActivity, "Error uploading photo: ${t.message}", Toast.LENGTH_SHORT).show()
                    file.delete()
                }
            })
        } catch (e: Exception) {
            setLoading(false)
            Log.e("PhotoUpload", "Error preparing file: ${e.message}", e)
            Toast.makeText(this, "Error preparing photo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileToBackend() {
        if (isLoading) return
        setLoading(true)

        val token = sessionManager.getAccessToken()
        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        // Collect all form data
        val fullName = binding.etFullName.text.toString().trim()
        val title = binding.etTitle.text.toString().trim()
        val nationality = if (binding.spinnerNationality.selectedItemPosition > 0) {
            binding.spinnerNationality.selectedItem.toString()
        } else ""
        val countryCode = binding.etCountryCode.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val gender = if (binding.spinnerGender.selectedItemPosition > 0) {
            binding.spinnerGender.selectedItem.toString()
        } else ""
        val dateOfBirth = binding.etDateOfBirth.text.toString().trim()
        val biography = binding.etBiography.text.toString().trim()
        val portfolioLink = binding.etPortfolio.text.toString().trim()
        val githubLink = binding.etGithub.text.toString().trim()
        val twitterLink = binding.etTwitter.text.toString().trim()
        val facebookLink = binding.etFacebook.text.toString().trim()

        // Build profile update request
        val profileData = mutableMapOf<String, Any>(
            "name" to fullName,
            "titre" to title,
            "nationality" to nationality,
            "phone" to "$countryCode$phone",
            "gender" to gender,
            "date_of_birth" to dateOfBirth,
            "biography" to biography,
            "portfolio_link" to portfolioLink,
            "github_link" to githubLink,
            "twitter_link" to twitterLink,
            "facebook_link" to facebookLink,
            "skills" to skills,
            "experiences" to experiences.map {
                mapOf(
                    "position" to it.position,
                    "company" to it.company,
                    "description" to it.description,
                    "startDate" to it.startDate,
                    "endDate" to it.endDate
                )
            },
            "educations" to educations.map {
                mapOf(
                    "school" to it.school,
                    "degree" to it.degree,
                    "field" to it.field,
                    "graduationDate" to it.graduationDate
                )
            }
        )

        Log.d("ProfileSave", "Saving profile: $profileData")

        ApiClient.userService.updateUserProfile("Bearer $token", profileData).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                setLoading(false)
                if (response.isSuccessful) {
                    val userData = response.body()
                    Log.d("ProfileSave", "Profile saved successfully: $userData")
                    
                    // Save photo URL to session if present in response
                    userData?.get("photo_profil")?.let { photoUrl ->
                        sessionManager.saveUserPhoto(photoUrl.toString())
                        Log.d("ProfileSave", "Photo URL saved to session: $photoUrl")
                    }
                    
                    Toast.makeText(this@JobSeekerProfileInitialActivity, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to job seeker dashboard
                    val intent = Intent(this@JobSeekerProfileInitialActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("ProfileSave", "Save failed: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@JobSeekerProfileInitialActivity, "Failed to save profile: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                setLoading(false)
                Log.e("ProfileSave", "Save error: ${t.message}", t)
                Toast.makeText(this@JobSeekerProfileInitialActivity, "Error saving profile: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLoading(loading: Boolean) {
        isLoading = loading
        binding.btnBrowseJobs.isEnabled = !loading
        binding.btnSaveComplete.isEnabled = !loading
    }

    private fun navigateToLogin() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}