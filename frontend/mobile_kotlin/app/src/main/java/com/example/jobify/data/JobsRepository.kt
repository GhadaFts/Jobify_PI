package com.example.jobify.data

import com.example.jobify.model.Applicant
import com.example.jobify.model.Job
import com.example.jobify.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

// Network-backed repository that fetches jobs from the gateway via JobApiService
class JobsRepository {
    private val api = ApiClient.jobService
    // Enable verbose raw-auth JSON dumps for debugging. Set to false to disable.
    // Default to disabled; gate actual dumping behind BuildConfig.DEBUG to avoid
    // leaking sensitive info in production.
    private val DEBUG_DUMP_AUTH_RAW = false
    private val ENABLE_RAW_AUTH_DUMP: Boolean
        get() = try { com.example.jobify.BuildConfig.DEBUG && DEBUG_DUMP_AUTH_RAW } catch (_: Throwable) { false }
    // Simple in-memory cache for user profiles to avoid repeated network calls
    private val seekerCache: MutableMap<String, com.example.jobify.network.UserProfile> = mutableMapOf()

    // Generate a small mock profile to match web behavior when a real profile is incomplete
    private fun generateMockProfile(job: com.example.jobify.model.Job): Map<String, Any> {
        val titleLower = job.title.toLowerCase()
        val baseSkills = if (job.skills.isNotEmpty()) job.skills else listOf("Communication", "Teamwork", "Problem solving")

        fun developer(): Map<String, Any> = mapOf(
            "fullName" to "Alexandre Martin",
            "email" to "alexandre.martin@email.com",
            "phone" to "+21612345678",
            "title" to "Développeur Full Stack",
            "profileImage" to "/assets/default-avatar.png",
            "skills" to (if (job.skills.isNotEmpty()) job.skills else listOf("JavaScript", "TypeScript", "Angular", "Node.js")),
            "experienceList" to listOf("Développeur Full Stack • Tech Solutions SARL • 2020-03-01 - 2024-01-01\nDéveloppement et maintenance d'applications web.", "Développeur Frontend • Web Agency Tunis • 2018-06-01 - 2020-02-28\nInterfaces utilisateur responsive with Angular."),
            "educationList" to listOf("Master en Informatique en Génie Logiciel • Université Tunis El Manar (2018-06-01)") ,
            "nationality" to "Tunisien",
            "dateOfBirth" to "1994-05-15",
            "gender" to "Male",
            "socials" to mapOf<String, String>("github" to "https://github.com/alexandremartin")
        )

        fun engineer(): Map<String, Any> = mapOf(
            "fullName" to "Sophie Ben Ahmed",
            "email" to "sophie.benahmed@email.com",
            "phone" to "+21623456789",
            "title" to "Ingénieur Logiciel",
            "profileImage" to "/assets/default-avatar.png",
            "skills" to (if (job.skills.isNotEmpty()) job.skills else listOf("Java", "Spring Boot", "Microservices")),
            "experienceList" to listOf("Ingénieur Logiciel Senior • Software Engineering Corp • 2019-04-01 - 2024-01-01\nConception et développement d'architectures microservices."),
            "educationList" to listOf("Diplôme d'Ingénieur en Informatique • École Nationale d'Ingénieurs de Tunis (2017-06-01)"),
            "nationality" to "Tunisienne",
            "dateOfBirth" to "1993-08-22",
            "gender" to "Female",
            "socials" to mapOf<String, String>()
        )

        fun designer(): Map<String, Any> = mapOf(
            "fullName" to "Youssef Trabelsi",
            "email" to "youssef.trabelsi@email.com",
            "phone" to "+21634567890",
            "title" to "UI/UX Designer",
            "profileImage" to "/assets/default-avatar.png",
            "skills" to (if (job.skills.isNotEmpty()) job.skills else listOf("Figma", "Adobe XD", "Photoshop")),
            "experienceList" to listOf("UI/UX Designer • Digital Agency Tunis • 2021-01-01 - 2024-01-01\nConception de maquettes et prototypes."),
            "educationList" to listOf("Licence en Design Graphique • Institut Supérieur des Beaux-Arts (2019-06-01)"),
            "nationality" to "Tunisien",
            "dateOfBirth" to "1996-11-30",
            "gender" to "Male",
            "socials" to mapOf<String, String>()
        )

        fun manager(): Map<String, Any> = mapOf(
            "fullName" to "Leila Jlassi",
            "email" to "leila.jlassi@email.com",
            "phone" to "+21645678901",
            "title" to "Project Manager",
            "profileImage" to "/assets/default-avatar.png",
            "skills" to (if (job.skills.isNotEmpty()) job.skills else listOf("Project Management", "Agile", "Scrum")),
            "experienceList" to listOf("Chef de Projet IT • Solutions Entreprise SA • 2018-03-01 - 2024-01-01\nGestion de projets digitaux."),
            "educationList" to listOf("Master en Management • Institut des Hautes Études Commerciales (2016-06-01)"),
            "nationality" to "Tunisienne",
            "dateOfBirth" to "1992-03-14",
            "gender" to "Female",
            "socials" to mapOf<String, String>()
        )

        fun analyst(): Map<String, Any> = mapOf(
            "fullName" to "Mehdi Karray",
            "email" to "mehdi.karray@email.com",
            "phone" to "+21656789012",
            "title" to "Data Analyst",
            "profileImage" to "/assets/default-avatar.png",
            "skills" to (if (job.skills.isNotEmpty()) job.skills else listOf("SQL", "Python", "Tableau")),
            "experienceList" to listOf("Data Analyst • Data Insights Ltd • 2020-02-01 - 2024-01-01\nAnalyse de données business."),
            "educationList" to listOf("Master en Statistique • Faculté des Sciences de Tunis (2018-06-01)"),
            "nationality" to "Tunisien",
            "dateOfBirth" to "1994-07-08",
            "gender" to "Male",
            "socials" to mapOf<String, String>()
        )

        return when {
            titleLower.contains("developer") || titleLower.contains("dev") -> developer()
            titleLower.contains("engineer") -> engineer()
            titleLower.contains("designer") -> designer()
            titleLower.contains("manager") -> manager()
            titleLower.contains("analyst") -> analyst()
            else -> mapOf(
                "fullName" to "Mohamed Ali",
                "email" to "mohamed.ali@email.com",
                "phone" to "+21698765432",
                "title" to "Professional",
                "profileImage" to "/assets/default-avatar.png",
                "skills" to baseSkills,
                "experienceList" to listOf("Professional • Various Companies • 2018-01-01 - 2024-01-01\nExpérience diversifiée."),
                "educationList" to listOf("Licence • Université de Tunis (2018-06-01)"),
                "nationality" to "Tunisien",
                "dateOfBirth" to "1995-01-01",
                "gender" to "Male",
                "socials" to mapOf<String, String>()
            )
        }
    }
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
                            // CV / motivation / contact / details
                            val cvLinkFromApp = (m["cvLink"] ?: m["cv_link"] ?: m["cv"])?.toString()
                            val motivationFromApp = (m["motivationLettre"] ?: m["motivation"] ?: m["coverLetter"])?.toString()
                            // Try to extract contact/details from nested jobSeeker if present
                            val jobSeekerMap = m["jobSeeker"] as? Map<*, *>
                            // Names: web uses fullName / full_name
                            val fullNameFromSeeker = jobSeekerMap?.get("fullName") ?: jobSeekerMap?.get("full_name")
                            if (fullNameFromSeeker != null) name = fullNameFromSeeker.toString()
                            // Photo/profile keys used on web: photo_profil
                            val photoProfil = jobSeekerMap?.get("photo_profil")?.toString()
                            if (!photoProfil.isNullOrEmpty()) profileImage = photoProfil
                            // Contact + personal
                            var phoneFromSeeker = jobSeekerMap?.get("phone_number")?.toString()
                                ?: jobSeekerMap?.get("phone")?.toString()
                            var emailFromSeeker = jobSeekerMap?.get("email")?.toString()
                            var nationalityFromSeeker = jobSeekerMap?.get("nationality")?.toString()
                            var dobFromSeeker = jobSeekerMap?.get("date_of_birth")?.toString()
                            var genderFromSeeker = jobSeekerMap?.get("gender")?.toString()

                            val locationFromSeeker = jobSeekerMap?.get("location")?.toString()

                            // experience & education arrays commonly used in web UI
                            val experiencesRaw = jobSeekerMap?.get("experience") ?: jobSeekerMap?.get("experiences")
                            var experienceList = when (experiencesRaw) {
                                is List<*> -> experiencesRaw.mapNotNull { item ->
                                    val im = item as? Map<*, *> ?: return@mapNotNull item?.toString()
                                    val pos = im["position"] ?: im["title"] ?: im["role"]
                                    val comp = im["company"] ?: im["employer"]
                                    val desc = im["description"] ?: im["summary"]
                                    val start = im["startDate"] ?: im["start_date"]
                                    val end = im["endDate"] ?: im["end_date"]
                                    val period = when {
                                        start != null && end != null -> "${start} - ${end}"
                                        start != null -> "${start} - Present"
                                        else -> ""
                                    }
                                    val titlePart = pos?.toString() ?: ""
                                    val companyPart = comp?.toString() ?: ""
                                    val descPart = desc?.toString() ?: ""
                                    listOf(titlePart, companyPart, period).filter { it.isNotEmpty() }.joinToString(" • ") + if (descPart.isNotEmpty()) "\n$descPart" else ""
                                }
                                is String -> listOf(experiencesRaw)
                                else -> emptyList()
                            }

                            val educationRaw = jobSeekerMap?.get("education") ?: jobSeekerMap?.get("educations")
                            var educationList = when (educationRaw) {
                                is List<*> -> educationRaw.mapNotNull { item ->
                                    val im = item as? Map<*, *> ?: return@mapNotNull item?.toString()
                                    val degree = im["degree"] ?: im["title"] ?: im["qualification"]
                                    val field = im["field"] ?: im["fieldOfStudy"]
                                    val school = im["school"] ?: im["institution"]
                                    val grad = im["graduationDate"] ?: im["graduation_date"]
                                    val left = listOf(degree?.toString(), field?.toString()).filterNotNull().filter { it.isNotEmpty() }
                                    val header = if (left.isNotEmpty()) left.joinToString(" en ") else "Diplôme"
                                    val schoolPart = school?.toString() ?: ""
                                    val gradPart = grad?.toString() ?: ""
                                    var eduStr = header
                                    if (schoolPart.isNotEmpty()) eduStr += " • $schoolPart"
                                    if (gradPart.isNotEmpty()) eduStr += " ($gradPart)"
                                    eduStr
                                }
                                is String -> listOf(educationRaw)
                                else -> emptyList()
                            }

                            var skillsFromSeeker = (jobSeekerMap?.get("skills") as? List<*>)?.mapNotNull { it?.toString() }
                                ?: (m["skills"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

                            val socialsMap = mutableMapOf<String, String>()
                            jobSeekerMap?.let { jm ->
                                (jm["github_link"] ?: jm["github"])?.toString()?.let { socialsMap["github"] = it }
                                (jm["web_link"] ?: jm["website"] ?: jm["website_link"])?.toString()?.let { socialsMap["website"] = it }
                                (jm["twitter_link"] ?: jm["twitter"])?.toString()?.let { socialsMap["twitter"] = it }
                                (jm["facebook_link"] ?: jm["facebook"])?.toString()?.let { socialsMap["facebook"] = it }
                                (jm["linkedin_link"] ?: jm["linkedin"])?.toString()?.let { socialsMap["linkedin"] = it }
                            }
                            val appliedDate = when (val ad = m["applicationDate"] ?: m["appliedDate"]) {
                                is Number -> ad.toLong()
                                is String -> ad.toLongOrNull() ?: System.currentTimeMillis()
                                else -> System.currentTimeMillis()
                            }
                            val status = (m["status"] ?: "new").toString()

                            // Attempt to enrich applicant by resolving jobSeekerId (Keycloak ID) to a user profile
                            val rawSeekerId = (m["jobSeekerId"] ?: (m["jobSeeker"] as? Map<*, *>)?.get("keycloakId") ?: (m["jobSeeker"] as? Map<*, *>)?.get("id"))?.toString()
                            val seekerId = rawSeekerId?.trim()?.trimEnd(',')
                            // Keep userResp in outer scope so we can inspect it later when logging field sources
                            var userResp: retrofit2.Response<com.example.jobify.network.UserProfile>? = null
                            if (!seekerId.isNullOrEmpty()) {
                                // First: consult in-memory cache to avoid duplicate network calls
                                try {
                                    seekerCache[seekerId]?.let { cached ->
                                        try { Log.d("JobsRepository", "Using cached auth profile for $seekerId") } catch (_: Throwable) {}
                                        // Apply the same enrichment logic as if we fetched the user
                                        name = cached.fullName ?: name
                                        titleStr = cached.jobTitle ?: titleStr
                                        profileImage = cached.profilePicture ?: profileImage
                                        if (!cached.phoneNumber.isNullOrEmpty()) phoneFromSeeker = cached.phoneNumber
                                        if (!cached.email.isNullOrEmpty()) emailFromSeeker = cached.email
                                        if (!cached.nationality.isNullOrEmpty()) nationalityFromSeeker = cached.nationality
                                        if (!cached.dateOfBirth.isNullOrEmpty()) dobFromSeeker = cached.dateOfBirth
                                        if (!cached.gender.isNullOrEmpty()) genderFromSeeker = cached.gender
                                        if (!cached.skills.isNullOrEmpty()) skillsFromSeeker = cached.skills.mapNotNull { it?.toString() }
                                        if (!cached.experience.isNullOrEmpty()) {
                                            experienceList = cached.experience.mapNotNull { item ->
                                                val im = item as? Map<*, *> ?: return@mapNotNull item?.toString()
                                                val pos = im["position"] ?: im["title"] ?: im["role"]
                                                val comp = im["company"] ?: im["employer"]
                                                val desc = im["description"] ?: im["summary"]
                                                val start = im["startDate"] ?: im["start_date"]
                                                val end = im["endDate"] ?: im["end_date"]
                                                val period = when {
                                                    start != null && end != null -> "${start} - ${end}"
                                                    start != null -> "${start} - Present"
                                                    else -> ""
                                                }
                                                val titlePart = pos?.toString() ?: ""
                                                val companyPart = comp?.toString() ?: ""
                                                val descPart = desc?.toString() ?: ""
                                                listOf(titlePart, companyPart, period).filter { it.isNotEmpty() }.joinToString(" • ") + if (descPart.isNotEmpty()) "\n$descPart" else ""
                                            }
                                        }
                                        if (!cached.education.isNullOrEmpty()) {
                                            educationList = cached.education.mapNotNull { item ->
                                                val im = item as? Map<*, *> ?: return@mapNotNull item?.toString()
                                                val degree = im["degree"] ?: im["title"] ?: im["qualification"]
                                                val field = im["field"] ?: im["fieldOfStudy"]
                                                val school = im["school"] ?: im["institution"]
                                                val grad = im["graduationDate"] ?: im["graduation_date"]
                                                val left = listOf(degree?.toString(), field?.toString()).filterNotNull().filter { it.isNotEmpty() }
                                                val header = if (left.isNotEmpty()) left.joinToString(" en ") else "Diplôme"
                                                val schoolPart = school?.toString() ?: ""
                                                val gradPart = grad?.toString() ?: ""
                                                var eduStr = header
                                                if (schoolPart.isNotEmpty()) eduStr += " • $schoolPart"
                                                if (gradPart.isNotEmpty()) eduStr += " ($gradPart)"
                                                eduStr
                                            }
                                        }
                                        cached.githubLink?.let { socialsMap["github"] = it }
                                        cached.webLink?.let { socialsMap["website"] = it }
                                        cached.twitterLink?.let { socialsMap["twitter"] = it }
                                        cached.facebookLink?.let { socialsMap["facebook"] = it }
                                        // skip network
                                    } ?: run {
                                        // We'll attempt up to three endpoints in order: primary, alternate, public.
                                        try {
                                            try { Log.d("JobsRepository", "Resolving seekerId: $seekerId") } catch (_: Throwable) {}

                                            // Optionally fetch raw JSON for debugging (gated by build type)
                                            if (ENABLE_RAW_AUTH_DUMP) {
                                                try {
                                                    val raw = ApiClient.authService.getUserByKeycloakIdRaw(seekerId)
                                                    if (raw.isSuccessful) {
                                                        val bodyStr = try { raw.body()?.string() } catch (_: Throwable) { null }
                                                        try { Log.d("JobsRepository", "RAW AUTH primary for $seekerId: $bodyStr") } catch (_: Throwable) {}
                                                    } else {
                                                        try { Log.d("JobsRepository", "RAW AUTH primary for $seekerId returned ${raw.code()}") } catch (_: Throwable) {}
                                                    }
                                                } catch (ex: Exception) {
                                                    Log.e("JobsRepository", "Auth-service primary raw call threw for $seekerId: ${ex.message}", ex)
                                                }
                                            }

                                            // Try primary endpoint (deserialized)
                                            try {
                                                userResp = ApiClient.authService.getUserByKeycloakId(seekerId)
                                            } catch (ex: Exception) {
                                                Log.e("JobsRepository", "Auth-service primary call threw for $seekerId: ${ex.message}", ex)
                                                userResp = null
                                            }

                                            // Try alternate endpoint if needed
                                            if (userResp == null || !userResp.isSuccessful) {
                                                try { Log.d("JobsRepository", "Primary auth endpoint failed for $seekerId; trying alternate user endpoint") } catch (_: Throwable) {}
                                                // Raw alt
                                                if (ENABLE_RAW_AUTH_DUMP) {
                                                    try {
                                                        val rawAlt = ApiClient.authService.getUserByKeycloakIdAltRaw(seekerId)
                                                        if (rawAlt.isSuccessful) {
                                                            val bodyStr = try { rawAlt.body()?.string() } catch (_: Throwable) { null }
                                                            try { Log.d("JobsRepository", "RAW AUTH alt for $seekerId: $bodyStr") } catch (_: Throwable) {}
                                                        } else {
                                                            try { Log.d("JobsRepository", "RAW AUTH alt for $seekerId returned ${rawAlt.code()}") } catch (_: Throwable) {}
                                                        }
                                                    } catch (ex: Exception) {
                                                        Log.e("JobsRepository", "Auth-service alt raw call threw for $seekerId: ${ex.message}", ex)
                                                    }
                                                }
                                                try {
                                                    userResp = ApiClient.authService.getUserByKeycloakIdAlt(seekerId)
                                                } catch (ex: Exception) {
                                                    Log.e("JobsRepository", "Auth-service alt call threw for $seekerId: ${ex.message}", ex)
                                                    userResp = null
                                                }
                                            }

                                            // Try public profile endpoint if still not available
                                            if (userResp == null || !userResp.isSuccessful) {
                                                try { Log.d("JobsRepository", "Alt endpoint failed for $seekerId; trying public profile endpoint") } catch (_: Throwable) {}
                                                // Raw public
                                                if (ENABLE_RAW_AUTH_DUMP) {
                                                    try {
                                                        val rawPub = ApiClient.authService.getUserPublicProfileRaw(seekerId)
                                                        if (rawPub.isSuccessful) {
                                                            val bodyStr = try { rawPub.body()?.string() } catch (_: Throwable) { null }
                                                            try { Log.d("JobsRepository", "RAW AUTH public for $seekerId: $bodyStr") } catch (_: Throwable) {}
                                                        } else {
                                                            try { Log.d("JobsRepository", "RAW AUTH public for $seekerId returned ${rawPub.code()}") } catch (_: Throwable) {}
                                                        }
                                                    } catch (ex: Exception) {
                                                        Log.e("JobsRepository", "Auth-service public raw call threw for $seekerId: ${ex.message}", ex)
                                                    }
                                                }
                                                try {
                                                    userResp = ApiClient.authService.getUserPublicProfile(seekerId)
                                                } catch (ex: Exception) {
                                                    Log.e("JobsRepository", "Auth-service public call threw for $seekerId: ${ex.message}", ex)
                                                    userResp = null
                                                }
                                            }

                                            if (userResp == null) {
                                                try { Log.d("JobsRepository", "Auth-service response: null for $seekerId") } catch (_: Throwable) {}
                                            } else {
                                                try { Log.d("JobsRepository", "Auth-service response code for $seekerId = ${userResp.code()}") } catch (_: Throwable) {}
                                                if (userResp.isSuccessful) {
                                                    val user = userResp.body()
                                                    if (user != null) {
                                                        // cache profile to avoid future calls
                                                        try { seekerCache[seekerId] = user } catch (_: Throwable) {}

                                                        // DEBUG: Log all UserProfile fields to diagnose mapping
                                                        Log.d("JobsRepository", "UserProfile for $seekerId: fullName=${user.fullName}, jobTitle=${user.jobTitle}, phoneNumber=${user.phoneNumber}, email=${user.email}, nationality=${user.nationality}, dateOfBirth=${user.dateOfBirth}, gender=${user.gender}, skills=${user.skills?.size ?: 0}, experience=${user.experience?.size ?: 0}, education=${user.education?.size ?: 0}, github=${user.githubLink}, twitter=${user.twitterLink}, web=${user.webLink}, facebook=${user.facebookLink}")

                                                        // Prefer canonical fields from auth-service when available
                                                        name = user.fullName ?: name
                                                        // auth-service may return jobTitle or title
                                                        titleStr = user.jobTitle ?: titleStr
                                                        profileImage = user.profilePicture ?: profileImage

                                                        // personal/contact fields from auth-service
                                                        if (!user.phoneNumber.isNullOrEmpty()) phoneFromSeeker = user.phoneNumber
                                                        if (!user.email.isNullOrEmpty()) emailFromSeeker = user.email
                                                        if (!user.nationality.isNullOrEmpty()) nationalityFromSeeker = user.nationality
                                                        if (!user.dateOfBirth.isNullOrEmpty()) dobFromSeeker = user.dateOfBirth
                                                        if (!user.gender.isNullOrEmpty()) genderFromSeeker = user.gender

                                                        // prefer skills/experience/education from auth-service if provided
                                                        if (!user.skills.isNullOrEmpty()) {
                                                            skillsFromSeeker = user.skills.mapNotNull { it?.toString() }
                                                        }

                                                        // If auth-service provides structured experience/education, prefer it when available
                                                        if (!user.experience.isNullOrEmpty()) {
                                                            experienceList = user.experience.mapNotNull { item ->
                                                                val im = item as? Map<*, *> ?: return@mapNotNull item?.toString()
                                                                val pos = im["position"] ?: im["title"] ?: im["role"]
                                                                val comp = im["company"] ?: im["employer"]
                                                                val desc = im["description"] ?: im["summary"]
                                                                val start = im["startDate"] ?: im["start_date"]
                                                                val end = im["endDate"] ?: im["end_date"]
                                                                val period = when {
                                                                    start != null && end != null -> "${start} - ${end}"
                                                                    start != null -> "${start} - Present"
                                                                    else -> ""
                                                                }
                                                                val titlePart = pos?.toString() ?: ""
                                                                val companyPart = comp?.toString() ?: ""
                                                                val descPart = desc?.toString() ?: ""
                                                                listOf(titlePart, companyPart, period).filter { it.isNotEmpty() }.joinToString(" • ") + if (descPart.isNotEmpty()) "\n$descPart" else ""
                                                            }
                                                        }

                                                        if (!user.education.isNullOrEmpty()) {
                                                            educationList = user.education.mapNotNull { item ->
                                                                val im = item as? Map<*, *> ?: return@mapNotNull item?.toString()
                                                                val degree = im["degree"] ?: im["title"] ?: im["qualification"]
                                                                val field = im["field"] ?: im["fieldOfStudy"]
                                                                val school = im["school"] ?: im["institution"]
                                                                val grad = im["graduationDate"] ?: im["graduation_date"]
                                                                val left = listOf(degree?.toString(), field?.toString()).filterNotNull().filter { it.isNotEmpty() }
                                                                val header = if (left.isNotEmpty()) left.joinToString(" en ") else "Diplôme"
                                                                val schoolPart = school?.toString() ?: ""
                                                                val gradPart = grad?.toString() ?: ""
                                                                var eduStr = header
                                                                if (schoolPart.isNotEmpty()) eduStr += " • $schoolPart"
                                                                if (gradPart.isNotEmpty()) eduStr += " ($gradPart)"
                                                                eduStr
                                                            }
                                                        }

                                                        // socials from auth-service
                                                        user.githubLink?.let { socialsMap["github"] = it }
                                                        user.webLink?.let { socialsMap["website"] = it }
                                                        user.twitterLink?.let { socialsMap["twitter"] = it }
                                                        user.facebookLink?.let { socialsMap["facebook"] = it }
                                                    }
                                                }
                                            }
                                        } catch (ex: Exception) {
                                            // ignore enrichment errors and keep existing fields
                                            Log.e("JobsRepository", "Exception while enriching seeker $seekerId: ${ex.message}", ex)
                                        }
                                    }
                                } catch (_: Throwable) {
                                    // defensive: ignore cache/lookup errors
                                }
                            }

                                            // Decide whether to use a mock profile. Be conservative: do NOT overwrite identifying fields
                                            // (name/email) when there is any profile source (nested jobSeeker or auth-service).
                                            val hadAnyProfileSource = jobSeekerMap != null || !seekerId.isNullOrEmpty()

                                            // Only use mock when there is no profile source at all OR when both skills and experience are missing.
                                            val shouldUseMock = !hadAnyProfileSource || (skillsFromSeeker.isEmpty() && experienceList.isEmpty())

                                            var usedMock = false
                                            if (shouldUseMock) {
                                                try {
                                                    val mock = generateMockProfile(job)
                                                    usedMock = true

                                                    // Only set name/email from mock if we have absolutely no profile source
                                                    if (!hadAnyProfileSource) {
                                                        if (name == "Unknown" || name.isBlank()) name = (mock["fullName"] as? String) ?: name
                                                        if (emailFromSeeker.isNullOrEmpty()) emailFromSeeker = mock["email"] as? String
                                                    }

                                                    // Fill non-identifying missing fields from mock (profile image, phone, skills, experience, education, socials, nationality, dob, gender)
                                                    if (phoneFromSeeker.isNullOrEmpty()) phoneFromSeeker = mock["phone"] as? String
                                                    if (profileImage.isNullOrEmpty()) profileImage = mock["profileImage"] as? String
                                                    if (skillsFromSeeker.isEmpty()) {
                                                        skillsFromSeeker = (mock["skills"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                                                    }
                                                    if (experienceList.isEmpty()) {
                                                        experienceList = (mock["experienceList"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                                                    }
                                                    if (educationList.isEmpty()) {
                                                        educationList = (mock["educationList"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                                                    }
                                                    if (nationalityFromSeeker.isNullOrEmpty()) nationalityFromSeeker = mock["nationality"] as? String
                                                    if (dobFromSeeker.isNullOrEmpty()) dobFromSeeker = mock["dateOfBirth"] as? String
                                                    if (genderFromSeeker.isNullOrEmpty()) genderFromSeeker = mock["gender"] as? String
                                                    val mockSocials = mock["socials"] as? Map<*, *>
                                                    mockSocials?.forEach { k, v -> if (k != null && v != null) socialsMap[k.toString()] = v.toString() }
                                                    try { Log.d("JobsRepository", "Used mock for job ${job.id}: ${mock.keys}") } catch (_: Throwable) {}
                                                } catch (ex: Exception) {
                                                    Log.e("JobsRepository", "Mock generation failed for job ${job.id}: ${ex.message}", ex)
                                                }
                                            }

                                            // Normalize profile image URL: if it's a relative path (starts with '/') or missing scheme, prefix with BASE_URL
                                            try {
                                                if (!profileImage.isNullOrEmpty()) {
                                                    val pi = profileImage!!
                                                    if (pi.startsWith("/")) {
                                                        profileImage = ApiClient.BASE_URL.trimEnd('/') + pi
                                                    } else if (!pi.startsWith("http://") && !pi.startsWith("https://")) {
                                                        profileImage = ApiClient.BASE_URL.trimEnd('/') + "/" + pi
                                                    }
                                                }
                                            } catch (_: Throwable) {}

                                            // Debug: log which sources provided which fields
                                            try {
                                                Log.d("JobsRepository", "Field sources for applicant $appId -> nameSource=${if (name != "Unknown" && name == (m["jobSeekerName"] ?: fullNameFromSeeker)) "app/jobSeeker" else if (!seekerId.isNullOrEmpty() && (userResp?.isSuccessful == true)) "auth" else if (usedMock) "mock" else "unknown"}, emailPresent=${!emailFromSeeker.isNullOrEmpty()}, phonePresent=${!phoneFromSeeker.isNullOrEmpty()}, skillsCount=${skillsFromSeeker.size}, experienceCount=${experienceList.size}, educationCount=${educationList.size}, profileImage=${profileImage ?: "null"}")
                                            } catch (_: Throwable) {}

                            val mappedApplicant = com.example.jobify.model.Applicant(
                                id = appId,
                                name = name,
                                title = titleStr,
                                profileImageUrl = profileImage,
                                appliedDate = appliedDate,
                                isNew = status == "new",
                                isFavorite = false,
                                status = status,
                                cvLink = cvLinkFromApp,
                                motivation = motivationFromApp,
                                phone = phoneFromSeeker,
                                email = emailFromSeeker,
                                location = locationFromSeeker,
                                nationality = nationalityFromSeeker,
                                dateOfBirth = dobFromSeeker,
                                gender = genderFromSeeker,
                                skills = skillsFromSeeker,
                                experienceList = experienceList,
                                educationList = educationList,
                                socials = socialsMap,
                                jobSeekerId = seekerId
                            )

                            // DEBUG: Log final Applicant field values before adding to list
                            Log.d("JobsRepository", "Created Applicant: id=${mappedApplicant.id}, name=${mappedApplicant.name}, title='${mappedApplicant.title}', email=${mappedApplicant.email}, phone=${mappedApplicant.phone}, nationality=${mappedApplicant.nationality}, dateOfBirth=${mappedApplicant.dateOfBirth}, gender=${mappedApplicant.gender}, skills=${mappedApplicant.skills}, experienceListSize=${mappedApplicant.experienceList.size}, educationListSize=${mappedApplicant.educationList.size}, socials=${mappedApplicant.socials}")

                            mappedApps.add(mappedApplicant)
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
