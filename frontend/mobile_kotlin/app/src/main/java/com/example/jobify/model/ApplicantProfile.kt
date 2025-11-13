package com.example.jobify.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Experience(
    val id: String,
    val jobTitle: String,
    val company: String,
    val startDate: String,
    val endDate: String,
    val description: String
) : Parcelable

@Parcelize
data class Education(
    val id: String,
    val degree: String,
    val institution: String,
    val graduationDate: String
) : Parcelable

@Parcelize
data class ApplicantProfile(
    val id: String,
    val name: String,
    val title: String,
    val location: String,
    val phoneNumber: String,
    val email: String,
    val dateOfBirth: String,
    val gender: String,
    val profileImageUrl: String?,
    val cvUrl: String?,
    val motivationLetter: String,
    val skills: List<String>,
    val experience: List<Experience>,
    val education: List<Education>,
    val githubUrl: String?,
    val websiteUrl: String?,
    val isNew: Boolean = true,
    val appliedDate: Long
) : Parcelable

