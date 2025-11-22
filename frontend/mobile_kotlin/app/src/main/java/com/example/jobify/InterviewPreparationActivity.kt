package com.example.jobify

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.io.File
import java.io.FileOutputStream

class InterviewPreparationActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var scrollViewRoot: ScrollView

    private lateinit var btnMenu: ImageView
    private lateinit var btnTheme: ImageView

    private lateinit var menuHomeLayout: LinearLayout
    private lateinit var menuProfileLayout: LinearLayout
    private lateinit var menuLogoutLayout: LinearLayout
    private lateinit var menuDarkModeLayout: LinearLayout
    private lateinit var menuHelpLayout: LinearLayout

    private lateinit var menuCorrectCVLayout: LinearLayout
    private lateinit var menuInterviewTrainingLayout: LinearLayout
    private lateinit var menuJobMarketAnalyseLayout: LinearLayout

    private lateinit var btnDownloadPdf: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interview_preparation)

        initViews()
        setupDrawerActions()
        setupThemeToggle()
        setupDownloadPdf()
    }

    // -------------------------
    // VIEW INITIALIZATION
    // -------------------------
    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        scrollViewRoot = findViewById(R.id.scrollViewRoot)

        btnMenu = findViewById(R.id.btnMenu)
        btnTheme = findViewById(R.id.btnTheme)

        menuHomeLayout = findViewById(R.id.menuHomeLayout)
        menuProfileLayout = findViewById(R.id.menuProfileLayout)
        menuLogoutLayout = findViewById(R.id.menuLogoutLayout)
        menuDarkModeLayout = findViewById(R.id.menuDarkModeLayout)
        menuHelpLayout = findViewById(R.id.menuHelpLayout)

        menuCorrectCVLayout = findViewById(R.id.menuCorrectCVLayout)
        menuInterviewTrainingLayout = findViewById(R.id.menuInterviewTrainingLayout)
        menuJobMarketAnalyseLayout = findViewById(R.id.menuJobMarketAnalyseLayout)

        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)
    }

    // -------------------------
    // DRAWER MENU ACTIONS
    // -------------------------
    private fun setupDrawerActions() {

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuHomeLayout.setOnClickListener {
            showMessage("Home clicked")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuProfileLayout.setOnClickListener {
            showMessage("Profile clicked")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuLogoutLayout.setOnClickListener {
            showMessage("Logged out")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuHelpLayout.setOnClickListener {
            showMessage("Help center")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuCorrectCVLayout.setOnClickListener {
            showMessage("Correct CV")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuInterviewTrainingLayout.setOnClickListener {
            showMessage("Interview Training")
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuJobMarketAnalyseLayout.setOnClickListener {
            showMessage("Job Market Analyse")
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    // -------------------------
    // THEME TOGGLE (DARK / LIGHT)
    // -------------------------
    private fun setupThemeToggle() {
        btnTheme.setOnClickListener {

            val current = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            if (current == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                showMessage("Light mode activated")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                showMessage("Dark mode activated")
            }
        }
    }

    // -------------------------
    // PDF DOWNLOAD
    // -------------------------
    private fun setupDownloadPdf() {
        btnDownloadPdf.setOnClickListener {
            val pdfFile = createSamplePDF()

            if (pdfFile != null) {
                openPDF(pdfFile)

                Handler(Looper.getMainLooper()).postDelayed({
                    scrollViewRoot.smoothScrollTo(0, 0)
                }, 300)
            } else {
                showMessage("Error creating PDF")
            }
        }
    }

    private fun createSamplePDF(): File? {
        return try {
            val file = File(cacheDir, "interview_guide.pdf")
            val output = FileOutputStream(file)

            val content = """
                Guide d'entretien - Exemple
                ----------------------------
                
                • 50+ questions courantes
                • Questions comportementales
                • Questions techniques
                • Conseils pour répondre
                • Erreurs à éviter
                
                Ceci est un PDF généré automatiquement.
            """.trimIndent()

            output.write(content.toByteArray())
            output.close()

            file
        } catch (e: Exception) {
            null
        }
    }

    private fun openPDF(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            showMessage("Aucun lecteur PDF disponible")
        }
    }

    // -------------------------
    // UTILITIES
    // -------------------------
    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
