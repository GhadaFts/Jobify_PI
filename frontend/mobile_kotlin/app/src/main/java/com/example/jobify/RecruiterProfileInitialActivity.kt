package com.example.jobify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.databinding.ActivityRecruiterProfileInitialBinding
import com.google.android.material.chip.Chip

class RecruiterProfileInitialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecruiterProfileInitialBinding
    private var currentStep = 1
    private var imageUri: Uri? = null
    private val services = mutableListOf<String>()

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
            binding.ivCompanyLogo.setImageURI(imageUri)
            binding.ivCompanyLogo.visibility = View.VISIBLE
            binding.ivUploadIcon.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecruiterProfileInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                // Navigate to recruiter dashboard
                startActivity(Intent(this, PostsActivity::class.java))
                finish()
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
}