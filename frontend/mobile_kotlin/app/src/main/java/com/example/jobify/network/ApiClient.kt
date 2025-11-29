package com.example.jobify.network

import com.example.jobify.MyApp
import com.example.jobify.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // IMPORTANT: Update this URL based on your setup
    // For Android Emulator: use 10.0.2.2 (this maps to localhost on your computer)
    // For Physical Device: use your computer's IP address (e.g., 192.168.1.100)
    // The gateway should be running on port 8888
    private const val BASE_URL = "http://10.0.2.2:8888/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // Attach Content-Type and Authorization header if token available
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .addHeader("Content-Type", "application/json")

            try {
                val session = SessionManager(MyApp.instance)
                val token = session.getAccessToken()
                if (!token.isNullOrEmpty()) {
                    // Log masked token presence for debugging (do not log full token)
                    val masked = if (token.length > 8) token.substring(0, 4) + "..." + token.takeLast(4) else "<masked>"
                    Log.i("ApiClient", "Attaching Authorization header (masked=$masked)")
                    builder.addHeader("Authorization", "Bearer $token")
                } else {
                    Log.d("ApiClient", "No access token present for request to ${original.url}")
                }
            } catch (e: Exception) {
                Log.e("ApiClient", "Failed to read session token: ${e.message}")
                // If for some reason we cannot access session, proceed without Authorization
            }

            val request = builder.build()
            chain.proceed(request)
        }
        .authenticator(Authenticator { route: Route?, response ->
            // Try to refresh token synchronously when we get a 401
            try {
                val session = SessionManager(MyApp.instance)
                val currentRefresh = session.getRefreshToken()
                if (currentRefresh.isNullOrEmpty()) return@Authenticator null

                // Create a small Retrofit instance using a plain OkHttp client to avoid interceptor loops
                val refreshClient = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()

                val refreshRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(refreshClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val refreshService = refreshRetrofit.create(AuthApiService::class.java)
                val call = refreshService.refreshToken(RefreshTokenRequest(currentRefresh))
                val resp = call.execute()
                if (resp.isSuccessful) {
                    val tokens = resp.body()
                    if (tokens != null) {
                        // Save new tokens
                        session.updateTokens(tokens.accessToken, tokens.refreshToken, tokens.expiresIn)

                        // Retry the original request with the new token
                        val newRequest: Request = response.request.newBuilder()
                            .header("Authorization", "Bearer ${tokens.accessToken}")
                            .build()
                        return@Authenticator newRequest
                    }
                }
                // If refresh failed, clear local session so UI reacts (will force login)
                Log.w("ApiClient", "Token refresh failed or returned empty; clearing session")
                session.clearSession()
            } catch (e: Exception) {
                Log.e("ApiClient", "Exception during token refresh: ${e.message}", e)
                // ignore and give up (will result in original 401)
            }
            null
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val jobService: JobApiService = retrofit.create(JobApiService::class.java)
    val applicationService: ApplicationApiService = retrofit.create(ApplicationApiService::class.java)
}