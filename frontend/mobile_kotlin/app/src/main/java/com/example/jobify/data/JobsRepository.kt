package com.example.jobify.data

import com.example.jobify.model.Applicant
import com.example.jobify.model.Job
import com.example.jobify.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Network-backed repository that fetches jobs from the gateway via JobApiService
class JobsRepository {
    private val api = ApiClient.jobService

    suspend fun getJobs(): List<Job> = withContext(Dispatchers.IO) {
        val resp = api.getMyJobs()
        if (resp.isSuccessful) {
            val body = resp.body() ?: emptyList()
            // First map job DTOs to Job objects without applications
            val jobs = body.map { dto ->
                // dto is a Map<String, Any> - extract known fields defensively
                val id = dto["id"]?.toString() ?: java.util.UUID.randomUUID().toString()
                val title = (dto["title"] ?: dto["jobPosition"] ?: "").toString()
                val company = (dto["company"] ?: "").toString()
                val companyLogo = dto["companyLogo"]?.toString()
                val location = (dto["location"] ?: "").toString()
                val type = (dto["type"] ?: "").toString()
                val experience = (dto["experience"] ?: "").toString()
                val salary = (dto["salary"] ?: dto["salaryRange"] ?: "").toString()
                val description = (dto["description"] ?: "").toString()
                val skills = (dto["skills"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                val requirements = (dto["requirements"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                val published = (dto["published"] as? Boolean) ?: false
                val postedAt = when (val p = dto["postedAt"] ?: dto["posted"]) {
                    is Number -> p.toLong()
                    is String -> p.toLongOrNull() ?: System.currentTimeMillis()
                    else -> System.currentTimeMillis()
                }
                val applicantsCount = (dto["applicants"] as? Number)?.toInt() ?: (dto["applicantsCount"] as? Number)?.toInt() ?: 0

                // map applications if present
                val applicants = mutableListOf<Applicant>()
                val appsRaw = dto["applications"] as? List<*>
                if (appsRaw != null) {
                    for (a in appsRaw) {
                        val m = a as? Map<*, *> ?: continue
                        val appId = m["id"]?.toString() ?: m["_id"]?.toString() ?: java.util.UUID.randomUUID().toString()
                        val name = (m["jobSeekerName"] ?: m["jobSeeker"]?.let { (it as? Map<*, *>)?.get("fullName") } ?: "Unknown").toString()
                        val titleStr = (m["jobSeekerTitle"] ?: "").toString()
                        val profileImage = (m["jobSeeker"] as? Map<*, *>)?.get("profileImageUrl")?.toString()
                        val appliedDate = when (val ad = m["applicationDate"] ?: m["appliedDate"]) {
                            is Number -> ad.toLong()
                            is String -> ad.toLongOrNull() ?: System.currentTimeMillis()
                            else -> System.currentTimeMillis()
                        }
                        val status = (m["status"] ?: "new").toString()
                        applicants.add(Applicant(
                            id = appId,
                            name = name,
                            title = titleStr,
                            profileImageUrl = profileImage,
                            appliedDate = appliedDate,
                            isNew = status == "new",
                            isFavorite = false,
                            status = status
                        ))
                    }
                }

                Job(
                    id = id,
                    title = title,
                    company = company,
                    companyLogoUrl = companyLogo,
                    location = location,
                    jobType = type,
                    shortDescription = description,
                    experience = experience,
                    salaryRange = salary,
                    applicantsCount = applicantsCount,
                    skills = skills,
                    badge = dto["badge"]?.toString(),
                    published = published,
                    postedAt = postedAt,
                    requirements = requirements,
                    applicants = applicants
                )
            }

            // Then for each job, fetch applications from application-service and attach them
            val finalJobs = mutableListOf<Job>()
            for (job in jobs) {
                try {
                    val appResp = ApiClient.applicationService.getByJobOffer(job.id)
                    if (appResp.isSuccessful) {
                        val appsBody = appResp.body() ?: emptyList()
                        val mappedApps = mutableListOf<com.example.jobify.model.Applicant>()
                        for (a in appsBody) {
                            val m = a as? Map<*, *> ?: continue
                            val appId = m["id"]?.toString() ?: m["_id"]?.toString() ?: java.util.UUID.randomUUID().toString()
                            var name = (m["jobSeekerName"] ?: (m["jobSeeker"] as? Map<*, *>)?.get("fullName") ?: "Unknown").toString()
                            var titleStr = (m["jobSeekerTitle"] ?: "").toString()
                            var profileImage = (m["jobSeeker"] as? Map<*, *>)?.get("profileImageUrl")?.toString()
                            val appliedDate = when (val ad = m["applicationDate"] ?: m["appliedDate"]) {
                                is Number -> ad.toLong()
                                is String -> ad.toLongOrNull() ?: System.currentTimeMillis()
                                else -> System.currentTimeMillis()
                            }
                            val status = (m["status"] ?: "new").toString()

                            // Attempt to enrich applicant by resolving jobSeekerId (Keycloak ID) to a user profile
                            val rawSeekerId = (m["jobSeekerId"] ?: (m["jobSeeker"] as? Map<*, *>)?.get("keycloakId") ?: (m["jobSeeker"] as? Map<*, *>)?.get("id"))?.toString()
                            val seekerId = rawSeekerId?.trim()?.trimEnd(',')
                            if (!seekerId.isNullOrEmpty()) {
                                try {
                                    val userResp = ApiClient.authService.getUserByKeycloakId(seekerId)
                                    if (userResp.isSuccessful) {
                                        val user = userResp.body()
                                        if (user != null) {
                                            // Prefer canonical fields from auth-service when available
                                            name = user.fullName ?: name
                                            titleStr = user.jobTitle ?: titleStr
                                            profileImage = user.profilePicture ?: profileImage
                                        }
                                    }
                                } catch (_: Exception) {
                                    // ignore enrichment errors and keep existing fields
                                }
                            }

                            mappedApps.add(com.example.jobify.model.Applicant(
                                id = appId,
                                name = name,
                                title = titleStr,
                                profileImageUrl = profileImage,
                                appliedDate = appliedDate,
                                isNew = status == "new",
                                isFavorite = false,
                                status = status
                            ))
                        }

                        // Create a new Job instance with attached applicants
                        val jobWithApps = job.copy(applicants = mappedApps, applicantsCount = mappedApps.size)
                        finalJobs.add(jobWithApps)
                    } else {
                        // if application-service call failed, keep original job
                        finalJobs.add(job)
                    }
                } catch (e: Exception) {
                    // on any exception, keep original job and continue
                    finalJobs.add(job)
                }
            }

            return@withContext finalJobs
        } else {
            throw Exception("Failed to load jobs: ${resp.code()} ${resp.message()}")
        }
    }

    suspend fun createJob(payload: Map<String, Any>): Map<String, Any> = withContext(Dispatchers.IO) {
        val resp = api.createJob(payload)
        if (resp.isSuccessful) resp.body() ?: emptyMap() else throw Exception("Create job failed: ${resp.code()} ${resp.message()}")
    }

    suspend fun updateJob(id: String, payload: Map<String, Any>): Map<String, Any> = withContext(Dispatchers.IO) {
        // Ensure integer-like IDs that were parsed as doubles (e.g., "8.0") become "8"
        val safeId = try {
            if (id.contains('.')) {
                val d = id.toDoubleOrNull()
                if (d != null) d.toLong().toString() else id
            } else id
        } catch (_: Throwable) {
            id
        }

        val resp = api.updateJob(safeId, payload)
        if (resp.isSuccessful) {
            resp.body() ?: emptyMap()
        } else {
            val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
            throw Exception("Update job failed: ${resp.code()} ${resp.message()} ${err ?: ""}")
        }
    }

    suspend fun uploadLogo(part: okhttp3.MultipartBody.Part): Map<String, Any> = withContext(Dispatchers.IO) {
        val resp = api.uploadLogo(part)
        if (resp.isSuccessful) resp.body() ?: emptyMap() else throw Exception("Upload failed: ${resp.code()} ${resp.message()}")
    }
}
