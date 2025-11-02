package com.example.jobify

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class CvCorrectionActivity : AppCompatActivity() {

    private lateinit var uploadZone: LinearLayout
    private lateinit var uploadText: TextView
    private lateinit var fileNameText: TextView
    private lateinit var analyzeButton: Button
    private var fileUri: android.net.Uri? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var rootLayout: LinearLayout
    private var isDarkMode = false

    companion object {
        private const val PICK_PDF_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_correction)

        rootLayout = findViewById(R.id.rootLayout)
        drawerLayout = findViewById(R.id.drawerLayout)

        // ---------------- Drawer + Theme Setup ----------------
        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnTheme = findViewById<ImageView>(R.id.btnTheme)
        val darkModeLayout = findViewById<LinearLayout>(R.id.menuDarkModeLayout)
        val darkModeIcon = findViewById<ImageView>(R.id.menuDarkModeIcon)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        darkModeLayout.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }

        btnTheme.setOnClickListener {
            toggleDarkMode(btnTheme, darkModeIcon)
        }

        // Navigation menu clicks
        findViewById<LinearLayout>(R.id.menuHomeLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, JobOpportunitiesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuProfileLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuLogoutLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuHelpLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<LinearLayout>(R.id.menuCorrectCVLayout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, CvCorrectionActivity::class.java))

        }



        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                rootLayout.alpha = 1 - slideOffset * 0.5f
                rootLayout.translationX = drawerView.width * slideOffset * 0.3f
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                rootLayout.alpha = 1f
                rootLayout.translationX = 0f
            }
            override fun onDrawerStateChanged(newState: Int) {}
        })

        // ---------------- CV Upload Logic ----------------
        uploadZone = findViewById(R.id.upload_zone)
        uploadText = findViewById(R.id.upload_text)
        fileNameText = findViewById(R.id.file_name)
        analyzeButton = findViewById(R.id.analyze_button)

        uploadZone.setOnClickListener {
            selectPdfFile()
        }

        analyzeButton.setOnClickListener {
            if (fileUri != null) {
                Toast.makeText(this, "Analyzing CV...", Toast.LENGTH_SHORT).show()
                // TODO: Envoyer le fichier au backend pour analyse
            } else {
                Toast.makeText(this, "Please upload a CV first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------- FILE PICKER ----------------
    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "Select CV"), PICK_PDF_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            fileUri = data?.data
            val fileName = fileUri?.lastPathSegment ?: "Unknown file"
            fileNameText.text = fileName
            analyzeButton.isEnabled = true
            uploadText.text = "File Uploaded"
        }
    }

    // ---------------- DARK MODE TOGGLE ----------------
    private fun toggleDarkMode(btnTheme: ImageView, darkModeIcon: ImageView) {
        if (isDarkMode) {
            btnTheme.setImageResource(R.drawable.ic_sun)
            darkModeIcon.setImageResource(R.drawable.ic_dark_mode)
            rootLayout.setBackgroundColor(Color.parseColor("#F9FAFB"))
            isDarkMode = false
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon)
            darkModeIcon.setImageResource(R.drawable.ic_sun)
            rootLayout.setBackgroundColor(Color.parseColor("#1F1F1F"))
            isDarkMode = true
        }
    }
}
