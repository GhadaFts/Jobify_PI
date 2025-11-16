package com.example.jobify

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.jobify.data.CvSuggestion
import com.example.jobify.data.GeminiResponse
import com.example.jobify.services.AiService
import com.example.jobify.services.AiServiceException
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream

class CvCorrectionActivity : AppCompatActivity() {

    private val TAG = "CvCorrectionActivity"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView
    private lateinit var uploadZone: LinearLayout
    private lateinit var fileNameTextView: TextView
    private lateinit var analyzeButton: Button
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var mainScroll: NestedScrollView
    private var isDarkMode = false
    private lateinit var rootLayout: ScrollView
    private lateinit var tvAdviceResult: TextView



    // Results UI
    private lateinit var resultsWrapper: LinearLayout
    private lateinit var profileName: TextView
    private lateinit var profileTitle: TextView
    private lateinit var profileNationality: TextView
    private lateinit var profileSummary: TextView
    private lateinit var profileSkills: TextView
    private lateinit var profileExperience: TextView
    private lateinit var cvScoreText: TextView
    private lateinit var cvSuggestionsContainer: LinearLayout
    private lateinit var overallAssessment: TextView
    private lateinit var strengthsContainer: LinearLayout
    private lateinit var improvementsContainer: LinearLayout

    private var cvUri: Uri? = null

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uri = data?.data
            if (uri != null) {
                cvUri = uri
                val fileName = getFileName(uri)
                fileNameTextView.text = fileName
                fileNameTextView.visibility = View.VISIBLE
                analyzeButton.isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_correction)

        bindViews()
        setupListeners()

        // ---------------- MENU ACTIONS ----------------
        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuCorrectCVLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, CvCorrectionActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuJobMarketAnalyseLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, AICareerAdvisorActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuHelpLayout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "Help section coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            // Close drawer first
            drawerLayout.closeDrawer(GravityCompat.START)

            // Perform logout after drawer closes
            Handler(Looper.getMainLooper()).postDelayed({
                performLogout()
            }, 250)
        }
    }

    private fun bindViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        uploadZone = findViewById(R.id.upload_zone)
        fileNameTextView = findViewById(R.id.file_name)
        analyzeButton = findViewById(R.id.analyze_button)
        loadingIndicator = findViewById(R.id.loading_indicator)
        mainScroll = findViewById(R.id.main_scroll)

        resultsWrapper = findViewById(R.id.results_wrapper)
        profileName = findViewById(R.id.profile_name)
        profileTitle = findViewById(R.id.profile_title)
        profileNationality = findViewById(R.id.profile_nationality)
        profileSummary = findViewById(R.id.profile_summary)
        profileSkills = findViewById(R.id.profile_skills)
        profileExperience = findViewById(R.id.profile_experience)
        cvScoreText = findViewById(R.id.cv_score)
        cvSuggestionsContainer = findViewById(R.id.cv_suggestions_container)
        overallAssessment = findViewById(R.id.overall_assessment)
        strengthsContainer = findViewById(R.id.strengths_container)
        improvementsContainer = findViewById(R.id.improvements_container)
    }

    private fun setupListeners() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        uploadZone.setOnClickListener {
            // let user pick a PDF
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            filePickerLauncher.launch(intent)
        }

        analyzeButton.setOnClickListener {
            cvUri?.let { uri ->
                analyzeCv(uri)
            }
        }
    }

    private fun performLogout() {
        try {
            // Clear session
            val sessionManager = SessionManager(this)
            sessionManager.clearSession()

            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate to MainActivity (splash) which will redirect to login
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

            // Finish current activity
            finishAffinity() // This ensures all activities are cleared

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Logout error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    private fun toggleDarkMode(btnTheme: ImageView, darkModeIcon: ImageView?) {
        if (isDarkMode) {
            btnTheme.setImageResource(R.drawable.ic_sun)
            darkModeIcon?.setImageResource(R.drawable.ic_dark_mode)
            rootLayout.setBackgroundColor(Color.parseColor("#F5F7FA"))
            tvAdviceResult.setTextColor(Color.parseColor("#333333"))
            isDarkMode = false
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon)
            darkModeIcon?.setImageResource(R.drawable.ic_sun)
            rootLayout.setBackgroundColor(Color.parseColor("#1F1F1F"))
            tvAdviceResult.setTextColor(Color.parseColor("#FFFFFF"))
            isDarkMode = true
        }
    }
    private fun analyzeCv(uri: Uri) {
        showLoading(true)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 1) extract text off the UI thread
                val cvText = withContext(Dispatchers.IO) {
                    extractTextFromPdf(uri)
                }

                if (cvText.isNullOrBlank()) {
                    showErrorDialog(getString(R.string.error_extract_text))
                    return@launch
                }

                // 2) call AI (network call)
                val jsonResponse = withContext(Dispatchers.IO) {
                    try {
                        AiService.analyzeCv(cvText)
                    } catch (e: AiServiceException) {
                        // rethrow so we catch it specifically below
                        throw e
                    }
                }

                // 3) parse and display
                try {
                    val response = Json { isLenient = true; ignoreUnknownKeys = true }
                        .decodeFromString<GeminiResponse>(jsonResponse)
                    displayResults(response)
                } catch (e: Exception) {
                    Log.e(TAG, "Parsing error", e)
                    showErrorDialog(getString(R.string.error_parse_response) + "\n\n" + (e.message ?: ""))
                }

            } catch (e: AiServiceException) {
                Log.e(TAG, "AI service error", e)
                showErrorDialog(e.message ?: getString(R.string.error_ai_generic))
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error", e)
                showErrorDialog(getString(R.string.error_unexpected) + "\n\n" + (e.message ?: ""))
            } finally {
                showLoading(false)
            }
        }
    }

    private fun displayResults(response: GeminiResponse) {
        // show wrapper
        resultsWrapper.visibility = View.VISIBLE

        response.profile?.let {
            profileName.text = "Name: ${it.name ?: getString(R.string.not_provided_label)}"
            profileTitle.text = "Title: ${it.title ?: getString(R.string.not_provided_label)}"
            profileNationality.text = "Nationality: ${it.nationality ?: getString(R.string.not_provided_label)}"
            profileSummary.text = it.summary ?: getString(R.string.no_professional_summary)
            profileSkills.text = "Skills: ${it.skills?.joinToString() ?: getString(R.string.not_provided_label)}"
            profileExperience.text = "Experience: ${it.experience?.size ?: 0} positions"
        } ?: run {
            profileName.text = getString(R.string.not_provided_label)
        }

        cvScoreText.text = "${response.cvScore ?: 0}%"

        // suggestions
        addSuggestions(response.cvSuggestions)

        // improved summary
        overallAssessment.text = response.improvedSummary?.overallAssessment ?: ""
        addListItems(strengthsContainer, response.improvedSummary?.strengths)
        addListItems(improvementsContainer, response.improvedSummary?.improvements)

        // scroll to results
        mainScroll.post {
            mainScroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun addSuggestions(suggestions: List<CvSuggestion>?) {
        cvSuggestionsContainer.removeAllViews()
        if (suggestions.isNullOrEmpty()) {
            val t = TextView(this)
            t.text = getString(R.string.no_suggestions)
            t.setPadding(0, 8, 0, 8)
            cvSuggestionsContainer.addView(t)
            return
        }

        suggestions.forEach { suggestion ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(12, 12, 12, 12)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 4, 0, 4)
                }
            }

            // simple bullet icon placeholder
            val icon = TextView(this).apply {
                text = when (suggestion.type) {
                    "success" -> "✔"
                    "warning" -> "⚠"
                    "info" -> "ℹ"
                    "missing" -> "✖"
                    else -> "•"
                }
                textSize = 18f
                setPadding(0, 0, 12, 0)
            }

            val text = TextView(this).apply {
                text = "${suggestion.title ?: ""}\n${suggestion.message ?: ""}"
                textSize = 14f
            }

            row.addView(icon)
            row.addView(text)
            cvSuggestionsContainer.addView(row)
        }
    }

    private fun addListItems(container: LinearLayout, items: List<String>?) {
        container.removeAllViews()
        items?.forEach { item ->
            val t = TextView(this).apply {
                text = "• $item"
                textSize = 14f
                setPadding(0, 6, 0, 6)
            }
            container.addView(t)
        }
    }

    private fun extractTextFromPdf(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                // Using iText PdfReader + PdfTextExtractor
                val reader = PdfReader(stream)
                val sb = StringBuilder()
                val pages = reader.numberOfPages
                for (i in 1..pages) {
                    val pageText = PdfTextExtractor.getTextFromPage(reader, i)
                    sb.append(pageText).append("\n")
                }
                reader.close()
                sb.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract text from PDF", e)
            null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            // keep header hidden to emphasise loading
            // but don't hide results if they were already visible
            // (we keep UI simple)
            analyzeButton.isEnabled = false
        } else {
            analyzeButton.isEnabled = (cvUri != null)
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.analysis_error_title))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result.substring(cut + 1)
            }
        }
        return result ?: getString(R.string.unknown_file)
    }

}
