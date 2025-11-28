package com.example.jobify.network

import retrofit2.Call
import retrofit2.http.*

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
    val id: Long,
    val keycloakId: String,
    val fullName: String,
    val email: String,
    val role: String,
    val deleted: Boolean = false
)

data class LogoutResponse(
    val message: String
)

data class ErrorResponse(
    val message: String,
    val statusCode: Int? = null,
    val errorMessage: String? = null
)