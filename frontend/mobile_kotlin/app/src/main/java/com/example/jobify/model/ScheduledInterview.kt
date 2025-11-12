package com.example.jobify.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ScheduledInterview(
    val id: String,
    val candidateName: String,
    val candidatePosition: String,
    val date: String, // Format: "mm/dd/yyyy"
    val time: String, // Format: "HH:MM AM/PM"
    val interviewType: String, // "Local Interview" or "Online Interview"
    val location: String,
    val duration: String, // in minutes
    val notes: String = ""
) : Parcelable {
    // Calculate status based on interview date
    fun getStatus(): String {
        return try {
            val dateFormat = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
            val interviewDate = dateFormat.parse(date)
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
            }.time

            if (interviewDate != null && interviewDate.before(today)) {
                "completed"
            } else {
                "scheduled"
            }
        } catch (_: Exception) {
            "scheduled"
        }
    }
}

