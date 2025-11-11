package com.example.jobify

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("JobifyPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
    }

    // Save user session
    fun saveUserSession(email: String, name: String, role: String) {
        prefs.edit().apply {
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
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
            "role" to getUserRole()
        )
    }
}