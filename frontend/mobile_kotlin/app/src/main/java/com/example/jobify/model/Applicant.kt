package com.example.jobify.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Applicant(
    val id: String,
    val name: String,
    val title: String, // e.g., "Développeur Full Stack Senior"
    val profileImageUrl: String? = null,
    val appliedDate: Long, // timestamp
    val isNew: Boolean = true,
    val isFavorite: Boolean = false,
    val status: String = "new", // new, interview, rejected, hired

    // Extended fields to mirror web dialog
    val cvLink: String? = null,
    val motivation: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val location: String? = null,
    val skills: List<String> = emptyList(),
    val experienceList: List<String> = emptyList(),
    val educationList: List<String> = emptyList(),
    val socials: Map<String, String> = emptyMap(),
    val nationality: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val jobSeekerId: String? = null
) : Parcelable


