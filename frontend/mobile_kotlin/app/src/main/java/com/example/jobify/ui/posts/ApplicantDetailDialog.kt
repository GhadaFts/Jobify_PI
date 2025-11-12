package com.example.jobify.ui.posts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.jobify.R
import com.example.jobify.model.ApplicantProfile
import kotlinx.coroutines.delay

@Composable
fun ApplicantDetailDialog(
    applicantProfile: ApplicantProfile,
    onDismiss: () -> Unit,
    onContactClick: () -> Unit,
    onStatusChanged: (String) -> Unit = { }, // Callback when status changes
    isUnderReviewStatus: Boolean = false, // Track if status has changed to Under Review
    onActionAccepted: () -> Unit = { }, // Callback when action is accepted
    onActionRejected: () -> Unit = { } // Callback when action is rejected
) {
    var statusChanged by remember { mutableStateOf(isUnderReviewStatus) }
    var showActionDialog by remember { mutableStateOf(false) }
    var showInterviewDialog by remember { mutableStateOf(false) }

    // 20-second timer effect - only if applicant is "new"
    LaunchedEffect(applicantProfile.isNew, statusChanged) {
        if (applicantProfile.isNew && !statusChanged) {
            delay(20000) // Wait 20 seconds
            statusChanged = true
            onStatusChanged("Under Review")
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Application Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.applicant),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    applicantProfile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                // Dynamic status badge
                                if (statusChanged) {
                                    Surface(
                                        color = Color(0xFFFCD34D),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "Under Review",
                                            color = Color(0xFF92400E),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                } else if (applicantProfile.isNew) {
                                    Surface(
                                        color = Color(0xFF3B82F6),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "new",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                applicantProfile.title,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                applicantProfile.location,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Contact Information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Contact Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Text(applicantProfile.email, fontSize = 12.sp)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Phone",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Text(applicantProfile.phoneNumber, fontSize = 12.sp)
                            }

                            Text(
                                "Date of Birth: ${applicantProfile.dateOfBirth}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Text(
                                "Gender: ${applicantProfile.gender}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Application Details
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Application Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Application Date:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                                Text(
                                    formatAppliedDate(applicantProfile.appliedDate),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Skills/Competences
                    Column {
                        Text(
                            "Skills",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            applicantProfile.skills.forEach { skill ->
                                Surface(
                                    color = Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        skill,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1976D2),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // CV Section
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Curriculum Vitae",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (applicantProfile.cvUrl != null) {
                                Button(
                                    onClick = { /* TODO: Download CV */ },
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3B82F6)
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Download,
                                        contentDescription = "Download",
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    // Motivation Letter
                    if (applicantProfile.motivationLetter.isNotBlank()) {
                        Column {
                            Text(
                                "Motivation Letter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 150.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                            ) {
                                Text(
                                    applicantProfile.motivationLetter,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    // Professional Experience
                    if (applicantProfile.experience.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Professional Experience",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            applicantProfile.experience.forEach { exp ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            exp.jobTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            exp.company,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            "${exp.startDate} - ${exp.endDate}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF999999)
                                        )
                                        if (exp.description.isNotBlank()) {
                                            Text(
                                                exp.description,
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Education
                    if (applicantProfile.education.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Education",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            applicantProfile.education.forEach { edu ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            edu.degree,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            edu.institution,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            edu.graduationDate,
                                            fontSize = 10.sp,
                                            color = Color(0xFF999999)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Social Links
                    if (applicantProfile.githubUrl != null || applicantProfile.websiteUrl != null) {
                        Column {
                            Text(
                                "Social Links",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (applicantProfile.githubUrl != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Code,
                                            contentDescription = "GitHub",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "GitHub",
                                            fontSize = 12.sp,
                                            color = Color(0xFF3B82F6)
                                        )
                                    }
                                }
                                if (applicantProfile.websiteUrl != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Language,
                                            contentDescription = "Website",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "Website",
                                            fontSize = 12.sp,
                                            color = Color(0xFF3B82F6)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Close", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showInterviewDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6)
                            )
                        ) {
                            Text("Contact Candidate", fontSize = 12.sp, color = Color.White)
                        }

                        // Action button - appears after 20 seconds
                        if (statusChanged) {
                            Button(
                                onClick = { showActionDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3B82F6)
                                )
                            ) {
                                Text("Action", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // Show action dialog when action button is clicked
    if (showActionDialog) {
        CandidateActionDialog(
            candidateName = applicantProfile.name,
            candidateTitle = applicantProfile.title,
            onDismiss = { showActionDialog = false },
            onAccept = {
                showActionDialog = false
                onActionAccepted()
            },
            onReject = {
                showActionDialog = false
                onActionRejected()
            }
        )
    }

    // Show interview scheduling dialog when contact button is clicked
    if (showInterviewDialog) {
        ScheduleInterviewDialog(
            candidateName = applicantProfile.name,
            candidateTitle = applicantProfile.title,
            appliedFor = "1",
            onDismiss = { showInterviewDialog = false },
            onSchedule = { interviewDetails ->
                onContactClick()
                showInterviewDialog = false
            }
        )
    }
}

@Composable
fun CandidateActionDialog(
    candidateName: String,
    candidateTitle: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    var selectedAction by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    "Take a Final Decision",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Warning Box - Enhanced message
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Warning",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFFD97706)
                        )
                        Text(
                            "⚠ Warning: This action is permanent and cannot be undone. Once you accept or reject this candidate, this decision cannot be reversed. Please review carefully before confirming.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Candidate Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.applicant),
                        contentDescription = "Candidate",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(
                            candidateName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            candidateTitle,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Action Selection Label
                Text(
                    "Select an action:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Accept Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedAction = "accept" },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedAction == "accept") Color(0xFFDEF7EC) else Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedAction == "accept") Color(0xFF10B981) else Color(0xFFE5E7EB)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAction == "accept",
                            onClick = { selectedAction = "accept" }
                        )
                        Column {
                            Text(
                                "Accept Candidate",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                "This candidate will be accepted for this position",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Reject Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedAction = "reject" },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedAction == "reject") Color(0xFFFEE2E2) else Color.White
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedAction == "reject") Color(0xFFEF4444) else Color(0xFFE5E7EB)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAction == "reject",
                            onClick = { selectedAction = "reject" }
                        )
                        Column {
                            Text(
                                "Reject Candidate",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                "This candidate will not be considered for this position",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            when (selectedAction) {
                                "accept" -> onAccept()
                                "reject" -> onReject()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        enabled = selectedAction.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            disabledContainerColor = Color(0xFFCBD5E1)
                        )
                    ) {
                        Text("Confirm Decision", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleInterviewDialog(
    candidateName: String,
    candidateTitle: String,
    appliedFor: String = "1",
    onDismiss: () -> Unit,
    onSchedule: (InterviewDetails) -> Unit
) {
    var interviewDate by remember { mutableStateOf("") }
    var interviewTime by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("60") }
    var interviewType by remember { mutableStateOf("Local Interview") }
    var location by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        "Schedule Interview",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // Candidate Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                candidateName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                candidateTitle,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                "Applied for: $appliedFor",
                                fontSize = 11.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }

                    // Interview Date - Date Picker
                    Column(modifier = Modifier.clickable { showDatePicker = true }) {
                        Text(
                            "Interview Date *",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = interviewDate,
                            onValueChange = {},
                            placeholder = { Text("mm/dd/yyyy") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            singleLine = true,
                            readOnly = true,
                            enabled = false,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Calendar",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }

                    // Interview Time
                    Column {
                        Text(
                            "Interview Time *",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = interviewTime,
                            onValueChange = { interviewTime = it },
                            placeholder = { Text("--:-- --") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            singleLine = true
                        )
                    }

                    // Duration
                    Column {
                        Text(
                            "Duration (minutes) *",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = "$duration minutes",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf("30", "45", "60", "90", "120").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("$option minutes") },
                                        onClick = {
                                            duration = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Interview Type - Only Local and Online
                    Column {
                        Text(
                            "Interview Type *",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        var typeExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded }
                        ) {
                            OutlinedTextField(
                                value = interviewType,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                listOf("Local Interview", "Online Interview").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            interviewType = option
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Location
                    Column {
                        Text(
                            "Location *",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            placeholder = { Text("e.g. Conference Room A, Office 123, etc.") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            singleLine = true
                        )
                    }

                    // Additional Notes
                    Column {
                        Text(
                            "Additional Notes",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = additionalNotes,
                            onValueChange = { additionalNotes = it },
                            placeholder = { Text("Any specific topics to cover, preparation required, etc.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp),
                            shape = RoundedCornerShape(6.dp),
                            maxLines = 4
                        )
                    }
                }

                // Action Buttons - Fixed at bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Cancel", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (interviewDate.isNotEmpty() && interviewTime.isNotEmpty() && location.isNotEmpty()) {
                                    onSchedule(
                                        InterviewDetails(
                                            date = interviewDate,
                                            time = interviewTime,
                                            duration = duration,
                                            type = interviewType,
                                            location = location,
                                            notes = additionalNotes
                                        )
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            enabled = interviewDate.isNotEmpty() && interviewTime.isNotEmpty() && location.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6),
                                disabledContainerColor = Color(0xFFCBD5E1)
                            )
                        ) {
                            Text("Schedule Interview", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Show date picker when date field is clicked
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { selectedDate ->
                interviewDate = selectedDate
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

// Data class for interview details
data class InterviewDetails(
    val date: String,
    val time: String,
    val duration: String,
    val type: String,
    val location: String,
    val notes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = java.util.Calendar.getInstance()
                            calendar.timeInMillis = millis
                            val month = (calendar.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
                            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                            val year = calendar.get(java.util.Calendar.YEAR)
                            onDateSelected("$month/$day/$year")
                        }
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    onDismiss()
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
