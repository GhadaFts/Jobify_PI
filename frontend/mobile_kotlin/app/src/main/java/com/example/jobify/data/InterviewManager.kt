package com.example.jobify.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.jobify.model.ScheduledInterview
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Simple manager for scheduled interviews using both static list and SharedPreferences
 * Static list for real-time access, SharedPreferences for persistence
 */
object InterviewManager {
    // Static list shared across entire app
    private val interviewsList = mutableListOf<ScheduledInterview>()
    private var prefs: SharedPreferences? = null
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private const val PREF_KEY = "interviews_list"

    /**
     * Initialize with context (call once from Application or MainActivity)
     */
    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences("jobify_interviews", Context.MODE_PRIVATE)
            loadFromPreferences()
            Log.d("InterviewManager", "Initialized with ${interviewsList.size} interviews from SharedPreferences")
        }
    }

    /**
     * Load interviews from SharedPreferences into memory
     */
    private fun loadFromPreferences() {
        try {
            val jsonString = prefs?.getString(PREF_KEY, "[]") ?: "[]"
            Log.d("InterviewManager", "Loading from SharedPreferences: $jsonString")
            val loaded = json.decodeFromString<List<ScheduledInterview>>(jsonString)
            interviewsList.clear()
            interviewsList.addAll(loaded)
            Log.d("InterviewManager", "Loaded ${interviewsList.size} interviews")
        } catch (e: Exception) {
            Log.e("InterviewManager", "Error loading: ${e.message}")
            interviewsList.clear()
        }
    }

    /**
     * Get all scheduled interviews
     */
    fun getAllInterviews(): List<ScheduledInterview> {
        Log.d("InterviewManager", "Getting ${interviewsList.size} interviews")
        return interviewsList.toList()
    }

    /**
     * Add a new scheduled interview
     */
    fun addInterview(interview: ScheduledInterview) {
        Log.d("InterviewManager", ">>> ADD INTERVIEW CALLED <<<")
        Log.d("InterviewManager", "Interview: ${interview.candidateName} | ${interview.date} | ${interview.time}")
        interviewsList.add(interview)
        Log.d("InterviewManager", "Added interview: ${interview.candidateName}")
        Log.d("InterviewManager", "Total interviews now: ${interviewsList.size}")
        saveToPreferences()
        // Print all interviews for debugging
        interviewsList.forEachIndexed { index, intv ->
            Log.d("InterviewManager", "  [$index] ${intv.candidateName} - ${intv.date} ${intv.time}")
        }
    }

    /**
     * Save interviews to SharedPreferences
     */
    private fun saveToPreferences() {
        try {
            val jsonString = json.encodeToString(interviewsList)
            Log.d("InterviewManager", "Saving ${interviewsList.size} interviews to SharedPreferences")
            prefs?.edit {
                putString(PREF_KEY, jsonString)
            }
            Log.d("InterviewManager", "Saved successfully")
        } catch (e: Exception) {
            Log.e("InterviewManager", "Error saving: ${e.message}")
        }
    }

    /**
     * Refresh from SharedPreferences
     */
    fun refresh() {
        Log.d("InterviewManager", "Refreshing from SharedPreferences")
        loadFromPreferences()
    }

    /**
     * Clear all interviews
     */
    fun clearAllInterviews() {
        interviewsList.clear()
        prefs?.edit { remove(PREF_KEY) }
        Log.d("InterviewManager", "Cleared all interviews")
    }
}

