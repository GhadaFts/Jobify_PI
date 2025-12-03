package com.example.jobify.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {

    /**
     * Get user profile (returns RecruiterProfile for recruiters)
     */
    @GET("auth-service/user/profile")
    fun getUserProfile(@Header("Authorization") token: String): Call<RecruiterProfile>

    /**
     * Get user profile (returns JobSeekerProfile for job seekers)
     */
    @GET("auth-service/user/profile")
    fun getJobSeekerProfile(@Header("Authorization") token: String): Call<JobSeekerProfile>

    /**
     * Update user profile
     */
    @PUT("auth-service/user/profile")
    fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body profileData: UpdateProfileRequest
    ): Call<RecruiterProfile>

    /**
     * Upload profile photo
     */
    @Multipart
    @POST("auth-service/user/upload-photo")
    fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<PhotoUploadResponse>

    /**
     * Get recruiter profile by Keycloak ID (public endpoint)
     */
    @GET("auth-service/user/{keycloakId}/public")
    fun getRecruiterProfilePublic(@Path("keycloakId") keycloakId: String): Call<RecruiterProfile>
}

// Data Models
data class RecruiterProfile(
    val id: Long,
    val keycloakId: String?,
    val email: String,
    val fullName: String,
    val role: String,
    val photo_profil: String?,
    val twitter_link: String?,
    val web_link: String?,
    val github_link: String?,
    val facebook_link: String?,
    val description: String?,
    val phone_number: String?,
    val nationality: String?,
    val companyAddress: String?,
    val domaine: String?,
    val employees_number: Int?,
    val service: List<String>?
)

data class JobSeekerProfile(
    val id: Long,
    val keycloakId: String?,
    val email: String,
    val fullName: String,
    val role: String,
    val photo_profil: String?,
    val twitter_link: String?,
    val web_link: String?,
    val github_link: String?,
    val facebook_link: String?,
    val description: String?,
    val phone_number: String?,
    val nationality: String?,
    val title: String?,
    val date_of_birth: String?,
    val gender: String?,
    val skills: List<String>?,
    val experience: List<Experience>?,
    val education: List<Education>?
)

data class Experience(
    val position: String,
    val company: String,
    val startDate: String,
    val endDate: String,
    val description: String
)

data class Education(
    val degree: String,
    val field: String,
    val school: String,
    val graduationDate: String
)

data class UpdateProfileRequest(
    val fullName: String,
    val email: String,
    val title: String? = null,
    val gender: String? = null,
    val date_of_birth: String? = null,
    val photo_profil: String? = null,
    val twitter_link: String? = null,
    val web_link: String? = null,
    val github_link: String? = null,
    val facebook_link: String? = null,
    val description: String? = null,
    val phone_number: String? = null,
    val nationality: String? = null,
    val companyAddress: String? = null,
    val domaine: String? = null,
    val employees_number: Int? = null,
    val service: List<String>? = null,
    val skills: List<String>? = null,
    val experience: List<Experience>? = null,
    val education: List<Education>? = null
)

data class PhotoUploadResponse(
    val url: String,
    val message: String
)

