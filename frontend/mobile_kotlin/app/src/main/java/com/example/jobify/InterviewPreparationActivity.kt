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
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import java.io.FileOutputStream

class InterviewPreparationActivity : BaseDrawerActivity() {

    private lateinit var scrollViewRoot: ScrollView

    private lateinit var btnDownloadPdf: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interview_preparation)

        initViews()
        setupDownloadPdf()
    }


    private fun initViews() {

        scrollViewRoot = findViewById(R.id.scrollViewRoot)
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)
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
    override fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
