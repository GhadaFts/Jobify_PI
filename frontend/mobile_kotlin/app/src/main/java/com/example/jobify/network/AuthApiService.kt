package com.example.jobify.network

import retrofit2.Call
import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import okhttp3.ResponseBody

interface AuthApiService {

    @POST("auth-service/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth-service/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("auth-service/auth/profile")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserProfile>

    @POST("auth-service/auth/refresh")
    fun refreshToken(@Body refreshRequest: RefreshTokenRequest): Call<LoginResponse>

    @POST("auth-service/auth/logout")
    fun logout(
        @Header("Authorization") token: String,
        @Body logoutRequest: LogoutRequest
    ): Call<LogoutResponse>

    // Get user by Keycloak ID (public endpoint used by other microservices)
    @GET("auth-service/auth/users/{keycloakId}")
    suspend fun getUserByKeycloakId(@Path("keycloakId") keycloakId: String): retrofit2.Response<UserProfile>

    // Raw response variant for debugging (returns raw JSON body)
    @GET("auth-service/auth/users/{keycloakId}")
    suspend fun getUserByKeycloakIdRaw(@Path("keycloakId") keycloakId: String): retrofit2.Response<ResponseBody>

    // Alternate endpoints used by other backend variants (auth-service2)
    @GET("auth-service/user/{keycloakId}")
    suspend fun getUserByKeycloakIdAlt(@Path("keycloakId") keycloakId: String): retrofit2.Response<UserProfile>

    @GET("auth-service/user/{keycloakId}")
    suspend fun getUserByKeycloakIdAltRaw(@Path("keycloakId") keycloakId: String): retrofit2.Response<ResponseBody>

    @GET("auth-service/user/{keycloakId}/public")
    suspend fun getUserPublicProfile(@Path("keycloakId") keycloakId: String): retrofit2.Response<UserProfile>

    @GET("auth-service/user/{keycloakId}/public")
    suspend fun getUserPublicProfileRaw(@Path("keycloakId") keycloakId: String): retrofit2.Response<ResponseBody>
}

// Request Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String = "job_seeker" // Changed default to match backend format
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class LogoutRequest(
    val refreshToken: String
)

// Response Models
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val tokenType: String
)

data class RegisterResponse(
    val message: String? = null,
    val keycloakId: String,
    val email: String? = null,
    val role: String? = null,
    val success: Boolean? = null
)

data class UserProfile(
    val id: Long?,
    val keycloakId: String?,
    @SerializedName(value = "fullName", alternate = ["full_name"]) 
    val fullName: String?,
    val email: String?,
    val role: String?,
    val deleted: Boolean = false,
    @SerializedName(value = "profilePicture", alternate = ["photo_profil"]) 
    val profilePicture: String? = null,
    @SerializedName(value = "jobTitle", alternate = ["title"]) 
    val jobTitle: String? = null,
    // Some backends use "title" instead of "jobTitle"; handled via alternate on jobTitle
    // Contact and personal fields returned by auth-service (map snake_case alternates)
    @SerializedName(value = "phoneNumber", alternate = ["phone_number"]) 
    val phoneNumber: String? = null,
    val nationality: String? = null,
    @SerializedName(value = "dateOfBirth", alternate = ["date_of_birth"]) 
    val dateOfBirth: String? = null,
    val gender: String? = null,
    // Socials
    @SerializedName(value = "githubLink", alternate = ["github_link"]) 
    val githubLink: String? = null,
    @SerializedName(value = "webLink", alternate = ["web_link"]) 
    val webLink: String? = null,
    @SerializedName(value = "twitterLink", alternate = ["twitter_link"]) 
    val twitterLink: String? = null,
    @SerializedName(value = "facebookLink", alternate = ["facebook_link"]) 
    val facebookLink: String? = null,
    // Additional arrays that may be returned
    val skills: List<String>? = null,
    // Optional structured profile sections returned by some auth-service variants
    val experience: List<Map<String, Any>>? = null,
    val education: List<Map<String, Any>>? = null
)

data class LogoutResponse(
    val message: String
)

data class ErrorResponse(
    val message: String,
    val statusCode: Int? = null,
    val errorMessage: String? = null
)