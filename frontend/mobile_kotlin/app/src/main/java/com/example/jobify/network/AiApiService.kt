package com.example.jobify.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// ===== INTERVIEW BOT MODELS =====
data class ConversationContext(
    val phase: String, // "collect_info" | "advice" | "practice"
    val currentStep: String? = null,
    val userProfile: UserProfileData? = null,
    val questions: List<String>? = null
)

data class UserProfileData(
    val jobTitle: String? = null,
    val interviewType: String? = null, // "presentiel" | "en_ligne" | "hybride"
    val experienceLevel: String? = null,
    val skills: List<String>? = null,
    val industry: String? = null,
    val companyType: String? = null,
    val specificConcerns: List<String>? = null
)

data class ChatRequest(
    val message: String,
    val conversationContext: ConversationContext? = null,
    val userProfile: UserProfileData? = null
)

data class ChatResponse(
    val response: String,
    val conversationPhase: String,
    val nextStep: String? = null,
    val userProfileUpdates: Map<String, Any>? = null,
    val suggestions: List<String>? = null,
    val questions: List<String>? = null
)

// ===== CV CORRECTION MODELS (Renommées) =====
data class CvExperience(
    val id: Int? = null,
    val company: String? = null,
    val position: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val description: String? = null,
    val isCurrent: Boolean? = null
)

data class CvEducation(
    val id: Int? = null,
    val institution: String? = null,
    val degree: String? = null,
    val fieldOfStudy: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val description: String? = null
)

data class CvJobSeekerProfile(
    val id: Int = 0,
    val email: String? = null,
    val password: String? = null,
    val fullName: String? = null,
    val role: String? = null,
    val photoProfil: String? = null,
    val twitterLink: String? = null,
    val webLink: String? = null,
    val githubLink: String? = null,
    val facebookLink: String? = null,
    val description: String? = null,
    val phoneNumber: String? = null,
    val nationality: String? = null,
    val skills: List<String> = emptyList(),
    val experience: List<CvExperience> = emptyList(),
    val education: List<CvEducation> = emptyList(),
    val title: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null
)

data class CvSuggestion(
    val id: String? = null,
    val type: String, // "success", "warning", "info", "missing"
    val title: String? = null,
    val message: String? = null
)

data class CvImprovedSummary(
    val overallAssessment: String? = null,
    val strengths: List<String> = emptyList(),
    val improvements: List<String> = emptyList()
)

data class CvAnalysisResponseFull(
    val cvScore: Int,
    val cvSuggestions: List<CvSuggestion> = emptyList(),
    val improvedSummary: CvImprovedSummary,
    val profile: CvJobSeekerProfile
)

data class CvAnalysisRequest(
    val cvContent: String,
    val jobDescription: String? = null
)

data class CvAnalysisResponseLegacy(
    val analysis: String? = null,
    val suggestions: List<String>? = null,
    val score: Int? = null
)

// ===== APPLICATION RANKING MODELS =====
data class JobSeekerAIRequest(
    val id: Int,
    val email: String,
    val fullName: String,
    val description: String,
    val nationality: String,
    val skills: List<String>,
    val experience: String,
    val education: String,
    val title: String,
    val date_of_birth: String,
    val gender: String
)

data class ApplicationAIRequest(
    val id: Int,
    val applicationDate: String,
    val status: String,
    val motivation_lettre: String,
    val jobSeeker: JobSeekerAIRequest,
    val jobOfferId: String
)

data class JobOfferAIRequest(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val type: String,
    val experience: String,
    val salary: String,
    val description: String,
    val skills: List<String>,
    val requirements: List<String>,
    val applications: List<ApplicationAIRequest>
)

data class ApplicationScore(
    val id: Int,
    val score: Double
)

data class AIRankingResponse(
    val id: String,
    val applications: List<ApplicationScore>
)

data class RankingValidationResponse(
    val valid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)

data class HealthResponse(
    val status: String,
    val timestamp: String? = null,
    val service: String,
    val version: String? = null
)

interface AiApiService {

    // ===== INTERVIEW BOT ENDPOINTS =====

    @POST("ai-service/interview-bot/chat")
    suspend fun chatWithInterviewBot(@Body request: ChatRequest): Response<ChatResponse>

    @POST("ai-service/interview-bot/chat")
    fun chatWithInterviewBotCall(@Body request: ChatRequest): Call<ChatResponse>

    // ===== CV CORRECTION ENDPOINTS =====

    @POST("ai-service/cv-correction/analyze")
    suspend fun analyzeCvFull(@Body request: CvAnalysisRequest): Response<CvAnalysisResponseFull>

    @POST("ai-service/cv-correction/analyze")
    fun analyzeCvFullCall(@Body request: CvAnalysisRequest): Call<CvAnalysisResponseFull>

    @POST("ai-service/cv-correction/analyze")
    suspend fun analyzeCv(@Body request: CvAnalysisRequest): Response<CvAnalysisResponseLegacy>

    @POST("ai-service/cv-correction/analyze")
    fun analyzeCvCall(@Body request: CvAnalysisRequest): Call<CvAnalysisResponseLegacy>

    // ===== APPLICATION RANKING ENDPOINTS =====

    @POST("ai-service/application-ranking/rank")
    suspend fun rankApplications(@Body jobOfferData: JobOfferAIRequest): Response<AIRankingResponse>

    @POST("ai-service/application-ranking/rank")
    fun rankApplicationsCall(@Body jobOfferData: JobOfferAIRequest): Call<AIRankingResponse>

    @POST("ai-service/application-ranking/validate")
    suspend fun validateRankingRequest(@Body jobOfferData: JobOfferAIRequest): Response<RankingValidationResponse>

    @POST("ai-service/application-ranking/validate")
    fun validateRankingRequestCall(@Body jobOfferData: JobOfferAIRequest): Call<RankingValidationResponse>

    @GET("ai-service/application-ranking/health")
    suspend fun checkRankingHealth(): Response<HealthResponse>

    @GET("ai-service/application-ranking/health")
    fun checkRankingHealthCall(): Call<HealthResponse>
}