package com.example.jobify.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// Request data class
data class CareerAnalysisRequest(
    val country: String,
    val education: String,
    val certificate: String,
    val skills: String? = null
)

// Response data class
data class CareerAnalysisResponse(
    val advice: String,
    val model: String? = null,
    val version: String? = null
)

// Health check response
data class HealthCheckResponse(
    val status: String,
    val service: String
)

interface CareerApiService {
    // Analyze career with AI
    @POST("career-advice/analyze")
    suspend fun analyzeCareer(@Body request: CareerAnalysisRequest): Response<CareerAnalysisResponse>

    // Analyze career with Call (for non-coroutine usage)
    @POST("career-advice/analyze")
    fun analyzeCareerCall(@Body request: CareerAnalysisRequest): Call<CareerAnalysisResponse>

    // Health check
    @GET("career-advice/health")
    suspend fun healthCheck(): Response<HealthCheckResponse>

    // Health check with Call
    @GET("career-advice/health")
    fun healthCheckCall(): Call<HealthCheckResponse>
}