package com.example.jobify.model

data class ProcessedAdvice(
    val summary: String,
    val recommendations: List<Recommendation>,
    val skills: List<SkillItem>,
    val careerPath: String
)

data class Recommendation(
    val title: String,
    val description: String,
    val priority: Priority
)

enum class Priority {
    HIGH, MEDIUM, LOW;

    fun getColorHex(): String {
        return when (this) {
            HIGH -> "#FF6B35"    // Orange
            MEDIUM -> "#3B82F6"  // Blue
            LOW -> "#10B981"     // Green
        }
    }

    fun getDisplayName(): String {
        return name.lowercase().capitalize()
    }
}

data class SkillItem(
    val name: String,
    val reason: String
)