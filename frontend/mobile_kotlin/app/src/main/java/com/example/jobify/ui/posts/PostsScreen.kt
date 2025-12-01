package com.example.jobify.ui.posts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jobify.model.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    modifier: Modifier = Modifier,
    viewModel: PostsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showNotPublished by viewModel.showNotPublished.collectAsState()
    val selectedJob by viewModel.selectedJob.collectAsState()
    val jobToEdit by viewModel.jobToEdit.collectAsState()
    
    // Load bookmarks when screen is first composed
    val bookmarkRepository = remember { com.example.jobify.data.BookmarkRepository.getInstance() }
    LaunchedEffect(Unit) {
        bookmarkRepository.loadBookmarks()
    }

    selectedJob?.let { JobDetailsDialog(job = it, onDismiss = viewModel::dismissJobDetails) }
    jobToEdit?.let {
        EditJobDialog(
            job = it,
            onDismiss = viewModel::dismissEditJobDialog,
            onSave = viewModel::saveJob
        )
    }

    when (val state = uiState) {
        is PostsUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        is PostsUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = Color.Red) }
        }
        is PostsUiState.Success -> {
            val jobsToShow = if (showNotPublished) state.jobs.filter { !it.published } else state.jobs
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    CreatePostWidget { newJob ->
                        viewModel.createJob(newJob)
                    }
                }
                item {
                    var searchText by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search your jobs...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White
                        )
                    )
                }
                item {
                    val allJobsCount = state.jobs.size
                    val notPublishedCount = state.jobs.filter { !it.published }.size
                    SegmentedControl(allJobsCount, notPublishedCount, showNotPublished) { viewModel.toggleFilter(it) }
                }

                if (jobsToShow.isEmpty()) {
                    item {
                        EmptyState(showNotPublished = showNotPublished)
                    }
                } else {
                    items(jobsToShow, key = { it.id }) { job ->
                        JobCard(
                            job = job,
                            onPublishClick = { viewModel.publishJob(job.id) },
                            onViewDetailsClick = { viewModel.showJobDetails(job) },
                            onEditClick = { viewModel.showEditJobDialog(job) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostWidget(onCreateJob: (Job) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Profile",
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if(expanded) "Create a new job posting" else "Create a new job posting...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(visible = expanded) {
                CreateJobForm(
                    onCreateJob = {
                        onCreateJob(it)
                        expanded = false
                    },
                    onCancel = { expanded = false }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobForm(onCreateJob: (Job) -> Unit, onCancel: () -> Unit) {
    val jobTypes = listOf("Full-time", "Part-time", "Contract", "Freelance")
    val experienceLevels = listOf("Entry-level", "Mid-level", "Senior-level", "Lead")
    val jobStatusOptions = listOf("open", "new", "hot job", "limited openings", "actively hiring", "urgent hiring")

    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf(jobTypes.first()) }
    var experience by remember { mutableStateOf(experienceLevels.first()) }
    var salary by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(emptyList<String>()) }
    var requirements by remember { mutableStateOf(emptyList<String>()) }
    var badge by remember { mutableStateOf(jobStatusOptions.first()) }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(title, { title = it }, label = { Text("Job Title *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
            OutlinedTextField(company, { company = it }, label = { Text("Company *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(location, { location = it }, label = { Text("Location *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
            DropdownInput(jobType, { jobType = it }, jobTypes, "Job Type *", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DropdownInput(experience, { experience = it }, experienceLevels, "Experience Level *", Modifier.weight(1f))
            OutlinedTextField(salary, { salary = it }, label = { Text("Salary Range *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(description, { description = it }, label = { Text("Job Description *") }, modifier = Modifier.fillMaxWidth().height(120.dp), colors = textFieldColors)

        EditableChipSection("Required Skills", skills) { skills = it }
        EditableListSection("Requirements", requirements) { requirements = it }

        DropdownInput(badge, { badge = it }, jobStatusOptions, "Job Status")

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Handle logo upload */ }
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.Image, contentDescription = "Company Logo")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Company Logo")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val newJob = Job(
                    id = "", // ID will be generated by ViewModel
                    title = title,
                    company = company,
                    location = location,
                    jobType = jobType,
                    experience = experience,
                    salaryRange = salary,
                    shortDescription = description,
                    skills = skills,
                    requirements = requirements,
                    badge = badge,
                    published = true, // Default to published
                    postedAt = 0, // Will be set by ViewModel
                    companyLogoUrl = "",
                    applicantsCount = 0
                )
                onCreateJob(newJob)
            }) { Text("Publish Job") }
        }
    }
}

@Composable
fun SegmentedControl(
    allJobsCount: Int,
    notPublishedCount: Int,
    showNotPublished: Boolean,
    onTabSelected: (Boolean) -> Unit
) {
    TabRow(
        selectedTabIndex = if (showNotPublished) 1 else 0,
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Tab(
            selected = !showNotPublished,
            onClick = { onTabSelected(false) },
            text = { Text(text = "All Jobs ($allJobsCount)") }
        )
        Tab(
            selected = showNotPublished,
            onClick = { onTabSelected(true) },
            text = { Text(text = "Not Published ($notPublishedCount)") }
        )
    }
}

@Composable
fun EmptyState(showNotPublished: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (showNotPublished) "No jobs to publish." else "No jobs available at the moment.",
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}
