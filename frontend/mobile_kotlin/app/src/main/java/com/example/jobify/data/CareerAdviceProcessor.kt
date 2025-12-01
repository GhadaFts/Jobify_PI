package com.example.jobify.data

import com.example.jobify.model.*
import com.example.jobify.network.CareerAnalysisRequest
import com.example.jobify.network.CareerAnalysisResponse

object CareerAdviceProcessor {

    /**
     * Process AI response for frontend display
     */
    fun processAIAdvice(aiResponse: CareerAnalysisResponse, userData: CareerAnalysisRequest): ProcessedAdvice {
        val adviceText = aiResponse.advice
        val sentences = extractSentences(adviceText)
        return structureAdvice(sentences, userData)
    }

    /**
     * Extract sentences from AI text
     */
    private fun extractSentences(text: String): List<String> {
        // Method 1: Split by period followed by space
        var sentences = text.split(Regex("\\.\\s+"))
            .filter { it.trim().isNotEmpty() }

        // If not enough sentences, try splitting by newlines
        if (sentences.size < 3) {
            sentences = text.split(Regex("\n+"))
                .filter { it.trim().isNotEmpty() }
        }

        // Clean sentences (remove trailing periods)
        return sentences.map { it.replace(Regex("\\.$"), "").trim() }
    }

    /**
     * Structure sentences into frontend format
     */
    private fun structureAdvice(sentences: List<String>, userData: CareerAnalysisRequest): ProcessedAdvice {
        val summary = sentences.firstOrNull()
            ?: "Career advice for ${userData.country} based on your profile."

        // Create recommendations from sentences
        val recommendations = sentences.drop(1).take(3).mapIndexed { index, sentence ->
            Recommendation(
                title = generateRecommendationTitle(sentence, index),
                description = sentence,
                priority = determinePriority(index, sentences.size)
            )
        }

        // Extract skills mentioned
        val skills = extractSkills(sentences, userData.skills)

        // Last sentence as career path
        val careerPath = sentences.lastOrNull()
            ?: "With your ${userData.education} and ${userData.certificate}, you have strong potential in ${userData.country}."

        return ProcessedAdvice(
            summary = summary,
            recommendations = recommendations,
            skills = skills,
            careerPath = careerPath
        )
    }

    /**
     * Generate titles based on sentence content
     */
    private fun generateRecommendationTitle(sentence: String, index: Int): String {
        val commonTitles = listOf(
            "Skill Development Focus",
            "Career Strategy",
            "Professional Networking",
            "Market Alignment",
            "Certification Path",
            "Experience Building"
        )

        val keywords = mapOf(
            "certification" to "Certification Development",
            "skill" to "Skill Development",
            "network" to "Network Building",
            "experience" to "Experience Gain",
            "market" to "Market Strategy",
            "portfolio" to "Portfolio Enhancement",
            "project" to "Project Experience"
        )

        val lowerSentence = sentence.lowercase()

        for ((keyword, title) in keywords) {
            if (lowerSentence.contains(keyword)) {
                return title
            }
        }

        return commonTitles[index % commonTitles.size]
    }

    /**
     * Determine priority based on position
     */
    private fun determinePriority(index: Int, totalSentences: Int): Priority {
        return when {
            index == 0 -> Priority.HIGH
            index < 3 -> Priority.MEDIUM
            else -> Priority.LOW
        }
    }

    /**
     * Extract skills mentioned
     */
    private fun extractSkills(sentences: List<String>, userSkills: String?): List<SkillItem> {
        val skills = mutableListOf<SkillItem>()

        // Use user skills if available
        if (!userSkills.isNullOrBlank()) {
            val userSkillList = userSkills.split(",").map { it.trim() }
            userSkillList.take(3).forEach { skill ->
                skills.add(
                    SkillItem(
                        name = skill,
                        reason = "Essential for your target market"
                    )
                )
            }
        }

        // If not enough skills, add generic ones
        if (skills.size < 2) {
            val defaultSkills = listOf(
                SkillItem("Technical Certifications", "Highly valued in international markets"),
                SkillItem("Communication Skills", "Critical for cross-cultural collaboration"),
                SkillItem("Project Management", "Key for career advancement")
            )

            defaultSkills.take(3 - skills.size).forEach { skill ->
                skills.add(skill)
            }
        }

        return skills
    }

    /**
     * Create fallback advice when service fails
     */
    fun createFallbackAdvice(request: CareerAnalysisRequest): ProcessedAdvice {
        return ProcessedAdvice(
            summary = "Based on your ${request.education} education and ${request.certificate} certification, here's a career development plan for ${request.country}.",
            recommendations = listOf(
                Recommendation(
                    title = "Enhance Your Technical Skills",
                    description = "Focus on developing skills relevant to ${request.country}'s job market and consider additional certifications.",
                    priority = Priority.HIGH
                ),
                Recommendation(
                    title = "Build Professional Network",
                    description = "Connect with professionals in ${request.country} through LinkedIn and industry events.",
                    priority = Priority.MEDIUM
                ),
                Recommendation(
                    title = "Tailor Your Application Materials",
                    description = "Customize your resume and cover letter for the ${request.country} market requirements.",
                    priority = Priority.MEDIUM
                )
            ),
            skills = listOf(
                SkillItem(
                    name = request.skills?.split(",")?.firstOrNull()?.trim() ?: "Technical Expertise",
                    reason = "Core requirement for positions in ${request.country}"
                ),
                SkillItem(
                    name = "Cross-cultural Communication",
                    reason = "Essential for working in international environments"
                ),
                SkillItem(
                    name = "Industry-specific Tools",
                    reason = "In-demand skills for career advancement"
                )
            ),
            careerPath = "With your background, you can target relevant positions in ${request.country} and advance your career through continuous learning and networking."
        )
    }
}