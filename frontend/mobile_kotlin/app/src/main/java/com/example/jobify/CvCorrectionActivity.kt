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
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.jobify.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class CvCorrectionActivity : AppCompatActivity() {

    private val TAG = "CvCorrectionActivity"

    // Views
    private lateinit var rootLayout: LinearLayout // CHANGÉ DE ScrollView À LinearLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView
    private lateinit var btnTheme: ImageView
    private lateinit var uploadZone: LinearLayout
    private lateinit var fileNameTextView: TextView
    private lateinit var analyzeButton: Button
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var mainScroll: NestedScrollView

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

    // Job description input
    private lateinit var jobDescriptionInput: EditText
    private lateinit var jobDescriptionLayout: LinearLayout

    private var cvUri: Uri? = null
    private var isDarkMode = false
    private var pdfText: String = ""

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uri = data?.data
            if (uri != null) {
                cvUri = uri
                val fileName = getFileName(uri)
                fileNameTextView.text = "Fichier sélectionné : $fileName"
                fileNameTextView.visibility = View.VISIBLE
                analyzeButton.isEnabled = true

                // Extraire le texte immédiatement après la sélection
                extractPdfText(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cv_correction)

        initViews()
        setupListeners()
        setupUI()

        Log.d(TAG, "CvCorrectionActivity créé")
    }

    private fun initViews() {
        // Navigation
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        btnTheme = findViewById(R.id.btnTheme)

        // Main content
        rootLayout = findViewById(R.id.scrollViewRoot) // CORRECTION ICI
        mainScroll = findViewById(R.id.mainScroll)
        uploadZone = findViewById(R.id.upload_zone)
        fileNameTextView = findViewById(R.id.file_name)
        analyzeButton = findViewById(R.id.analyze_button)
        loadingIndicator = findViewById(R.id.loading_indicator)

        // Job description
        jobDescriptionInput = findViewById(R.id.jobDescriptionInput)
        jobDescriptionLayout = findViewById(R.id.jobDescriptionLayout)

        // Results section
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

    private fun setupUI() {
        // Initialiser l'état de l'UI
        resultsWrapper.visibility = View.GONE
        loadingIndicator.visibility = View.GONE
        analyzeButton.isEnabled = false
    }

    private fun setupListeners() {
        // Menu navigation
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Theme toggle
        btnTheme.setOnClickListener {
            toggleDarkMode()
        }

        // File upload
        uploadZone.setOnClickListener {
            pickPdfFile()
        }

        // Analyze button
        analyzeButton.setOnClickListener {
            if (pdfText.isNotEmpty()) {
                analyzeCv()
            } else {
                showMessage("Veuillez d'abord sélectionner un fichier PDF")
            }
        }
    }

    private fun pickPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private fun extractPdfText(uri: Uri) {
        showLoading(true)
        analyzeButton.isEnabled = false

        // Version simple pour tester - pas d'extraction PDF
        Handler(Looper.getMainLooper()).postDelayed({
            pdfText = """
                CV de Test - Développeur Android
                Nom: Jean Dupont
                Titre: Développeur Android Senior
                Nationalité: Française
                
                PROFIL PROFESSIONNEL:
                Développeur Android avec 5 ans d'expérience dans le développement d'applications mobiles.
                Expert en Kotlin, Java, Android SDK, et architecture MVVM.
                
                COMPÉTENCES:
                - Kotlin, Java
                - Android SDK
                - Retrofit, Room
                - Firebase
                - Git, Agile/Scrum
                
                EXPÉRIENCE:
                - Développeur Android Senior chez TechCorp (2020-2024)
                - Développeur Android chez MobileStartup (2018-2020)
                
                FORMATION:
                - Master en Informatique, Université de Paris
                
                LANGUES:
                - Français: Langue maternelle
                - Anglais: Courant
            """.trimIndent()

            showLoading(false)
            analyzeButton.isEnabled = true
            showMessage("CV chargé avec succès (mode test)")
            Log.d(TAG, "Texte simulé chargé: ${pdfText.length} caractères")
        }, 1500)
    }

    private fun analyzeCv() {
        if (pdfText.isEmpty()) {
            showMessage("Aucun texte PDF disponible pour analyse")
            return
        }

        showLoading(true)
        resultsWrapper.visibility = View.GONE
        analyzeButton.isEnabled = false

        val jobDescription = jobDescriptionInput.text.toString().trim()

        Log.d(TAG, "=== DÉMARRAGE DE L'ANALYSE CV ===")
        Log.d(TAG, "Longueur du texte PDF: ${pdfText.length} caractères")
        Log.d(TAG, "Description du poste: ${if (jobDescription.isNotEmpty()) "fournie (${jobDescription.length} chars)" else "non fournie"}")

        val request = CvAnalysisRequest(
            cvContent = pdfText,
            jobDescription = if (jobDescription.isNotEmpty()) jobDescription else null
        )

        Log.d(TAG, "Envoi de la requête à l'API...")

        ApiClient.aiService.analyzeCvFullCall(request).enqueue(object : Callback<CvAnalysisResponseFull> {
            override fun onResponse(call: Call<CvAnalysisResponseFull>, response: Response<CvAnalysisResponseFull>) {
                showLoading(false)
                analyzeButton.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val analysisResult = response.body()!!

                    Log.d(TAG, "=== ANALYSE RÉUSSIE ===")
                    Log.d(TAG, "Score CV: ${analysisResult.cvScore}%")
                    Log.d(TAG, "Nombre de suggestions: ${analysisResult.cvSuggestions.size}")

                    displayResults(analysisResult)

                    // Scroll vers les résultats
                    mainScroll.postDelayed({
                        mainScroll.fullScroll(View.FOCUS_DOWN)
                    }, 300)

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "=== ERREUR API ===")
                    Log.e(TAG, "Code: ${response.code()}")
                    Log.e(TAG, "Message: ${response.message()}")
                    Log.e(TAG, "Corps d'erreur: $errorBody")

                    // Essayer avec l'ancien format si le nouveau échoue
                    if (response.code() == 404 || response.code() == 500) {
                        Log.d(TAG, "Tentative avec l'ancien format...")
                        tryLegacyFormat(request)
                    } else {
                        showErrorDialog("Erreur du serveur (${response.code()}):\n${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<CvAnalysisResponseFull>, t: Throwable) {
                showLoading(false)
                analyzeButton.isEnabled = true

                Log.e(TAG, "=== ERREUR RÉSEAU ===")
                Log.e(TAG, "Message: ${t.message}")
                Log.e(TAG, "Cause: ${t.cause}")
                Log.e(TAG, "StackTrace: ${t.stackTraceToString()}")

                // Mode simulation pour tester l'UI
                runSimulationMode()
            }
        })
    }

    private fun tryLegacyFormat(request: CvAnalysisRequest) {
        Log.d(TAG, "Essai du format legacy...")

        ApiClient.aiService.analyzeCvCall(request).enqueue(object : Callback<CvAnalysisResponseLegacy> {
            override fun onResponse(call: Call<CvAnalysisResponseLegacy>, response: Response<CvAnalysisResponseLegacy>) {
                if (response.isSuccessful && response.body() != null) {
                    val legacyResult = response.body()!!
                    Log.d(TAG, "Format legacy réussi! Score: ${legacyResult.score}")
                    displayLegacyResults(legacyResult)
                } else {
                    Log.e(TAG, "Format legacy échoué: ${response.code()}")
                    // Mode simulation si tout échoue
                    runSimulationMode()
                }
            }

            override fun onFailure(call: Call<CvAnalysisResponseLegacy>, t: Throwable) {
                Log.e(TAG, "Échec du format legacy", t)
                // Mode simulation
                runSimulationMode()
            }
        })
    }

    private fun runSimulationMode() {
        Log.d(TAG, "Mode simulation activé")

        // Créer des données de simulation
        val simulatedResult = CvAnalysisResponseFull(
            cvScore = 78,
            cvSuggestions = listOf(
                CvSuggestion(type = "success", title = "Format professionnel", message = "Votre CV a un format clair et professionnel"),
                CvSuggestion(type = "warning", title = "Expérience détaillée", message = "Ajoutez plus de détails sur vos réalisations"),
                CvSuggestion(type = "info", title = "Compétences techniques", message = "Considérez d'ajouter des compétences en CI/CD"),
                CvSuggestion(type = "missing", title = "Projets personnels", message = "Ajoutez une section projets pour montrer votre expertise")
            ),
            improvedSummary = CvImprovedSummary(
                overallAssessment = "Votre CV est solide avec une bonne structure. Il pourrait être amélioré avec plus de détails quantifiables sur vos réalisations.",
                strengths = listOf(
                    "Expérience pertinente en développement Android",
                    "Compétences techniques à jour (Kotlin, Android SDK)",
                    "Formation académique solide"
                ),
                improvements = listOf(
                    "Ajouter des métriques quantifiables (ex: 'amélioré les performances de 30%')",
                    "Inclure des projets open source ou personnels",
                    "Détailler les méthodologies de travail (Agile, Scrum)"
                )
            ),
            profile = CvJobSeekerProfile(
                id = 1,
                fullName = "Jean Dupont",
                title = "Développeur Android Senior",
                nationality = "Française",
                description = "Développeur Android avec 5 ans d'expérience dans le développement d'applications mobiles. Expert en Kotlin, Java, Android SDK, et architecture MVVM.",
                skills = listOf("Kotlin", "Java", "Android SDK", "Retrofit", "Room", "Firebase"),
                experience = listOf(),
                education = listOf()
            )
        )

        displayResults(simulatedResult)
        showMessage("Mode simulation: données de démonstration affichées")
    }

    private fun displayResults(result: CvAnalysisResponseFull) {
        Log.d(TAG, "Affichage des résultats...")

        // Afficher les résultats
        resultsWrapper.visibility = View.VISIBLE

        // Profile
        profileName.text = "Nom: ${result.profile.fullName ?: "Non spécifié"}"
        profileTitle.text = "Titre: ${result.profile.title ?: "Non spécifié"}"
        profileNationality.text = "Nationalité: ${result.profile.nationality ?: "Non spécifiée"}"
        profileSummary.text = result.profile.description ?: "Aucun résumé professionnel"
        profileSkills.text = "Compétences: ${result.profile.skills.joinToString(", ")}"
        profileExperience.text = "Expérience: ${result.profile.experience.size} poste(s)"

        // Score
        cvScoreText.text = "${result.cvScore}%"

        // Suggestions
        displaySuggestions(result.cvSuggestions)

        // Improved Summary
        overallAssessment.text = result.improvedSummary.overallAssessment ?: "Aucune évaluation disponible"
        displayListItems(strengthsContainer, result.improvedSummary.strengths)
        displayListItems(improvementsContainer, result.improvedSummary.improvements)

        Log.d(TAG, "Résultats affichés avec succès")
    }

    private fun displayLegacyResults(result: CvAnalysisResponseLegacy) {
        Log.d(TAG, "Affichage des résultats legacy...")

        resultsWrapper.visibility = View.VISIBLE

        // Afficher les résultats du format legacy
        cvScoreText.text = "${result.score ?: 0}%"
        overallAssessment.text = result.analysis ?: "Analyse non disponible"

        // Suggestions simples
        result.suggestions?.let { suggestions ->
            displaySimpleSuggestions(suggestions)
        }

        // Remplir les autres champs avec des valeurs par défaut
        profileName.text = "Nom: Non disponible (format legacy)"
        profileTitle.text = "Titre: Non disponible (format legacy)"
        profileNationality.text = "Nationalité: Non disponible"
        profileSummary.text = "Résumé non disponible avec le format d'analyse basique"
        profileSkills.text = "Compétences: Non disponible"
        profileExperience.text = "Expérience: Non disponible"

        showMessage("Format d'analyse basique utilisé")
    }

    private fun displaySuggestions(suggestions: List<CvSuggestion>) {
        cvSuggestionsContainer.removeAllViews()

        if (suggestions.isEmpty()) {
            val noSuggestions = TextView(this).apply {
                text = "Aucune suggestion disponible"
                setTextColor(Color.parseColor("#6B7280"))
                setPadding(16, 16, 16, 16)
            }
            cvSuggestionsContainer.addView(noSuggestions)
            return
        }

        Log.d(TAG, "Affichage de ${suggestions.size} suggestions")

        suggestions.forEachIndexed { index, suggestion ->
            Log.d(TAG, "Suggestion $index: ${suggestion.type} - ${suggestion.title}")

            val suggestionLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 12, 16, 12)
                background = getDrawable(R.drawable.bg_suggestion)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 8
                }
            }

            // Icône selon le type
            val icon = TextView(this).apply {
                text = when (suggestion.type) {
                    "success" -> "✓"
                    "warning" -> "⚠"
                    "info" -> "ⓘ"
                    "missing" -> "✗"
                    else -> "•"
                }
                textSize = 18f
                setPadding(0, 0, 12, 0)
                setTextColor(
                    when (suggestion.type) {
                        "success" -> Color.parseColor("#059669")
                        "warning" -> Color.parseColor("#D97706")
                        "info" -> Color.parseColor("#3B82F6")
                        "missing" -> Color.parseColor("#DC2626")
                        else -> Color.parseColor("#6B7280")
                    }
                )
            }

            val textContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                }
            }

            if (!suggestion.title.isNullOrEmpty()) {
                val title = TextView(this).apply {
                    text = suggestion.title
                    textSize = 16f
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    setTextColor(Color.parseColor("#111827"))
                }
                textContainer.addView(title)
            }

            if (!suggestion.message.isNullOrEmpty()) {
                val message = TextView(this).apply {
                    text = suggestion.message
                    textSize = 14f
                    setTextColor(Color.parseColor("#6B7280"))
                    setPadding(0, 4, 0, 0)
                }
                textContainer.addView(message)
            }

            suggestionLayout.addView(icon)
            suggestionLayout.addView(textContainer)
            cvSuggestionsContainer.addView(suggestionLayout)
        }
    }

    private fun displaySimpleSuggestions(suggestions: List<String>) {
        cvSuggestionsContainer.removeAllViews()

        Log.d(TAG, "Affichage de ${suggestions.size} suggestions simples")

        suggestions.forEach { suggestion ->
            val textView = TextView(this).apply {
                text = "• $suggestion"
                textSize = 14f
                setTextColor(Color.parseColor("#6B7280"))
                setPadding(16, 8, 16, 8)
            }
            cvSuggestionsContainer.addView(textView)
        }
    }

    private fun displayListItems(container: LinearLayout, items: List<String>) {
        container.removeAllViews()

        Log.d(TAG, "Affichage de ${items.size} items dans $container")

        items.forEach { item ->
            val textView = TextView(this).apply {
                text = "• $item"
                textSize = 14f
                setTextColor(Color.parseColor("#374151"))
                setPadding(0, 6, 0, 6)
            }
            container.addView(textView)
        }
    }

    private fun showLoading(show: Boolean) {
        loadingIndicator.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            analyzeButton.text = "Analyse en cours..."
            analyzeButton.isEnabled = false
        } else {
            analyzeButton.text = "Analyser le CV"
            analyzeButton.isEnabled = pdfText.isNotEmpty()
        }
    }

    private fun toggleDarkMode() {
        isDarkMode = !isDarkMode

        if (isDarkMode) {
            // Mode sombre
            rootLayout.setBackgroundColor(Color.parseColor("#1F2937"))
            // Si vous n'avez pas les icônes, commentez ces lignes :
            // btnTheme.setImageResource(R.drawable.ic_sun)
        } else {
            // Mode clair
            rootLayout.setBackgroundColor(Color.parseColor("#F9FAFB"))
            // btnTheme.setImageResource(R.drawable.ic_moon)
        }
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
        return result ?: "fichier_inconnu.pdf"
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Erreur d'analyse")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}