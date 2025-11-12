package com.example.jobify.ui.interviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class InterviewSchedule(
    val id: String,
    val candidateName: String,
    val candidatePosition: String,
    val date: String,
    val time: String,
    val interviewType: String,
    val status: String // scheduled, completed, cancelled
)

@Composable
fun InterviewsScreen(modifier: Modifier = Modifier) {
    // Sample data
    val interviews = listOf(
        InterviewSchedule(
            id = "1",
            candidateName = "John Doe",
            candidatePosition = "Senior Developer",
            date = "December 15, 2025",
            time = "10:00 AM",
            interviewType = "Online",
            status = "scheduled"
        ),
        InterviewSchedule(
            id = "2",
            candidateName = "Jane Smith",
            candidatePosition = "Frontend Developer",
            date = "December 16, 2025",
            time = "2:30 PM",
            interviewType = "In-Person",
            status = "scheduled"
        ),
        InterviewSchedule(
            id = "3",
            candidateName = "Mike Johnson",
            candidatePosition = "Full Stack Developer",
            date = "December 14, 2025",
            time = "11:00 AM",
            interviewType = "Online",
            status = "completed"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(interviews) { interview ->
            InterviewCard(interview)
        }
    }
}

@Composable
private fun InterviewCard(interview: InterviewSchedule) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with candidate name and status
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        interview.candidateName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        interview.candidatePosition,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                // Status badge
                Surface(
                    color = when (interview.status) {
                        "scheduled" -> Color(0xFFE3F2FD)
                        "completed" -> Color(0xFFE8F5E9)
                        "cancelled" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        interview.status.replaceFirstChar { it.uppercase() },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (interview.status) {
                            "scheduled" -> Color(0xFF1976D2)
                            "completed" -> Color(0xFF388E3C)
                            "cancelled" -> Color(0xFFC62828)
                            else -> Color(0xFF757575)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interview details
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InterviewInfoItem(
                    icon = Icons.Default.CalendarToday,
                    label = interview.date,
                    modifier = Modifier.weight(1f)
                )
                InterviewInfoItem(
                    icon = Icons.Default.Schedule,
                    label = interview.time,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InterviewInfoItem(
                    icon = Icons.Default.Person,
                    label = interview.interviewType,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* TODO: Edit interview */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text("Edit", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { /* TODO: Reschedule interview */ },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text("Reschedule", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun InterviewInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF6B7280)
        )
        Text(
            label,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
    }
}

