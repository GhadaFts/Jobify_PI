package com.example.jobify

import android.content.Context
import android.content.SharedPreferences
import com.example.jobify.network.UserProfile

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("JobifyPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_KEYCLOAK_ID = "keycloak_id"
        private const val KEY_USER_ID = "user_id"
    }

    // Save complete user session with tokens (for API login)
    fun saveUserSession(
        accessToken: String,
        refreshToken: String,
        expiresIn: Int,
        email: String,
        name: String,
        role: String,
        keycloakId: String,
        userId: Long? = null
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (expiresIn * 1000))
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role)
            putString(KEY_KEYCLOAK_ID, keycloakId)
            userId?.let { putLong(KEY_USER_ID, it) }
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Save user profile
    fun saveUserProfile(profile: UserProfile) {
        prefs.edit().apply {
            profile.email?.let { putString(KEY_USER_EMAIL, it) }
            profile.fullName?.let { putString(KEY_USER_NAME, it) }
            profile.role?.let { putString(KEY_USER_ROLE, it) }
            profile.keycloakId?.let { putString(KEY_KEYCLOAK_ID, it) }
            profile.id?.let { putLong(KEY_USER_ID, it) }
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Get user profile
    fun getUserProfile(): UserProfile? {
        val keycloakId = getKeycloakId() ?: return null

        return UserProfile(
            id = getUserId(),
            keycloakId = keycloakId,
            fullName = getUserName(),
            email = getUserEmail(),
            role = getUserRole(),
            deleted = false,
            profilePicture = null,
            jobTitle = null
        )
    }

    // Save user session (backward compatibility - without tokens)
    fun saveUserSession(email: String, name: String, role: String) {
        prefs.edit().apply {
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Update tokens (for refresh)
    fun updateTokens(accessToken: String, refreshToken: String, expiresIn: Int) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (expiresIn * 1000))
            apply()
        }
    }

    // Get access token
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // Get refresh token
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    // Check if token is expired
    fun isTokenExpired(): Boolean {
        val expiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        return System.currentTimeMillis() >= expiryTime
    }

    // Get keycloak ID
    fun getKeycloakId(): String? {
        return prefs.getString(KEY_KEYCLOAK_ID, null)
    }

    // Get user ID
    fun getUserId(): Long? {
        val id = prefs.getLong(KEY_USER_ID, -1)
        return if (id != -1L) id else null
    }

    // Get authorization header
    fun getAuthHeader(): String? {
        val token = getAccessToken()
        return if (token != null) "Bearer $token" else null
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Get user role
    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    // Get user name
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    // Get user email
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    // Clear session (logout)
    fun clearSession() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }

    // Get user data as a map
    fun getUserData(): Map<String, String?> {
        return mapOf(
            "email" to getUserEmail(),
            "name" to getUserName(),
            "role" to getUserRole(),
            "keycloakId" to getKeycloakId()
        )
    }
}