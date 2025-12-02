package com.example.jobify.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// Retrofit interface for job offer related endpoints proxied through the gateway
interface JobApiService {
    // Get all jobs
    @GET("joboffer-service/api/jobs")
    suspend fun getAllJobs(): Response<List<Map<String, Any>>>

    // Get jobs for the authenticated recruiter
    @GET("joboffer-service/api/jobs/my-jobs")
    suspend fun getMyJobs(): Response<List<Map<String, Any>>>

    // Create a job offer
    @POST("joboffer-service/api/jobs")
    suspend fun createJob(@Body payload: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>

    // Update an existing job
    @PUT("joboffer-service/api/jobs/{id}")
    suspend fun updateJob(@Path("id") id: String, @Body payload: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>

    // Upload company logo
    @Multipart
    @POST("joboffer-service/api/uploads/company-logo")
    suspend fun uploadLogo(@Part file: MultipartBody.Part): Response<Map<String, Any>>

    // Get job by ID (NON-SUSPEND version for callbacks)
    @GET("joboffer-service/api/jobs/{id}")
    fun getJobById(@Path("id") id: String): Call<Map<String, Any>>

    // Get job by ID (SUSPEND version for coroutines)
    @GET("joboffer-service/api/jobs/{id}")
    suspend fun getJobByIdSuspend(@Path("id") id: String): Response<Map<String, Any>>
}