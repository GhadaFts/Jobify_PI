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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
            if (isApplicantsExpanded && job.applicants.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                ApplicantsSection(
                    applicants = job.applicants,
                    onApplicantClick = { /* TODO: Handle applicant click */ },
                    onInterviewClick = { /* TODO: Handle interview scheduling */ },
                    onFavoriteClick = { /* TODO: Toggle favorite */ }
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
