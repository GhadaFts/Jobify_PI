package com.example.jobify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jobify.model.ChatHelper
import com.example.jobify.model.ChatMessage
import com.example.jobify.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class InterviewPreparationActivity : BaseDrawerActivity() {

    private lateinit var scrollViewRoot: ScrollView
    private lateinit var btnDownloadPdf: Button

    // Chat components
    private lateinit var welcomeScreen: LinearLayout
    private lateinit var chatContainer: LinearLayout
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var chatbotLogo: ImageView

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    // Conversation state - Utiliser UserProfileData (pas UserProfile)
    private var conversationContext = ConversationContext(phase = "collect_info")
    private var userProfile = UserProfileData() // Changé ici
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interview_preparation)

        initViews()
        setupChat()
        setupDownloadPdf()
        startOpeningSequence()
    }

    private fun initViews() {
        scrollViewRoot = findViewById(R.id.scrollViewRoot)
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)

        welcomeScreen = findViewById(R.id.welcomeScreen)
        chatContainer = findViewById(R.id.chatContainer)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        inputMessage = findViewById(R.id.inputMessage)
        btnSend = findViewById(R.id.btnSend)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        chatbotLogo = findViewById(R.id.chatbotLogo)
    }

    private fun setupChat() {
        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@InterviewPreparationActivity)
            adapter = chatAdapter
        }

        btnSend.setOnClickListener {
            sendMessage()
        }

        inputMessage.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun startOpeningSequence() {
        welcomeScreen.visibility = View.VISIBLE
        chatContainer.visibility = View.GONE

        // Animation du logo
        chatbotLogo.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(1000)
            .withEndAction {
                chatbotLogo.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(1000)
                    .start()
            }
            .start()

        // Transition vers le chat après 3 secondes
        Handler(Looper.getMainLooper()).postDelayed({
            welcomeScreen.visibility = View.GONE
            chatContainer.visibility = View.VISIBLE
            startConversation()
        }, 3000)
    }

    private fun startConversation() {
        val welcomeMessages = listOf(
            "🎯 **Bonjour ! Je suis votre coach IA pour la préparation aux entretiens.**",
            "Je vais vous aider à vous préparer en 3 étapes :",
            "1. 🧩 **Analyse de votre profil** - Pour comprendre vos besoins",
            "2. 💡 **Conseils personnalisés** - Adaptés à votre situation",
            "3. 🎬 **Simulation d'entretien** - Pour vous entraîner en conditions réelles",
            "Commençons par faire connaissance... Parlez-moi du poste que vous visez !"
        )

        welcomeMessages.forEachIndexed { index, msg ->
            Handler(Looper.getMainLooper()).postDelayed({
                addBotMessage(msg)
            }, (index * 1000).toLong())
        }
    }

    private fun sendMessage() {
        val messageText = inputMessage.text.toString().trim()

        if (messageText.isEmpty() || isLoading) return

        addUserMessage(messageText)
        inputMessage.text.clear()

        // Check anti-frustration
        if (shouldResetConversation()) {
            addBotMessage("🔄 **Je vois que je répète mes questions - désolé !**\n\nPassons directement à l'étape suivante avec les informations que vous m'avez déjà données.")
            conversationContext = ConversationContext(phase = "advice")
            return
        }

        isLoading = true
        loadingIndicator.visibility = View.VISIBLE
        btnSend.isEnabled = false

        val request = ChatRequest(
            message = messageText,
            conversationContext = conversationContext,
            userProfile = userProfile // userProfile est de type UserProfileData
        )

        ApiClient.aiService.chatWithInterviewBotCall(request).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                isLoading = false
                loadingIndicator.visibility = View.GONE
                btnSend.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val chatResponse = response.body()!!

                    // Update conversation context
                    conversationContext = ConversationContext(
                        phase = chatResponse.conversationPhase,
                        currentStep = chatResponse.nextStep,
                        userProfile = userProfile, // userProfile est UserProfileData
                        questions = chatResponse.questions
                    )

                    // Update user profile
                    chatResponse.userProfileUpdates?.let { updates ->
                        userProfile = userProfile.copy(
                            jobTitle = updates["jobTitle"] as? String ?: userProfile.jobTitle,
                            interviewType = updates["interviewType"] as? String ?: userProfile.interviewType,
                            experienceLevel = updates["experienceLevel"] as? String ?: userProfile.experienceLevel,
                            industry = updates["industry"] as? String ?: userProfile.industry,
                            companyType = updates["companyType"] as? String ?: userProfile.companyType
                        )
                    }

                    addBotMessage(chatResponse.response)
                } else {
                    Log.e("InterviewChat", "API Error: ${response.code()} - ${response.message()}")
                    addBotMessage("😔 **Désolé, je rencontre un problème technique.**\n*Veuillez réessayer dans quelques instants.*")
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                isLoading = false
                loadingIndicator.visibility = View.GONE
                btnSend.isEnabled = true

                Log.e("InterviewChat", "Network Error: ${t.message}", t)
                addBotMessage("😔 **Désolé, je rencontre un problème de connexion.**\n*Veuillez vérifier votre connexion Internet.*")
            }
        })
    }

    private fun addUserMessage(text: String) {
        val message = ChatMessage(text, isUser = true)
        chatAdapter.addMessage(message)
        scrollToBottom()
    }

    private fun addBotMessage(text: String) {
        val message = ChatMessage(text, isUser = false)
        chatAdapter.addMessage(message)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        Handler(Looper.getMainLooper()).postDelayed({
            chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }, 100)
    }

    private fun shouldResetConversation(): Boolean {
        if (messages.size < 4) return false

        val lastUserMessage = messages.filter { it.isUser }.lastOrNull()
        val hasRepetition = ChatHelper.detectRepetition(messages)
        val userFrustrated = lastUserMessage?.let {
            ChatHelper.detectFrustration(it.text)
        } ?: false

        return hasRepetition && userFrustrated
    }

    private fun resetConversation() {
        messages.clear()
        chatAdapter.clear()
        conversationContext = ConversationContext(phase = "collect_info")
        userProfile = UserProfileData() // Changé ici aussi

        addBotMessage("🔄 **Conversation réinitialisée !**\n\nParlez-moi du poste que vous visez et je vous aiderai à préparer votre entretien.")
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

    override fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}