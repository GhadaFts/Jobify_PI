
package com.example.jobify.data

import kotlinx.serialization.Serializable

@Serializable
data class GeminiResponse(
    val cvScore: Int? = null,
    val cvSuggestions: List<CvSuggestion>? = null,
    val improvedSummary: ImprovedSummary? = null,
    val profile: UserProfile? = null
)

@Serializable
data class CvSuggestion(
    val id: String? = null,
    val type: String? = null, // "success", "warning", "info", "missing"
    val title: String? = null,
    val message: String? = null
)

@Serializable
data class ImprovedSummary(
    val overallAssessment: String? = null,
    val strengths: List<String>? = null,
    val improvements: List<String>? = null
)

@Serializable
data class UserProfile(
    val name: String? = null,
    val title: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val nationality: String? = null,
    val summary: String? = null,
    val skills: List<String>? = null,
    val experience: List<Experience>? = null,
    val education: List<Education>? = null
)

@Serializable
data class Experience(
    val position: String? = null,
    val company: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val description: String? = null
)

@Serializable
data class Education(
    val degree: String? = null,
    val field: String? = null,
    val school: String? = null,
    val graduationDate: String? = null
)
