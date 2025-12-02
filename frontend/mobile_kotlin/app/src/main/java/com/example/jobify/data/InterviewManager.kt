package com.example.jobify.data

import android.content.Context
import android.util.Log
import com.example.jobify.model.Interview
import com.example.jobify.model.ScheduledInterview
import com.example.jobify.network.ApiClient
import com.example.jobify.network.InterviewResponseDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manager for scheduled interviews - fetches from backend interview-service
 */
object InterviewManager {
    // Cache for interviews (converts from backend DTOs to ScheduledInterview for UI)
    private val interviewsList = mutableListOf<ScheduledInterview>()
    private var isInitialized = false

    /**
     * Initialize the manager (call once from Application or MainActivity)
     */
    fun init(context: Context) {
        if (!isInitialized) {
            isInitialized = true
            Log.d("InterviewManager", "Initialized - ready to fetch interviews")
        }
    }

    /**
     * Get all scheduled interviews (from cache)
     */
    fun getAllInterviews(): List<ScheduledInterview> {
        Log.d("InterviewManager", "Getting ${interviewsList.size} interviews from cache")
        return interviewsList.toList()
    }

    /**
     * Fetch interviews from backend API (recruiter's interviews)
     */
    fun refresh(onComplete: ((Boolean, String?) -> Unit)? = null) {
        Log.d("InterviewManager", "Refreshing interviews from backend API")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.interviewService.getRecruiterInterviews()
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val interviews = response.body() ?: emptyList()
                        Log.d("InterviewManager", "✅ Fetched ${interviews.size} interviews from API")
                        
                        // Convert DTOs to ScheduledInterview for UI
                        interviewsList.clear()
                        interviews.forEach { dto ->
                            val scheduledInterview = convertDtoToScheduledInterview(dto)
                            interviewsList.add(scheduledInterview)
                            Log.d("InterviewManager", "  - ${scheduledInterview.candidateName} on ${scheduledInterview.date}")
                        }
                        
                        onComplete?.invoke(true, null)
                    } else {
                        val errorMsg = "Failed to fetch interviews: ${response.code()} ${response.message()}"
                        Log.e("InterviewManager", errorMsg)
                        onComplete?.invoke(false, errorMsg)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "Exception fetching interviews: ${e.message}"
                Log.e("InterviewManager", errorMsg, e)
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(false, errorMsg)
                }
            }
        }
    }

    /**
     * Convert backend DTO to UI model
     */
    private fun convertDtoToScheduledInterview(dto: InterviewResponseDTO): ScheduledInterview {
        val (date, time) = parseScheduledDateTime(dto.scheduledDate)
        
        // Determine status based on backend status and scheduled date
        val status = when (dto.status) {
            "COMPLETED" -> "completed"
            "CANCELLED" -> "cancelled"
            "SCHEDULED", "RESCHEDULED" -> {
                // Check if interview date has passed
                if (isInterviewPassed(dto.scheduledDate)) "completed" else "scheduled"
            }
            else -> "scheduled"
        }
        
        return ScheduledInterview(
            id = dto.id.toString(),
            candidateName = "Candidate", // TODO: Fetch from user service if needed
            candidatePosition = "Position", // TODO: Fetch from job if needed
            date = date,
            time = time,
            interviewType = when (dto.interviewType) {
                "REMOTE", "TECHNICAL", "HR", "MANAGERIAL" -> "Online Interview"
                "ON_SITE" -> "Local Interview"
                else -> dto.interviewType
            },
            location = dto.location ?: dto.meetingLink ?: "TBD",
            duration = "${dto.duration} min",
            notes = dto.notes ?: "",
            status = status,
            backendStatus = dto.status
        )
    }

    /**
     * Check if interview date/time has passed
     */
    private fun isInterviewPassed(isoDateTime: String): Boolean {
        return try {
            val cleanDateTime = isoDateTime.substringBefore('.')
                .substringBefore('+')
                .substringBefore('Z')
            
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val interviewDate = inputFormat.parse(cleanDateTime)
            val now = java.util.Date()
            
            interviewDate != null && interviewDate.before(now)
        } catch (e: Exception) {
            Log.e("InterviewManager", "Error checking if interview passed: ${e.message}")
            false
        }
    }

    /**
     * Parse ISO datetime to separate date and time strings
     * Input: "2025-12-05T14:30:00"
     * Output: ("12/05/2025", "02:30 PM")
     */
    private fun parseScheduledDateTime(isoDateTime: String): Pair<String, String> {
        return try {
            val cleanDateTime = isoDateTime.substringBefore('.')
                .substringBefore('+')
                .substringBefore('Z')
            
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val parsedDate = inputFormat.parse(cleanDateTime)
            
            val dateFormat = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
            val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            
            val dateString = dateFormat.format(parsedDate ?: java.util.Date())
            val timeString = timeFormat.format(parsedDate ?: java.util.Date())
            
            Pair(dateString, timeString)
        } catch (e: Exception) {
            Log.e("InterviewManager", "Error parsing date: ${e.message}")
            Pair("TBD", "TBD")
        }
    }

    /**
     * Cancel an interview by ID
     */
    fun cancelInterview(interviewId: Long, onComplete: ((Boolean, String?) -> Unit)? = null) {
        Log.d("InterviewManager", "Cancelling interview ID: $interviewId")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.interviewService.cancelInterview(interviewId)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("InterviewManager", "✅ Interview cancelled successfully")
                        // Remove from local cache
                        interviewsList.removeAll { it.id == interviewId.toString() }
                        onComplete?.invoke(true, null)
                    } else {
                        val errorMsg = "Failed to cancel interview: ${response.code()} ${response.message()}"
                        Log.e("InterviewManager", errorMsg)
                        onComplete?.invoke(false, errorMsg)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "Exception cancelling interview: ${e.message}"
                Log.e("InterviewManager", errorMsg, e)
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(false, errorMsg)
                }
            }
        }
    }

    /**
     * Add a new scheduled interview (legacy method for compatibility)
     * Note: In production, interviews should be created via API
     */
    @Deprecated("Use API to schedule interviews instead")
    fun addInterview(interview: ScheduledInterview) {
        Log.d("InterviewManager", "Legacy addInterview called: ${interview.candidateName}")
        interviewsList.add(interview)
    }

    /**
     * Clear all interviews from cache
     */
    fun clearAllInterviews() {
        interviewsList.clear()
        Log.d("InterviewManager", "Cleared all interviews from cache")
    }
}

