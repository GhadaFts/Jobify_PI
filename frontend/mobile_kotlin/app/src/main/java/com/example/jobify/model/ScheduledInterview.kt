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
    val notes: String = "",
    val status: String = "scheduled", // "scheduled", "completed", "cancelled"
    val backendStatus: String = "SCHEDULED" // Backend status: "SCHEDULED", "COMPLETED", "CANCELLED", "RESCHEDULED"
) : Parcelable {
    // Check if interview can be cancelled
    fun isCancellable(): Boolean {
        return status == "scheduled" && backendStatus in listOf("SCHEDULED", "RESCHEDULED")
    }
}

