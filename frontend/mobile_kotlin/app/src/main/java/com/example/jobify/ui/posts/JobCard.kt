package com.example.jobify.ui.posts

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobify.R
import com.example.jobify.data.InterviewManager
import com.example.jobify.model.Job
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCard(
    job: Job,
    onPublishClick: (String) -> Unit,
    onViewDetailsClick: () -> Unit,
    onEditClick: () -> Unit
) {
    var isPublishing by remember { mutableStateOf(false) }
    var isApplicantsExpanded by remember { mutableStateOf(false) }
    var selectedApplicant by remember { mutableStateOf<com.example.jobify.model.Applicant?>(null) }
    var underReviewApplicants by remember { mutableStateOf(setOf<String>()) }
    var currentJob by remember { mutableStateOf(job) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    
    // Bookmark repository for managing favorites
    val bookmarkRepository = remember { com.example.jobify.data.BookmarkRepository.getInstance() }
    val bookmarkedJobIds by bookmarkRepository.bookmarkedJobIds.collectAsState()
    
    // Check if this job is bookmarked
    val isJobBookmarked = remember(bookmarkedJobIds, job.id) {
        // Parse job ID - handle both "8" and "8.0" formats
        val jobId = try {
            job.id.toDoubleOrNull()?.toLong()
        } catch (e: Exception) {
            null
        }
        jobId?.let { bookmarkedJobIds.contains(it) } ?: false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.logo_soc),
                    contentDescription = "Company Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = job.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        job.badge?.let { Badge(text = it) }
                    }
                    Text(text = job.company, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
                IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Job", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = job.shortDescription, fontSize = 14.sp, color = Color.Gray, maxLines = 2)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.location,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                Chip(label = job.jobType)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoColumn(icon = Icons.Filled.Business, title = "Experience", value = job.experience)
                    InfoColumn(icon = Icons.Filled.Money, title = "Salary", value = job.salaryRange, valueColor = Color(0xFF28A745))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isApplicantsExpanded = !isApplicantsExpanded }
                        .padding(8.dp)
                ) {
                    InfoColumn(icon = Icons.Filled.People, title = "Applicants", value = "${job.applicantsCount} applied")
                }
            }

            // Expandable Applicants Section
            if (isApplicantsExpanded && currentJob.applicants.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                ApplicantsSection(
                    applicants = currentJob.applicants,
                    onApplicantClick = { applicant -> selectedApplicant = applicant },
                    onInterviewClick = { /* TODO: Handle interview scheduling */ },
                    onFavoriteClick = { applicant ->
                        // Toggle bookmark for this job (affects all applicants)
                        coroutineScope.launch {
                            // Parse job ID - handle both "8" and "8.0" formats
                            val jobId = try {
                                job.id.toDoubleOrNull()?.toLong()
                            } catch (e: Exception) {
                                null
                            }
                            
                            jobId?.let { id ->
                                android.util.Log.d("JobCard", "Toggling bookmark for job ID: $id, current state: $isJobBookmarked")
                                val wasBookmarked = isJobBookmarked
                                val success = bookmarkRepository.toggleBookmark(id)
                                if (success) {
                                    val action = if (wasBookmarked) "removed from" else "added to"
                                    android.util.Log.d("JobCard", "Bookmark toggled successfully, new state: ${!wasBookmarked}")
                                    android.widget.Toast.makeText(
                                        context,
                                        "Application $action favorites",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    android.util.Log.e("JobCard", "Failed to toggle bookmark")
                                    android.widget.Toast.makeText(
                                        context,
                                        "Failed to update favorites",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } ?: run {
                                android.util.Log.e("JobCard", "Failed to parse job ID: ${job.id}")
                                android.widget.Toast.makeText(
                                    context,
                                    "Invalid job ID format",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    favoriteApplicants = if (isJobBookmarked) {
                        // If job is bookmarked, all applicants are favorites
                        currentJob.applicants.map { it.id }.toSet()
                    } else {
                        emptySet()
                    },
                    underReviewApplicants = underReviewApplicants,
                    onActionClick = { applicant ->
                        // TODO: Handle action click
                    },
                    onActionAccepted = { applicant ->
                        // Update applicant status to accepted via backend
                        coroutineScope.launch {
                            try {
                                android.util.Log.d("JobCard", "Accepting application ${applicant.id}")
                                val response = com.example.jobify.network.ApiClient.applicationService.updateApplicationStatus(
                                    id = applicant.id,
                                    payload = mapOf("status" to "ACCEPTED")
                                )
                                
                                android.util.Log.d("JobCard", "Accept response code: ${response.code()}, success: ${response.isSuccessful}")
                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    android.util.Log.d("JobCard", "Application accepted successfully, response: $responseBody")
                                    // Update local state
                                    val updatedApplicants = currentJob.applicants.map { app ->
                                        if (app.id == applicant.id) {
                                            app.copy(status = "accepted", isNew = false)
                                        } else {
                                            app
                                        }
                                    }
                                    currentJob = currentJob.copy(applicants = updatedApplicants)
                                    underReviewApplicants = underReviewApplicants - applicant.id
                                    
                                    android.widget.Toast.makeText(
                                        context,
                                        "Application accepted",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    android.util.Log.e("JobCard", "Failed to accept application: ${response.code()}, message: ${response.message()}, error: $errorBody")
                                    android.widget.Toast.makeText(
                                        context,
                                        "Failed to accept application: ${response.code()}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("JobCard", "Error accepting application: ${e.message}", e)
                                e.printStackTrace()
                                android.widget.Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onActionRejected = { applicant ->
                        // Update applicant status to rejected via backend
                        coroutineScope.launch {
                            try {
                                android.util.Log.d("JobCard", "Rejecting application ${applicant.id}")
                                val response = com.example.jobify.network.ApiClient.applicationService.updateApplicationStatus(
                                    id = applicant.id,
                                    payload = mapOf("status" to "REJECTED")
                                )
                                
                                android.util.Log.d("JobCard", "Reject response code: ${response.code()}, success: ${response.isSuccessful}")
                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    android.util.Log.d("JobCard", "Application rejected successfully, response: $responseBody")
                                    // Update local state
                                    val updatedApplicants = currentJob.applicants.map { app ->
                                        if (app.id == applicant.id) {
                                            app.copy(status = "rejected", isNew = false)
                                        } else {
                                            app
                                        }
                                    }
                                    currentJob = currentJob.copy(applicants = updatedApplicants)
                                    underReviewApplicants = underReviewApplicants - applicant.id
                                    
                                    android.widget.Toast.makeText(
                                        context,
                                        "Application rejected",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    android.util.Log.e("JobCard", "Failed to reject application: ${response.code()}, message: ${response.message()}, error: $errorBody")
                                    android.widget.Toast.makeText(
                                        context,
                                        "Failed to reject application: ${response.code()}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("JobCard", "Error rejecting application: ${e.message}", e)
                                e.printStackTrace()
                                android.widget.Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onInterviewScheduled = { applicant, interviewDetails ->
                        // Schedule interview via backend
                        coroutineScope.launch {
                            try {
                                android.util.Log.d("JobCard", "Scheduling interview for application ${applicant.id}")
                                
                                // Validate jobSeekerId is present
                                if (applicant.jobSeekerId.isNullOrEmpty()) {
                                    android.util.Log.e("JobCard", "Cannot schedule interview: jobSeekerId is missing")
                                    android.widget.Toast.makeText(
                                        context,
                                        "Cannot schedule interview: applicant information incomplete",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                                
                                // Parse interview details and build request
                                // Convert date from "MM/dd/yyyy" to "yyyy-MM-dd"
                                val dateParts = interviewDetails.date.split("/")
                                val isoDate = if (dateParts.size == 3) {
                                    "${dateParts[2]}-${dateParts[0].padStart(2, '0')}-${dateParts[1].padStart(2, '0')}"
                                } else {
                                    interviewDetails.date // fallback
                                }
                                
                                // Convert time from "hh:mm AM/PM" to "HH:mm:ss" (24-hour format)
                                val timeParts = interviewDetails.time.replace(" AM", "").replace(" PM", "").split(":")
                                val isPM = interviewDetails.time.contains("PM")
                                val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
                                val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                                
                                val hour24 = when {
                                    isPM && hour != 12 -> hour + 12
                                    !isPM && hour == 12 -> 0
                                    else -> hour
                                }
                                
                                val isoTime = String.format("%02d:%02d:00", hour24, minute)
                                val scheduledDateTime = "${isoDate}T${isoTime}"
                                
                                android.util.Log.d("JobCard", "Converted date/time: ${interviewDetails.date} ${interviewDetails.time} -> $scheduledDateTime")
                                
                                // Map interview type
                                val interviewType = when (interviewDetails.type.lowercase()) {
                                    "online" -> "REMOTE"
                                    "local" -> "ON_SITE"
                                    else -> "REMOTE"
                                }
                                
                                val request = com.example.jobify.network.InterviewRequestDTO(
                                    applicationId = applicant.id,
                                    jobSeekerId = applicant.jobSeekerId,
                                    scheduledDate = scheduledDateTime,
                                    duration = interviewDetails.duration.toIntOrNull() ?: 60,
                                    location = interviewDetails.location,
                                    interviewType = interviewType,
                                    notes = interviewDetails.notes,
                                    meetingLink = interviewDetails.meetingLink
                                )
                                
                                val response = com.example.jobify.network.ApiClient.interviewService.scheduleInterview(request)
                                
                                android.util.Log.d("JobCard", "Interview schedule response code: ${response.code()}, success: ${response.isSuccessful}")
                                
                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    android.util.Log.d("JobCard", "Interview scheduled successfully, response: $responseBody")
                                    
                                    // Update application status to interview_scheduled
                                    val statusResponse = com.example.jobify.network.ApiClient.applicationService.updateApplicationStatus(
                                        id = applicant.id,
                                        payload = mapOf("status" to "INTERVIEW_SCHEDULED")
                                    )
                                    
                                    if (statusResponse.isSuccessful) {
                                        // Update local state
                                        val updatedApplicants = currentJob.applicants.map { app ->
                                            if (app.id == applicant.id) {
                                                app.copy(status = "interview_scheduled", isNew = false)
                                            } else {
                                                app
                                            }
                                        }
                                        currentJob = currentJob.copy(applicants = updatedApplicants)
                                        
                                        android.widget.Toast.makeText(
                                            context,
                                            "Interview scheduled successfully",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        android.util.Log.e("JobCard", "Failed to update application status after interview scheduling")
                                        android.widget.Toast.makeText(
                                            context,
                                            "Interview scheduled but status update failed",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    android.util.Log.e("JobCard", "Failed to schedule interview: ${response.code()}, message: ${response.message()}, error: $errorBody")
                                    android.widget.Toast.makeText(
                                        context,
                                        "Failed to schedule interview: ${response.code()}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("JobCard", "Error scheduling interview: ${e.message}", e)
                                e.printStackTrace()
                                android.widget.Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }

            // Show applicant detail dialog
            selectedApplicant?.let { selectedApplicantItem ->
                // Get the current version of the applicant from the job to reflect any status changes
                val currentApplicant = currentJob.applicants.find { it.id == selectedApplicantItem.id } ?: selectedApplicantItem

                // Debug: Log the appliedDate value
                android.util.Log.d("JobCard", "Applicant ${currentApplicant.id} appliedDate: ${currentApplicant.appliedDate} (${java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.FRENCH).format(java.util.Date(currentApplicant.appliedDate))})")

                // Map real applicant data from backend to ApplicantProfile
                val profile = com.example.jobify.model.ApplicantProfile(
                    id = currentApplicant.id,
                    name = currentApplicant.name,
                    title = currentApplicant.title,
                    location = currentApplicant.location ?: "",
                    phoneNumber = currentApplicant.phone ?: "",
                    email = currentApplicant.email ?: "",
                    dateOfBirth = currentApplicant.dateOfBirth ?: "",
                    gender = currentApplicant.gender ?: "",
                    nationality = currentApplicant.nationality ?: "",
                    status = currentApplicant.status,
                    profileImageUrl = currentApplicant.profileImageUrl,
                    cvUrl = currentApplicant.cvLink,
                    motivationLetter = currentApplicant.motivation ?: "",
                    skills = currentApplicant.skills,
                    experience = currentApplicant.experienceList.mapIndexed { index, expStr ->
                        // Parse experience string format: "position • company startDate - endDate\ndescription"
                        val lines = expStr.split("\n")
                        val headerLine = lines.firstOrNull() ?: ""
                        val description = lines.drop(1).joinToString("\n")
                        val parts = headerLine.split(" • ")
                        val position = parts.getOrNull(0) ?: ""
                        val companyAndDates = parts.getOrNull(1) ?: ""
                        val companyParts = companyAndDates.split(Regex("\\s+\\d"))
                        val company = companyParts.firstOrNull() ?: ""
                        com.example.jobify.model.Experience(
                            id = "exp$index",
                            jobTitle = position,
                            company = company,
                            startDate = "",
                            endDate = "",
                            description = description
                        )
                    },
                    education = currentApplicant.educationList.mapIndexed { index, eduStr ->
                        // Parse education string format: "degree en field • school (graduationDate)"
                        val parts = eduStr.split(" • ")
                        val degreeField = parts.getOrNull(0) ?: eduStr
                        val schoolGrad = parts.getOrNull(1) ?: ""
                        val school = schoolGrad.replace(Regex("\\s*\\([^)]*\\)"), "")
                        com.example.jobify.model.Education(
                            id = "edu$index",
                            degree = degreeField,
                            institution = school,
                            graduationDate = ""
                        )
                    },
                    githubUrl = currentApplicant.socials["github"],
                    websiteUrl = currentApplicant.socials["website"],
                    twitterUrl = currentApplicant.socials["twitter"],
                    facebookUrl = currentApplicant.socials["facebook"],
                    isNew = currentApplicant.isNew,
                    appliedDate = currentApplicant.appliedDate
                )

                ApplicantDetailDialog(
                    applicantProfile = profile,
                    onDismiss = { selectedApplicant = null },
                    onContactClick = { /* TODO: Handle contact */ },
                    isUnderReviewStatus = selectedApplicantItem.id in underReviewApplicants,
                    onDownloadCV = {
                        profile.cvUrl?.let { cvUrl ->
                            // Extract filename from URL or use applicant name
                            val filename = cvUrl.substringAfterLast("/").ifEmpty { "${profile.name.replace(" ", "_")}_CV.pdf" }
                            coroutineScope.launch {
                                downloadCV(context, cvUrl, filename)
                            }
                        }
                    },
                    onStatusChanged = { newStatus ->
                        // Update the applicant status in backend
                        coroutineScope.launch {
                            try {
                                // Call backend API to update status
                                val response = com.example.jobify.network.ApiClient.applicationService.updateApplicationStatus(
                                    id = selectedApplicantItem.id,
                                    payload = mapOf("status" to newStatus.uppercase())
                                )
                                
                                if (response.isSuccessful) {
                                    android.util.Log.d("JobCard", "Status updated successfully to $newStatus for application ${selectedApplicantItem.id}")
                                    
                                    // Update local state only after successful backend update
                                    underReviewApplicants = underReviewApplicants + selectedApplicantItem.id
                                    
                                    // Update the applicant in the job's applicants list
                                    val updatedApplicants = currentJob.applicants.map { app ->
                                        if (app.id == selectedApplicantItem.id) {
                                            app.copy(status = newStatus.lowercase(), isNew = false)
                                        } else {
                                            app
                                        }
                                    }
                                    currentJob = currentJob.copy(applicants = updatedApplicants)
                                } else {
                                    android.util.Log.e("JobCard", "Failed to update status: ${response.code()}")
                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Failed to update status", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("JobCard", "Error updating status", e)
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    onActionAccepted = {
                        // Update applicant to accepted
                        val updatedApplicants = currentJob.applicants.map { app ->
                            if (app.id == selectedApplicantItem.id) {
                                app.copy(status = "accepted", isNew = false)
                            } else {
                                app
                            }
                        }
                        currentJob = currentJob.copy(applicants = updatedApplicants)
                        underReviewApplicants = underReviewApplicants - selectedApplicantItem.id
                        selectedApplicant = null // Close the dialog
                    },
                    onActionRejected = {
                        // Update applicant to rejected
                        val updatedApplicants = currentJob.applicants.map { app ->
                            if (app.id == selectedApplicantItem.id) {
                                app.copy(status = "rejected", isNew = false)
                            } else {
                                app
                            }
                        }
                        currentJob = currentJob.copy(applicants = updatedApplicants)
                        underReviewApplicants = underReviewApplicants - selectedApplicantItem.id
                        selectedApplicant = null // Close the dialog
                    },
                    onInterviewScheduled = { interviewDetails ->
                        // Update applicant to interview_scheduled
                        val updatedApplicants = currentJob.applicants.map { app ->
                            if (app.id == selectedApplicantItem.id) {
                                app.copy(status = "interview_scheduled", isNew = false)
                            } else {
                                app
                            }
                        }
                        currentJob = currentJob.copy(applicants = updatedApplicants)

                        // Add to scheduled interviews
                        val scheduledInterview = com.example.jobify.model.ScheduledInterview(
                            id = "${System.currentTimeMillis()}",
                            candidateName = selectedApplicantItem.name,
                            candidatePosition = selectedApplicantItem.title,
                            date = interviewDetails.date,
                            time = interviewDetails.time,
                            interviewType = interviewDetails.type,
                            location = interviewDetails.location,
                            duration = interviewDetails.duration,
                            notes = interviewDetails.notes
                        )
                        InterviewManager.addInterview(scheduledInterview)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Required Skills", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                job.skills.forEach { skill -> Chip(label = skill) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Posted ${getPostedTime(job.postedAt)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onViewDetailsClick,
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Filled.Launch, contentDescription = "View Details", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Details")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (job.published) {
                        Surface(
                            color = Color(0xFF28A745), // Green
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Published ✓",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                isPublishing = true
                                onPublishClick(job.id)
                            },
                            enabled = !isPublishing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary, // Blue
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            if (isPublishing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Text("Publish Now")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(text: String) {
    val (backgroundColor, textColor) = when (text.lowercase()) {
        "actively hiring" -> Color(0xFFE0E7FF) to Color(0xFF4338CA)
        "new" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        "hot job" -> Color(0xFFFFE0E0) to Color(0xFFD92D20)
        "limited openings" -> Color(0xFFE0F7FA) to Color(0xFF006064)
        "urgent hiring" -> Color(0xFFFDE2E2) to Color(0xFFB91C1C)
        "open" -> Color(0xFFE6F7E6) to Color(0xFF166534)
        else -> Color(0xFFF3F4F6) to Color(0xFF1F2937)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun Chip(label: String) {
    Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(8.dp)) {
        Text(text = label, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
private fun InfoColumn(icon: ImageVector, title: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
        }
    }
}

private fun getPostedTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        days > 1 -> "$days days ago"
        days == 1L -> "1 day ago"
        else -> "Today"
    }
}

private suspend fun downloadCV(context: android.content.Context, cvUrl: String, filename: String) {
    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
        android.widget.Toast.makeText(context, "Downloading CV...", android.widget.Toast.LENGTH_SHORT).show()
    }
    
    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val apiService = com.example.jobify.network.ApiClient.cvUploadService
            val response = apiService.downloadCV(cvUrl).execute()
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                
                // Save to Downloads folder
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                }
                
                val uri = context.contentResolver.insert(
                    android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                
                uri?.let { fileUri ->
                    context.contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                        body.byteStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "CV downloaded successfully to Downloads", android.widget.Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "Failed to save CV", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Failed to download CV", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("JobCard", "Error downloading CV", e)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
