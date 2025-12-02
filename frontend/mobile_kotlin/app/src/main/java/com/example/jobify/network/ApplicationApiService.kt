package com.example.jobify.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApplicationApiService {
    // Get all applications (RECRUITER/ADMIN only)
    @GET("application-service/api/applications")
    suspend fun getAllApplications(): Response<List<Map<String, Any>>>

    // Get application by ID
    @GET("application-service/api/applications/{id}")
    suspend fun getApplicationById(@Path("id") id: String): Response<Map<String, Any>>

    // Get application by ID (NON-SUSPEND version for callbacks)
    @GET("application-service/api/applications/{id}")
    fun getApplicationByIdCall(@Path("id") id: String): Call<Map<String, Any>>

    // Get applications by job offer ID (RECRUITER/ADMIN only)
    @GET("application-service/api/applications/joboffer/{jobOfferId}")
    suspend fun getByJobOffer(@Path("jobOfferId") jobOfferId: String): Response<List<Map<String, Any>>>

    // Get my applications (JOB_SEEKER only)
    @GET("application-service/api/applications/my-applications")
    suspend fun getMyApplications(): Response<List<Map<String, Any>>>

    // Create a new application (JOB_SEEKER only)
    @POST("application-service/api/applications")
    suspend fun createApplication(@Body payload: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>

    // Create application with Call (for non-coroutine usage)
    @POST("application-service/api/applications")
    fun createApplicationCall(@Body payload: Map<String, @JvmSuppressWildcards Any>): Call<Map<String, Any>>

    // Update application partially (JOB_SEEKER only - own applications)
    @PATCH("application-service/api/applications/{id}")
    suspend fun updateApplication(
        @Path("id") id: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, Any>>

    // Update application status (RECRUITER only)
    @PATCH("application-service/api/applications/{id}/status")
    suspend fun updateApplicationStatus(
        @Path("id") id: String,
        @Body payload: Map<String, String>
    ): Response<Map<String, Any>>

    // Update AI score (RECRUITER/ADMIN only)
    @PATCH("application-service/api/applications/{id}/ai-score")
    suspend fun updateAiScore(
        @Path("id") id: String,
        @Body payload: Map<String, Int>
    ): Response<Map<String, Any>>

    // Delete application (JOB_SEEKER only - own applications)
    @DELETE("application-service/api/applications/{id}")
    suspend fun deleteApplication(@Path("id") id: String): Response<Void>

    // Check if duplicate application exists
    @GET("application-service/api/applications/check-duplicate")
    suspend fun checkDuplicateApplication(
        @Query("jobOfferId") jobOfferId: String,
        @Query("jobSeekerId") jobSeekerId: String
    ): Response<Boolean>

    // Check duplicate with Call (for non-coroutine usage)
    @GET("application-service/api/applications/check-duplicate")
    fun checkDuplicateApplicationCall(
        @Query("jobOfferId") jobOfferId: String,
        @Query("jobSeekerId") jobSeekerId: String
    ): Call<Boolean>
}