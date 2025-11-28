package com.example.jobify.repository

import android.util.Log
import com.example.jobify.network.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {

    private val authService = ApiClient.authService

    fun login(
        email: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val loginRequest = LoginRequest(email, password)

        authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        Log.d("AuthRepository", "Login successful: ${loginResponse.accessToken}")
                        onSuccess(loginResponse)
                    } ?: run {
                        onError("Empty response from server")
                    }
                } else {
                    val errorMessage = parseErrorMessage(response)
                    Log.e("AuthRepository", "Login failed: $errorMessage")
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val errorMessage = "Network error: ${t.message}"
                Log.e("AuthRepository", errorMessage, t)
                onError(errorMessage)
            }
        })
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        role: String,
        onSuccess: (RegisterResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val registerRequest = RegisterRequest(fullName, email, password, role)

        authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        Log.d("AuthRepository", "Registration successful")
                        onSuccess(registerResponse)
                    } ?: run {
                        onError("Empty response from server")
                    }
                } else {
                    val errorMessage = parseErrorMessage(response)
                    Log.e("AuthRepository", "Registration failed: $errorMessage")
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val errorMessage = "Network error: ${t.message}"
                Log.e("AuthRepository", errorMessage, t)
                onError(errorMessage)
            }
        })
    }

    fun getUserProfile(
        accessToken: String,
        onSuccess: (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val authHeader = "Bearer $accessToken"

        authService.getUserProfile(authHeader).enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        Log.d("AuthRepository", "Profile loaded: ${profile.fullName}")
                        onSuccess(profile)
                    } ?: run {
                        onError("Empty profile response")
                    }
                } else {
                    val errorMessage = parseErrorMessage(response)
                    Log.e("AuthRepository", "Failed to load profile: $errorMessage")
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                val errorMessage = "Network error: ${t.message}"
                Log.e("AuthRepository", errorMessage, t)
                onError(errorMessage)
            }
        })
    }

    fun refreshToken(
        refreshToken: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val refreshRequest = RefreshTokenRequest(refreshToken)

        authService.refreshToken(refreshRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { tokens ->
                        Log.d("AuthRepository", "Token refreshed successfully")
                        onSuccess(tokens)
                    } ?: run {
                        onError("Empty response from server")
                    }
                } else {
                    val errorMessage = parseErrorMessage(response)
                    Log.e("AuthRepository", "Token refresh failed: $errorMessage")
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val errorMessage = "Network error: ${t.message}"
                Log.e("AuthRepository", errorMessage, t)
                onError(errorMessage)
            }
        })
    }

    fun logout(
        accessToken: String,
        refreshToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val authHeader = "Bearer $accessToken"
        val logoutRequest = LogoutRequest(refreshToken)

        authService.logout(authHeader, logoutRequest).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful) {
                    Log.d("AuthRepository", "Logout successful")
                    onSuccess()
                } else {
                    // Even if logout fails on server, we should clear local session
                    Log.w("AuthRepository", "Logout failed on server, but clearing local session")
                    onSuccess()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Log.w("AuthRepository", "Logout network error, but clearing local session")
                // Clear local session even on network error
                onSuccess()
            }
        })
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message
            } else {
                "Unknown error occurred"
            }
        } catch (e: Exception) {
            when (response.code()) {
                401 -> "Invalid email or password"
                409 -> "User with this email already exists"
                400 -> "Invalid request. Please check your input"
                500 -> "Server error. Please try again later"
                else -> "Error: ${response.code()}"
            }
        }
    }
}