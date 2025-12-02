package com.example.jobify.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

data class InterviewRequestDTO(
    val applicationId: String,
    val jobSeekerId: String?,
    val scheduledDate: String, // LocalDateTime format: "yyyy-MM-dd'T'HH:mm:ss"
    val duration: Int,
    val location: String?,
    val interviewType: String, // "REMOTE" or "ON_SITE"
    val notes: String?,
    val meetingLink: String?
)

data class InterviewResponseDTO(
    val id: Long,
    val applicationId: String,
    val jobSeekerId: String,
    val recruiterId: String,
    val scheduledDate: String,
    val duration: Int,
    val location: String?,
    val interviewType: String,
    val status: String,
    val notes: String?,
    val meetingLink: String?,
    val createdAt: String,
    val updatedAt: String
)

interface InterviewApiService {
    /**
     * Schedule a new interview (RECRUITER only)
     */
    @POST("/interview-service/api/interviews")
    suspend fun scheduleInterview(@Body request: InterviewRequestDTO): Response<InterviewResponseDTO>

    /**
     * Get interview by ID (RECRUITER, JOB_SEEKER)
     */
    @GET("/interview-service/api/interviews/{id}")
    suspend fun getInterviewById(@Path("id") id: Long): Response<InterviewResponseDTO>

    /**
     * Get interviews by application ID
     */
    @GET("/interview-service/api/interviews/application/{applicationId}")
    suspend fun getInterviewsByApplicationId(@Path("applicationId") applicationId: String): Response<List<InterviewResponseDTO>>

    /**
     * Get recruiter's interviews
     */
    @GET("/interview-service/api/interviews/recruiter/my-interviews")
    suspend fun getRecruiterInterviews(): Response<List<InterviewResponseDTO>>

    /**
     * Get recruiter's upcoming interviews
     */
    @GET("/interview-service/api/interviews/recruiter/my-interviews/upcoming")
    suspend fun getRecruiterUpcomingInterviews(): Response<List<InterviewResponseDTO>>

    /**
     * Update interview (RECRUITER only)
     */
    @PUT("/interview-service/api/interviews/{id}")
    suspend fun updateInterview(
        @Path("id") id: Long,
        @Body updateData: Map<String, @JvmSuppressWildcards Any>
    ): Response<InterviewResponseDTO>

    /**
     * Cancel interview (RECRUITER only)
     */
    @DELETE("/interview-service/api/interviews/{id}")
    suspend fun cancelInterview(@Path("id") id: Long): Response<Void>

    /**
     * Get job seeker's upcoming interviews (NON-SUSPEND version for callbacks)
     */
    @GET("/interview-service/api/interviews/my-interviews/upcoming")
    fun getMyUpcomingInterviews(): Call<List<InterviewResponseDTO>>

    /**
     * Get job seeker's upcoming interviews (SUSPEND version for coroutines)
     */
    @GET("/interview-service/api/interviews/my-interviews/upcoming")
    suspend fun getMyUpcomingInterviewsSuspend(): Response<List<InterviewResponseDTO>>
}
