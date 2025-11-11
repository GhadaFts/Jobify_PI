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
    val status: String = "new" // new, interview, rejected, hired
) : Parcelable

