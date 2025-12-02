package com.example.jobify.ui.interviews

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
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
import com.example.jobify.R
import com.example.jobify.model.ScheduledInterview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewsScreen(
    modifier: Modifier = Modifier,
    scheduledInterviews: List<ScheduledInterview> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRefresh: () -> Unit = {},
    onCancelInterview: (String) -> Unit = {}
) {
    // Use passed interviews list
    val interviews = remember(scheduledInterviews) {
        mutableStateListOf<ScheduledInterview>().apply {
            addAll(scheduledInterviews)
        }
    }

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
    ) {
        // Header with title and description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Scheduled Interviews",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF1F2937)
            )
            Text(
                "Manage and track all your candidate interviews",
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF)
            )
        }

        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search interviews...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        // Filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val scheduledCount = interviews.count { it.status == "scheduled" }
            val completedCount = interviews.count { it.status == "completed" }
            val cancelledCount = interviews.count { it.status == "cancelled" }

            listOf(
                "all" to "All (${interviews.size})",
                "upcoming" to "Upcoming ($scheduledCount)",
                "completed" to "Completed ($completedCount)",
                "cancelled" to "Cancelled ($cancelledCount)"
            ).forEach { (filter, label) ->
                Surface(
                    modifier = Modifier
                        .clickable { selectedFilter = filter },
                    color = if (selectedFilter == filter) Color(0xFFE0E7FF) else Color.White,
                    shape = RoundedCornerShape(6.dp),
                    border = if (selectedFilter == filter)
                        BorderStroke(1.dp, Color(0xFF3B82F6))
                    else
                        BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Text(
                        label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedFilter == filter) Color(0xFF3B82F6) else Color(0xFF6B7280)
                    )
                }
            }
        }

        // Show loading, error, or interviews list
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3B82F6))
                        Text(
                            "Loading interviews...",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Failed to load interviews",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            errorMessage,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            interviews.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "No interviews scheduled",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            "Interviews will appear here when you schedule them",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
            else -> {
                // Interviews list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val filteredInterviews = when (selectedFilter) {
                        "upcoming" -> interviews.filter { it.status == "scheduled" }
                        "completed" -> interviews.filter { it.status == "completed" }
                        "cancelled" -> interviews.filter { it.status == "cancelled" }
                        else -> interviews
                    }.filter { it.candidateName.contains(searchText, ignoreCase = true) }

                    items(filteredInterviews) { interview ->
                        InterviewItem(
                            interview = interview,
                            onCancel = { onCancelInterview(interview.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InterviewItem(
    interview: ScheduledInterview,
    onCancel: () -> Unit = {}
) {
    val status = interview.status
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top section: Candidate info and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Candidate image
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    color = Color(0xFFE5E7EB)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.applicant),
                        contentDescription = interview.candidateName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                // Candidate info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        interview.candidateName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        interview.candidatePosition,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 14.sp
                    )
                }

                // Status badge
                Surface(
                    color = when (status) {
                        "scheduled" -> Color(0xFFFEF3C7)
                        "completed" -> Color(0xFFDCFCE7)
                        "cancelled" -> Color(0xFFFEE2E2)
                        else -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        when (status) {
                            "scheduled" -> "Upcoming"
                            "completed" -> "Completed"
                            "cancelled" -> "Cancelled"
                            else -> "Pending"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (status) {
                            "scheduled" -> Color(0xFF92400E)
                            "completed" -> Color(0xFF065F46)
                            "cancelled" -> Color(0xFF991B1B)
                            else -> Color(0xFF6B7280)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interview details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InterviewDetailItem(
                    icon = Icons.Default.CalendarToday,
                    text = interview.date,
                    modifier = Modifier.weight(1f)
                )
                InterviewDetailItem(
                    icon = Icons.Default.Schedule,
                    text = interview.time,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InterviewDetailItem(
                    icon = Icons.Default.LocationOn,
                    text = interview.interviewType,
                    modifier = Modifier.weight(1f)
                )
            }

            // Cancel button (only for scheduled interviews)
            if (interview.isCancellable()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Cancel Interview",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Interview") },
            text = { Text("Are you sure you want to cancel this interview with ${interview.candidateName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No, Keep It")
                }
            }
        )
    }
}

@Composable
private fun InterviewDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(14.dp),
            tint = Color(0xFF6B7280)
        )
        Text(
            text,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

