package com.example.jobify

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class AICareerAdvisorActivity : BaseDrawerActivity() {

    private lateinit var scrollViewRoot: ScrollView
    private lateinit var rootLayout: ScrollView

    private lateinit var inputInstructions: EditText
    private lateinit var inputCountry: EditText
    private lateinit var inputEducation: EditText
    private lateinit var inputCertificate: EditText
    private lateinit var inputSkills: EditText
    private lateinit var btnGetAdvice: Button
    private lateinit var tvAdviceResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ai_career_advisor)
        initViews()



        inputCountry = findViewById(R.id.inputCountry)
        inputEducation = findViewById(R.id.inputEducation)
        inputCertificate = findViewById(R.id.inputCertificate)
        btnGetAdvice = findViewById(R.id.btnGetAdvice)
        tvAdviceResult = findViewById(R.id.tvAdviceResult)
        val skillsContainer = findViewById<LinearLayout>(R.id.skillsContainer)
        val btnAddSkill = findViewById<Button>(R.id.btnAddSkill)
        val btnGetAdvice = findViewById<Button>(R.id.btnGetAdvice)
        val tvAdviceResult = findViewById<TextView>(R.id.tvAdviceResult)

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)


        // Dynamic Skill Field
        btnAddSkill.setOnClickListener {
            val newSkill = EditText(this)
            newSkill.hint = "Add another skill..."
            newSkill.setPadding(10, 10, 10, 10)
            newSkill.setBackgroundResource(android.R.drawable.edit_text)
            skillsContainer.addView(newSkill)
        }




        // Simulated AI Result
        btnGetAdvice.setOnClickListener {
            tvAdviceResult.visibility = TextView.VISIBLE
            tvAdviceResult.text = """
                🔍 Career Analysis Complete:
                • Focus on improving your portfolio visibility.
                • Learn emerging frameworks (e.g., Spring Boot, React).
                • Highlight teamwork & communication in interviews.
                • Target companies hiring in Tunisia tech scene.
            """.trimIndent()
        }


    }


    private fun initViews() {
        scrollViewRoot = findViewById(R.id.scrollViewRoot)
    }


}
