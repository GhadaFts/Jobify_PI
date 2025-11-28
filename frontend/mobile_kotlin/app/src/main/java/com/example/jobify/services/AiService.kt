// services/AiService.kt
package com.example.jobify.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class CvAnalyzeRequest(
    val cvContent: String,
    val jobDescription: String? = null
)

data class CvAnalyzeResponse(
    val cvScore: Int,
    val cvSuggestions: List<CvSuggestion>,
    val improvedSummary: ImprovedSummary,
    val profile: Profile
)

data class CvSuggestion(
    val id: String,
    val type: String, // "success" | "warning" | "info" | "missing"
    val title: String,
    val message: String
)

data class ImprovedSummary(
    val overallAssessment: String,
    val strengths: List<String>,
    val improvements: List<String>
)

data class Profile(
    val fullName: String?,
    val title: String?,
    val email: String?,
    val phone_number: String?,
    val nationality: String?,
    val description: String?,
    val skills: List<String>,
    val experience: List<Experience>,
    val education: List<Education>
)

data class Experience(
    val startDate: String,
    val endDate: String?,
    val company: String,
    val position: String,
    val description: String
)

data class Education(
    val degree: String,
    val school: String,
    val startDate: String,
    val endDate: String
)

interface AiApi {
    @POST("/cv-correction/analyze")
    suspend fun analyzeCv(@Body request: CvAnalyzeRequest): Response<CvAnalyzeResponse>
}

object AiService {
    private const val BASE_URL = "http://localhost:8888/ai-service" // même URL que Angular

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .client(okhttp3.OkHttpClient.Builder().build())
        .build()

    private val api = retrofit.create(AiApi::class.java)

    suspend fun analyzeCv(cvText: String, jobDescription: String? = null): CvAnalyzeResponse {
        val request = CvAnalyzeRequest(cvText, jobDescription)
        val response = api.analyzeCv(request)
        if (!response.isSuccessful) {
            throw AiServiceException("Erreur serveur: ${response.code()} ${response.message()}")
        }
        return response.body() ?: throw AiServiceException("Réponse vide")
    }
}

class AiServiceException(message: String) : Exception(message)