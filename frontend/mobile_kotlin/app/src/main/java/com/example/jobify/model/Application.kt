package com.example.jobify.model

import java.io.Serializable

/**
 * Application status enum matching backend
 */
enum class ApplicationStatus {
    NEW,
    UNDER_REVIEW,
    SHORTLISTED,
    INTERVIEW_SCHEDULED,
    REJECTED,
    ACCEPTED,
    WITHDRAWN
}

/**
 * Application request DTO for creating applications
 */
data class ApplicationRequestDTO(
    val jobSeekerId: String? = null, // Will be set automatically from JWT on backend
    val jobOfferId: Int,
    val cvLink: String,
    val motivationLettre: String? = null,
    val status: ApplicationStatus? = null,
    val aiScore: Int? = null,
    val isFavorite: Boolean = false
) : Serializable

/**
 * Application response DTO from backend
 */
data class ApplicationResponseDTO(
    val id: String,
    val applicationDate: String,
    val status: ApplicationStatus,
    val cvLink: String,
    val motivationLettre: String? = null,
    val jobSeekerId: String,
    val jobOfferId: Int,
    val aiScore: Int? = null,
    val isFavorite: Boolean = false,
    val lastStatusChange: String? = null,
    val createdAt: String,
    val updatedAt: String
) : Serializable

/**
 * Helper functions to convert between DTO and Map
 */
object ApplicationMapper {
    fun toMap(dto: ApplicationRequestDTO): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "jobOfferId" to dto.jobOfferId,
            "cvLink" to dto.cvLink,
            "isFavorite" to dto.isFavorite
        )

        dto.jobSeekerId?.let { map["jobSeekerId"] = it }
        dto.motivationLettre?.let { map["motivationLettre"] = it }
        dto.status?.let { map["status"] = it.name }
        dto.aiScore?.let { map["aiScore"] = it }

        return map
    }

    fun fromMap(map: Map<String, Any>): ApplicationResponseDTO {
        return ApplicationResponseDTO(
            id = map["id"] as? String ?: "",
            applicationDate = map["applicationDate"] as? String ?: "",
            status = ApplicationStatus.valueOf(map["status"] as? String ?: "NEW"),
            cvLink = map["cvLink"] as? String ?: "",
            motivationLettre = map["motivationLettre"] as? String,
            jobSeekerId = map["jobSeekerId"] as? String ?: "",
            jobOfferId = (map["jobOfferId"] as? Number)?.toInt() ?: 0,
            aiScore = (map["aiScore"] as? Number)?.toInt(),
            isFavorite = map["isFavorite"] as? Boolean ?: false,
            lastStatusChange = map["lastStatusChange"] as? String,
            createdAt = map["createdAt"] as? String ?: "",
            updatedAt = map["updatedAt"] as? String ?: ""
        )
    }

    fun fromMapList(mapList: List<Map<String, Any>>): List<ApplicationResponseDTO> {
        return mapList.map { fromMap(it) }
    }
}
