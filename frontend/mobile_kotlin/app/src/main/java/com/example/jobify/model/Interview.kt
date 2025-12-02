package com.example.jobify.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Interview model matching backend InterviewResponseDTO
 */
@Serializable
@Parcelize
data class Interview(
    val id: Long,
    val applicationId: String,
    val jobSeekerId: String,
    val recruiterId: String,
    val scheduledDate: String, // ISO format: "2025-12-05T14:30:00"
    val duration: Int, // in minutes
    val location: String?,
    val interviewType: String, // TECHNICAL, HR, MANAGERIAL, ON_SITE, REMOTE
    val status: String, // SCHEDULED, RESCHEDULED, COMPLETED, CANCELLED, NO_SHOW
    val notes: String?,
    val meetingLink: String?,
    val createdAt: String,
    val updatedAt: String,
    
    // Additional info (optional, populated by backend if available)
    val candidateName: String? = null,
    val candidatePosition: String? = null,
    val jobTitle: String? = null
) : Parcelable {
    
    /**
     * Convert to ScheduledInterview for UI compatibility
     */
    fun toScheduledInterview(): ScheduledInterview {
        val (date, time) = parseScheduledDateTime(scheduledDate)
        
        return ScheduledInterview(
            id = id.toString(),
            candidateName = candidateName ?: "Unknown Candidate",
            candidatePosition = candidatePosition ?: jobTitle ?: "Position",
            date = date,
            time = time,
            interviewType = when (interviewType) {
                "REMOTE", "TECHNICAL", "HR", "MANAGERIAL" -> "Online Interview"
                "ON_SITE" -> "Local Interview"
                else -> interviewType
            },
            location = location ?: meetingLink ?: "TBD",
            duration = "$duration min",
            notes = notes ?: ""
        )
    }
    
    /**
     * Parse ISO datetime to separate date and time strings
     * Input: "2025-12-05T14:30:00"
     * Output: ("12/05/2025", "02:30 PM")
     */
    private fun parseScheduledDateTime(isoDateTime: String): Pair<String, String> {
        return try {
            // Handle both with and without timezone
            val cleanDateTime = isoDateTime.substringBefore('.')
                .substringBefore('+')
                .substringBefore('Z')
            
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val parsedDate = inputFormat.parse(cleanDateTime)
            
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            
            val dateString = dateFormat.format(parsedDate ?: Date())
            val timeString = timeFormat.format(parsedDate ?: Date())
            
            Pair(dateString, timeString)
        } catch (e: Exception) {
            // Fallback if parsing fails
            Pair("TBD", "TBD")
        }
    }
    
    /**
     * Check if interview is upcoming
     */
    fun isUpcoming(): Boolean {
        return status in listOf("SCHEDULED", "RESCHEDULED")
    }
    
    /**
     * Check if interview is completed
     */
    fun isCompleted(): Boolean {
        return status == "COMPLETED"
    }
}
