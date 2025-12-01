package com.example.jobify


import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.jobify.model.*
import com.example.jobify.network.ApiClient
import com.example.jobify.network.CareerAnalysisRequest
import com.example.jobify.network.CareerAnalysisResponse
import com.example.jobify.data.CareerAdviceProcessor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AICareerAdvisorActivity : BaseDrawerActivity() {

    private lateinit var scrollViewRoot: ScrollView
    private lateinit var inputCountry: EditText
    private lateinit var inputEducation: EditText
    private lateinit var inputCertificate: EditText
    private lateinit var skillsContainer: LinearLayout
    private lateinit var btnAddSkill: Button
    private lateinit var btnGetAdvice: Button
    private lateinit var tvAdviceResult: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView

    private val skillInputs = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ai_career_advisor)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        scrollViewRoot = findViewById(R.id.scrollViewRoot)
        inputCountry = findViewById(R.id.inputCountry)
        inputEducation = findViewById(R.id.inputEducation)
        inputCertificate = findViewById(R.id.inputCertificate)
        skillsContainer = findViewById(R.id.skillsContainer)
        btnAddSkill = findViewById(R.id.btnAddSkill)
        btnGetAdvice = findViewById(R.id.btnGetAdvice)
        tvAdviceResult = findViewById(R.id.tvAdviceResult)
        progressBar = findViewById(R.id.progressBar)
        errorMessage = findViewById(R.id.errorMessage)

        // Add first skill input to list
        val firstSkillInput = skillsContainer.getChildAt(0) as? EditText
        if (firstSkillInput != null) {
            skillInputs.add(firstSkillInput)
        }
    }

    private fun setupListeners() {
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            // Handle menu click if needed
        }

        // Dynamic Skill Field
        btnAddSkill.setOnClickListener {
            addSkillField()
        }

        // Get AI Advice
        btnGetAdvice.setOnClickListener {
            generateAdvice()
        }
    }

    private fun addSkillField() {
        val newSkill = EditText(this)
        newSkill.hint = "e.g., Another skill"
        newSkill.setPadding(30, 30, 30, 30)
        newSkill.setBackgroundResource(android.R.drawable.edit_text)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = 16
        newSkill.layoutParams = params

        skillsContainer.addView(newSkill)
        skillInputs.add(newSkill)
    }

    private fun isFormValid(): Boolean {
        val country = inputCountry.text.toString().trim()
        val education = inputEducation.text.toString().trim()
        val certificate = inputCertificate.text.toString().trim()

        return country.isNotEmpty() && education.isNotEmpty() && certificate.isNotEmpty()
    }

    private fun collectSkills(): String {
        return skillInputs
            .mapNotNull { it.text.toString().trim().takeIf { s -> s.isNotEmpty() } }
            .joinToString(", ")
    }

    private fun generateAdvice() {
        if (!isFormValid()) {
            showError("Please fill in all required fields (Country, Education, Certificate)")
            return
        }

        // Hide error and result
        errorMessage.visibility = View.GONE
        tvAdviceResult.visibility = View.GONE

        // Show loading
        progressBar.visibility = View.VISIBLE
        btnGetAdvice.isEnabled = false
        btnGetAdvice.text = "Analyzing..."

        // Prepare request
        val request = CareerAnalysisRequest(
            country = inputCountry.text.toString().trim(),
            education = inputEducation.text.toString().trim(),
            certificate = inputCertificate.text.toString().trim(),
            skills = collectSkills().takeIf { it.isNotEmpty() }
        )

        // Call API
        ApiClient.careerService.analyzeCareerCall(request).enqueue(object : Callback<CareerAnalysisResponse> {
            override fun onResponse(
                call: Call<CareerAnalysisResponse>,
                response: Response<CareerAnalysisResponse>
            ) {
                progressBar.visibility = View.GONE
                btnGetAdvice.isEnabled = true
                btnGetAdvice.text = "Get AI Career Advice"

                if (response.isSuccessful && response.body() != null) {
                    val aiResponse = response.body()!!
                    Log.d("CareerAdvice", "AI Response received: ${aiResponse.advice}")

                    // Process AI advice
                    val processedAdvice = CareerAdviceProcessor.processAIAdvice(aiResponse, request)
                    displayAdvice(processedAdvice)
                } else {
                    Log.e("CareerAdvice", "API Error: ${response.code()} - ${response.message()}")
                    showError("Failed to generate career advice. Error: ${response.code()}")
                    showFallbackAdvice(request)
                }
            }

            override fun onFailure(call: Call<CareerAnalysisResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnGetAdvice.isEnabled = true
                btnGetAdvice.text = "Get AI Career Advice"

                Log.e("CareerAdvice", "Network Error: ${t.message}", t)

                val errorMsg = when {
                    t.message?.contains("Unable to resolve host") == true ->
                        "Unable to connect to career service. Please check your internet connection."
                    t.message?.contains("timeout") == true ->
                        "Request timeout. The analysis is taking longer than expected."
                    else ->
                        "Failed to connect to career service: ${t.message}"
                }

                showError(errorMsg)
                showFallbackAdvice(request)
            }
        })
    }

    private fun displayAdvice(advice: ProcessedAdvice) {
        val result = StringBuilder()

        // Summary
        result.append("🎯 YOUR PERSONALIZED CAREER PLAN\n\n")
        result.append("${advice.summary}\n\n")

        // Recommendations
        result.append("📋 KEY RECOMMENDATIONS\n\n")
        advice.recommendations.forEachIndexed { index, rec ->
            val icon = when (rec.priority) {
                Priority.HIGH -> "⚠️"
                Priority.MEDIUM -> "⏰"
                Priority.LOW -> "✅"
            }
            result.append("$icon ${rec.title} (${rec.priority.getDisplayName()} Priority)\n")
            result.append("${rec.description}\n\n")
        }

        // Skills
        result.append("💡 SKILLS TO FOCUS ON\n\n")
        advice.skills.forEach { skill ->
            result.append("🎯 ${skill.name}\n")
            result.append("${skill.reason}\n\n")
        }

        // Career Path
        result.append("🛣️ YOUR CAREER PATH\n\n")
        result.append(advice.careerPath)

        tvAdviceResult.text = result.toString()
        tvAdviceResult.visibility = View.VISIBLE

        // Scroll to result
        scrollViewRoot.post {
            scrollViewRoot.smoothScrollTo(0, tvAdviceResult.top)
        }
    }

    private fun showFallbackAdvice(request: CareerAnalysisRequest) {
        val fallbackAdvice = CareerAdviceProcessor.createFallbackAdvice(request)
        displayAdvice(fallbackAdvice)
    }

    private fun showError(message: String) {
        errorMessage.text = message
        errorMessage.visibility = View.VISIBLE

        // Scroll to error
        scrollViewRoot.post {
            scrollViewRoot.smoothScrollTo(0, 0)
        }
    }

    private fun resetForm() {
        inputCountry.text.clear()
        inputEducation.text.clear()
        inputCertificate.text.clear()

        // Clear all skill inputs except the first one
        while (skillsContainer.childCount > 1) {
            skillsContainer.removeViewAt(skillsContainer.childCount - 1)
        }
        skillInputs.clear()
        val firstSkillInput = skillsContainer.getChildAt(0) as? EditText
        if (firstSkillInput != null) {
            firstSkillInput.text.clear()
            skillInputs.add(firstSkillInput)
        }

        tvAdviceResult.visibility = View.GONE
        errorMessage.visibility = View.GONE
    }
}