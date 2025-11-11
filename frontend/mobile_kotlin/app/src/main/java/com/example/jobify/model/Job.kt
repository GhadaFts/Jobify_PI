package com.example.jobify.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Using Long for timestamp for broad Android compatibility (minSdk 24)

@Parcelize
data class Job(
    val id: String,
    val title: String,
    val company: String,
    val companyLogoUrl: String?,
    val location: String,
    val jobType: String, // "Full-time"
    val shortDescription: String,
    val experience: String, // "5+ years"
    val salaryRange: String, // "$120,000 - $150,000"
    val applicantsCount: Int,
    val skills: List<String>,
    val badge: String?, // "actively hiring", "new", "Draft"
    var published: Boolean,
    val postedAt: Long, // Changed from Instant to Long
    val requirements: List<String> = emptyList(),
    val applicants: List<Applicant> = emptyList()
) : Parcelable
