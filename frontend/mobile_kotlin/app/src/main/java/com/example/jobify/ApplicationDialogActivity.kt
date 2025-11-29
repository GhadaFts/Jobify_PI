package com.example.jobify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.jobify.network.*
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class ApplicationDialogActivity : AppCompatActivity() {

    private lateinit var jobTitleText: TextView
    private lateinit var companyNameText: TextView
    private lateinit var btnGenerateCV: Button
    private lateinit var btnDownloadPDF: Button
    private lateinit var btnUploadCV: Button
    private lateinit var btnSubmitApplication: Button
    private lateinit var btnCancel: Button
    private lateinit var coverLetterInput: TextInputEditText
    private lateinit var errorMessageText: TextView
    private lateinit var successMessageText: TextView
    private lateinit var cvPreviewCard: CardView
    private lateinit var cvPreviewText: TextView
    private lateinit var atsScoreCard: CardView
    private lateinit var atsScoreText: TextView
    private lateinit var keywordsContainer: LinearLayout
    private lateinit var uploadedFileText: TextView
    private lateinit var progressBar: ProgressBar

    private var jobPost: JobPost? = null
    private var uploadedFileUri: Uri? = null
    private var generatedCVText: String? = null
    private var atsScore: Int = 0
    private var cvLink: String? = null

    private lateinit var sessionManager: SessionManager
    private lateinit var cvUploadService: CvUploadApiService
    private lateinit var applicationService: ApplicationApiService

    companion object {
        const val EXTRA_JOB_POST = "job_post"
        const val PICK_FILE_REQUEST = 1001
        const val TAG = "ApplicationDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_dialog)

        // Initialize services
        sessionManager = SessionManager(this)
        cvUploadService = ApiClient.cvUploadService
        applicationService = ApiClient.applicationService

        // Get job post from intent
        jobPost = intent.getSerializableExtra(EXTRA_JOB_POST) as? JobPost

        initViews()
        setupListeners()
        displayJobInfo()
    }

    private fun initViews() {
        jobTitleText = findViewById(R.id.jobTitleText)
        companyNameText = findViewById(R.id.companyNameText)
        btnGenerateCV = findViewById(R.id.btnGenerateCV)
        btnDownloadPDF = findViewById(R.id.btnDownloadPDF)
        btnUploadCV = findViewById(R.id.btnUploadCV)
        btnSubmitApplication = findViewById(R.id.btnSubmitApplication)
        btnCancel = findViewById(R.id.btnCancel)
        coverLetterInput = findViewById(R.id.coverLetterInput)
        errorMessageText = findViewById(R.id.errorMessageText)
        successMessageText = findViewById(R.id.successMessageText)
        cvPreviewCard = findViewById(R.id.cvPreviewCard)
        cvPreviewText = findViewById(R.id.cvPreviewText)
        atsScoreCard = findViewById(R.id.atsScoreCard)
        atsScoreText = findViewById(R.id.atsScoreText)
        keywordsContainer = findViewById(R.id.keywordsContainer)
        uploadedFileText = findViewById(R.id.uploadedFileText)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnGenerateCV.setOnClickListener {
            generateAICV()
        }

        btnDownloadPDF.setOnClickListener {
            downloadPDF()
        }

        btnUploadCV.setOnClickListener {
            openFilePicker()
        }

        btnSubmitApplication.setOnClickListener {
            submitApplication()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun displayJobInfo() {
        jobPost?.let { job ->
            jobTitleText.text = job.title
            companyNameText.text = "at ${job.jobPosition}"
        }
    }

    // Keep the mock AI CV generation (static, no backend call)
    private fun generateAICV() {
        showProgress(true)
        hideMessages()

        // Simulate AI CV generation (no backend call)
        android.os.Handler(mainLooper).postDelayed({
            generateMockCV()
            showProgress(false)
            showSuccess("CV generated successfully!")

            // Show CV preview and ATS score
            cvPreviewCard.visibility = View.VISIBLE
            atsScoreCard.visibility = View.VISIBLE
            btnDownloadPDF.visibility = View.VISIBLE
            btnSubmitApplication.isEnabled = true
        }, 2000)
    }

    private fun generateMockCV() {
        jobPost?.let { job ->
            generatedCVText = """
                PROFESSIONAL SUMMARY
                ==================
                Experienced professional seeking ${job.title} position with ${job.experience} years of experience.
                
                WORK EXPERIENCE
                ==============
                ${job.jobPosition}
                ${job.experience}
                
                SKILLS
                ======
                ${job.skills.joinToString(", ")}
                
                DESCRIPTION
                ===========
                ${job.description}
            """.trimIndent()

            cvPreviewText.text = generatedCVText

            // Mock ATS score (70-95)
            atsScore = (70..95).random()
            atsScoreText.text = "$atsScore/100"

            // Set color based on score
            val scoreColor = when {
                atsScore >= 80 -> android.graphics.Color.parseColor("#10B981") // green
                atsScore >= 60 -> android.graphics.Color.parseColor("#F59E0B") // orange
                else -> android.graphics.Color.parseColor("#EF4444") // red
            }
            atsScoreText.setTextColor(scoreColor)

            // Display keywords
            displayKeywords(job.skills)
        }
    }

    private fun displayKeywords(keywords: List<String>) {
        keywordsContainer.removeAllViews()

        // Create rows with up to 3 chips per row
        val keywordsList = keywords.take(8)
        var currentRow: LinearLayout? = null

        keywordsList.forEachIndexed { index, keyword ->
            // Create new row every 3 items
            if (index % 3 == 0) {
                currentRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 8)
                    }
                }
                keywordsContainer.addView(currentRow)
            }

            val chip = TextView(this).apply {
                text = keyword
                setPadding(16, 8, 16, 8)
                setBackgroundResource(R.drawable.keyword_chip_background)
                setTextColor(android.graphics.Color.parseColor("#065F46"))
                textSize = 12f

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 8, 0)
                }
                layoutParams = params
            }
            currentRow?.addView(chip)
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            val mimeTypes = arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            )
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadedFileUri = uri
                val fileName = getFileName(uri)
                uploadedFileText.text = "Selected: $fileName"
                uploadedFileText.visibility = View.VISIBLE
                btnSubmitApplication.isEnabled = true
                hideMessages()
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result = "Unknown"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
        return result
    }

    private fun downloadPDF() {
        if (generatedCVText != null) {
            showSuccess("PDF downloaded successfully!")
        } else {
            showError("No CV generated yet. Please generate a CV first.")
        }
    }

    // REAL BACKEND INTEGRATION - Submit application with CV upload
    private fun submitApplication() {
        if (uploadedFileUri == null) {
            showError("Please upload your CV file.")
            return
        }

        val userProfile = sessionManager.getUserProfile()
        if (userProfile?.keycloakId == null) {
            showError("User not authenticated. Please log in again.")
            return
        }

        val job = jobPost
        if (job == null) {
            showError("Job information not found.")
            return
        }

        showProgress(true)
        hideMessages()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Step 1: Upload CV
                val file = getFileFromUri(uploadedFileUri!!)
                if (file == null) {
                    withContext(Dispatchers.Main) {
                        showError("Failed to read file. Please try again.")
                        showProgress(false)
                    }
                    return@launch
                }

                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val jobSeekerIdBody = userProfile.keycloakId.toRequestBody("text/plain".toMediaTypeOrNull())
                val jobOfferIdBody = job.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                withContext(Dispatchers.Main) {
                    showSuccess("Uploading CV...")
                }

                // Use Call.execute() for synchronous call in IO dispatcher
                val cvResponse = cvUploadService.uploadCV(filePart, jobSeekerIdBody, jobOfferIdBody).execute()

                if (!cvResponse.isSuccessful) {
                    val errorBody = cvResponse.errorBody()?.string()
                    Log.e(TAG, "❌ CV upload failed: $errorBody")
                    withContext(Dispatchers.Main) {
                        showError("Failed to upload CV. Please try again.")
                        showProgress(false)
                    }
                    return@launch
                }

                cvLink = cvResponse.body()?.cvLink
                Log.d(TAG, "✅ CV uploaded: $cvLink")

                withContext(Dispatchers.Main) {
                    showSuccess("CV uploaded! Submitting application...")
                }

                // Step 2: Check for duplicate application
                val checkResponse = applicationService.checkDuplicateApplicationCall(
                    job.id.toString(),
                    userProfile.keycloakId
                ).execute()

                if (checkResponse.isSuccessful && checkResponse.body() == true) {
                    withContext(Dispatchers.Main) {
                        showError("You have already applied for this job.")
                        showProgress(false)
                    }
                    return@launch
                }

                // Step 3: Submit application
                val applicationPayload: Map<String, Any> = mapOf(
                    "jobOfferId" to job.id,
                    "cvLink" to cvLink!!,
                    "motivationLettre" to coverLetterInput.text.toString(),
                    "isFavorite" to false
                )

                val applicationResponse = applicationService.createApplicationCall(applicationPayload).execute()

                withContext(Dispatchers.Main) {
                    if (applicationResponse.isSuccessful) {
                        val applicationData = applicationResponse.body()
                        Log.d(TAG, "✅ Application submitted: $applicationData")

                        showSuccess("Application submitted successfully!")
                        showProgress(false)

                        // Wait a bit then close and return success
                        android.os.Handler(mainLooper).postDelayed({
                            val resultIntent = Intent().apply {
                                putExtra("success", true)
                                putExtra("jobId", job.id)
                                putExtra("applicationId", applicationData?.get("id") as? String)
                                putExtra("cvLink", cvLink)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }, 1500)
                    } else {
                        val errorBody = applicationResponse.errorBody()?.string()
                        Log.e(TAG, "❌ Application failed: $errorBody")
                        showError("Failed to submit application. Please try again.")
                        showProgress(false)
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showError("An error occurred: ${e.message}")
                    showProgress(false)
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val fileName = getFileName(uri)
            val file = File(cacheDir, fileName)
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create file from URI: ${e.message}", e)
            null
        }
    }

    private fun showProgress(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnGenerateCV.isEnabled = !show
        btnUploadCV.isEnabled = !show
        btnSubmitApplication.isEnabled = !show
    }

    private fun showError(message: String) {
        errorMessageText.text = message
        errorMessageText.visibility = View.VISIBLE
        successMessageText.visibility = View.GONE
    }

    private fun showSuccess(message: String) {
        successMessageText.text = message
        successMessageText.visibility = View.VISIBLE
        errorMessageText.visibility = View.GONE
    }

    private fun hideMessages() {
        errorMessageText.visibility = View.GONE
        successMessageText.visibility = View.GONE
    }
}