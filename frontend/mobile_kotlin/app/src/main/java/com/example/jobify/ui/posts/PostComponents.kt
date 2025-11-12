package com.example.jobify.ui.posts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobify.R
import com.example.jobify.model.Applicant
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LabelChip(label: String) {
    Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(8.dp)) {
        Text(text = label, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun RequirementItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF28A745),
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DetailInfoChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = Color.Gray, fontSize = 14.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInput(value: String, onValueChange: (String) -> Unit, options: List<String>, label: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }, modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
        )
        ExposedDropdownMenu(expanded, { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onValueChange(option); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditableChipSection(title: String, chips: List<String>, onChipsChange: (List<String>) -> Unit) {
    var newChipText by remember { mutableStateOf("") }
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            chips.forEach { chip ->
                InputChip(selected = false, onClick = {}, label = { Text(chip) },
                    trailingIcon = { IconButton(onClick={ onChipsChange(chips - chip) }) { Icon(Icons.Default.Close, "Remove", Modifier.size(18.dp)) } })
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(newChipText, { newChipText = it }, Modifier.weight(1f), label = { Text("Add a skill") }, colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White))
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                if (newChipText.isNotBlank()) {
                    onChipsChange(chips + newChipText)
                    newChipText = ""
                }
            }) { Icon(Icons.Default.Add, "Add") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableListSection(title: String, items: List<String>, onItemsChange: (List<String>) -> Unit) {
    var newItemText by remember { mutableStateOf("") }
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        items.forEach { item ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, null, Modifier.padding(end = 8.dp), tint = Color(0xFF28A745))
                Text(item, Modifier.weight(1f).padding(vertical = 4.dp))
                IconButton({ onItemsChange(items - item) }) { Icon(Icons.Default.Close, "Remove", tint = Color.Gray) }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(newItemText, { newItemText = it }, Modifier.weight(1f), label = { Text("Add a requirement") }, colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White))
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                if (newItemText.isNotBlank()) { onItemsChange(items + newItemText); newItemText = "" }
            }) { Icon(Icons.Default.Add, "Add") }
        }
    }
}

@Composable
fun ApplicantCard(
    applicant: Applicant,
    onInterviewClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit,
    isFavorite: Boolean = false,
    showAIScore: Boolean = false,
    aiScore: Int = 0,
    rank: Int = 0,
    onActionClick: () -> Unit = {},
    isUnderReview: Boolean = false,
    onActionAccepted: () -> Unit = {},
    onActionRejected: () -> Unit = {}
) {
    var showActionDialog by remember { mutableStateOf(false) }
    var showInterviewDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (showAIScore) BorderStroke(2.dp, Color(0xFFE3F2FD)) else BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top row: Profile image + text info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rank Badge (if enabled) - shows position 1, 2, 3, etc.
                if (showAIScore && rank > 0) {
                    Surface(
                        color = Color(0xFF7C3AED),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$rank",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Profile Image
                Image(
                    painter = painterResource(id = R.drawable.applicant),
                    contentDescription = "Applicant Profile",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Text content column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Name with New/Under Review badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            applicant.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Show "Under Review" badge if status changed
                        if (isUnderReview) {
                            Surface(
                                color = Color(0xFFFCD34D),
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text(
                                    "Under Review",
                                    color = Color(0xFF92400E),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                        } else if (applicant.status == "accepted") {
                            // Show "Accepted" badge in green
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text(
                                    "Accepted",
                                    color = Color(0xFF065F46),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                        } else if (applicant.status == "rejected") {
                            // Show "Rejected" badge in red
                            Surface(
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text(
                                    "Rejected",
                                    color = Color(0xFF7F1D1D),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                        } else if (applicant.isNew) {
                            // Show "New" badge if still new
                            Surface(
                                color = Color(0xFF3B82F6),
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text(
                                    "New",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    // Title
                    Text(
                        applicant.title,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )

                    // Applied date
                    Text(
                        "Applied on ${formatAppliedDate(applicant.appliedDate)}",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }

                // Score display (if enabled) - shows on the right
                if (showAIScore) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "SCORE",
                            fontSize = 9.sp,
                            color = Color(0xFF7C3AED),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$aiScore",
                            fontSize = 18.sp,
                            color = Color(0xFF3B82F6),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "/100",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    // Chevron icon on the right
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "View Details",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Favorite star and Interview button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFDB022) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Button(
                    onClick = { showInterviewDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Interview", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                // Action button - appears when status changes to Under Review
                if (isUnderReview) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showActionDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Action", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    // Show action dialog when action button is clicked on card
    if (showActionDialog) {
        CandidateActionDialog(
            candidateName = applicant.name,
            candidateTitle = applicant.title,
            onDismiss = { showActionDialog = false },
            onAccept = {
                onActionAccepted()
                showActionDialog = false
            },
            onReject = {
                onActionRejected()
                showActionDialog = false
            }
        )
    }

    // Show interview scheduling dialog when interview button is clicked
    if (showInterviewDialog) {
        ScheduleInterviewDialog(
            candidateName = applicant.name,
            candidateTitle = applicant.title,
            appliedFor = "1",
            onDismiss = { showInterviewDialog = false },
            onSchedule = { interviewDetails ->
                onInterviewClick()
                showInterviewDialog = false
            }
        )
    }
}

@Composable
fun ApplicantsSection(
    applicants: List<Applicant>,
    onApplicantClick: (Applicant) -> Unit,
    onInterviewClick: (Applicant) -> Unit,
    onFavoriteClick: (Applicant) -> Unit,
    favoriteApplicants: Set<String> = emptySet(),
    underReviewApplicants: Set<String> = emptySet(),
    onActionClick: (Applicant) -> Unit = {},
    onActionAccepted: (Applicant) -> Unit = {},
    onActionRejected: (Applicant) -> Unit = {}
) {
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var showAIScores by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }

    // Animated rotation effect
    LaunchedEffect(isAnalyzing) {
        while (isAnalyzing) {
            delay(16) // ~60fps
            rotation = (rotation + 6f) % 360f
        }
    }

    // AI analysis effect - wait 5 seconds then show results
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            delay(5000) // 5 seconds
            showAIScores = true
            isAnalyzing = false
        }
    }

    val displayedApplicants = if (showFavoritesOnly) {
        applicants.filter { it.id in favoriteApplicants }
    } else {
        applicants
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // Header with Applications title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Blue icon box
            Surface(
                color = Color(0xFF3B82F6),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Applications",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }

            // Title section
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Applications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "${displayedApplicants.size} candidates total",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // AI Ranking / Analysis Button
            if (isAnalyzing) {
                Button(
                    onClick = { },
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF7C3AED)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Analyzing",
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(rotation)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Analysis in progress", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            } else if (showAIScores) {
                Button(
                    onClick = {
                        showAIScores = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B7280),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Disable AI",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Disable AI", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                Button(
                    onClick = {
                        isAnalyzing = true
                        showAIScores = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "AI Ranking",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Ranking", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

            OutlinedButton(
                onClick = { showFavoritesOnly = !showFavoritesOnly },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (favoriteApplicants.isNotEmpty()) Color(0xFFFEF3C7) else Color.White,
                    contentColor = if (favoriteApplicants.isNotEmpty()) Color(0xFFFDB022) else Color.Gray
                ),
                border = if (favoriteApplicants.isNotEmpty())
                    BorderStroke(1.dp, Color(0xFFFDB022))
                else
                    BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Favorites",
                    modifier = Modifier.size(14.dp),
                    tint = if (favoriteApplicants.isNotEmpty()) Color(0xFFFDB022) else Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Favorites${if (favoriteApplicants.isNotEmpty()) " (${favoriteApplicants.size})" else ""}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Applicants list
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            if (displayedApplicants.isEmpty()) {
                Text(
                    "No applicants found",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                // Create ranking: auto-generate scores for demo, then rank by score
                val applicantsWithScores = displayedApplicants.mapIndexed { index, applicant ->
                    val score = 100 - (index * 5) // 100, 95, 90, 85... for demo
                    applicant to score
                }

                // Sort by score descending and add rank
                val rankedApplicants = applicantsWithScores
                    .sortedByDescending { it.second }
                    .mapIndexed { index, (applicant, score) ->
                        Triple(applicant, score, index + 1) // rank starts at 1
                    }

                rankedApplicants.forEach { (applicant, score, rank) ->
                    ApplicantCard(
                        applicant = applicant,
                        onInterviewClick = { onInterviewClick(applicant) },
                        onFavoriteClick = { onFavoriteClick(applicant) },
                        onCardClick = { onApplicantClick(applicant) },
                        isFavorite = applicant.id in favoriteApplicants,
                        showAIScore = showAIScores,
                        aiScore = score,
                        rank = if (showAIScores) rank else 0,
                        onActionClick = { onActionClick(applicant) },
                        isUnderReview = applicant.id in underReviewApplicants,
                        onActionAccepted = { onActionAccepted(applicant) },
                        onActionRejected = { onActionRejected(applicant) }
                    )
                }
            }
        }
    }
}

fun formatAppliedDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
