# Line-by-Line Explanation: PostsScreen.kt & PostsViewModel.kt

## PostsScreen.kt - Complete Breakdown

```kotlin
package com.example.jobify.ui.posts
// Package declaration - organizes code into folders/namespaces
// This file is in: app/src/main/java/com/example/jobify/ui/posts/

// IMPORTS - Bringing in necessary classes and functions
import androidx.compose.animation.AnimatedVisibility
// AnimatedVisibility = Shows/hides UI with animation

import androidx.compose.foundation.clickable
// clickable = Makes any composable respond to clicks

import androidx.compose.foundation.layout.Arrangement
// Arrangement = Controls spacing between items (spacedBy, spaceEvenly, etc.)

import androidx.compose.foundation.layout.Box
// Box = Stacks children on top of each other (like FrameLayout)

import androidx.compose.foundation.layout.Column
// Column = Arranges children vertically (top to bottom)

import androidx.compose.foundation.layout.PaddingValues
// PaddingValues = Represents padding on all sides

import androidx.compose.foundation.layout.Row
// Row = Arranges children horizontally (left to right)

import androidx.compose.foundation.layout.Spacer
// Spacer = Empty space to create gaps between items

import androidx.compose.foundation.layout.fillMaxSize
// fillMaxSize = Fill entire available space (width + height)

import androidx.compose.foundation.layout.fillMaxWidth
// fillMaxWidth = Fill entire width, wrap height

import androidx.compose.foundation.layout.height
// height = Set fixed height

import androidx.compose.foundation.layout.padding
// padding = Add space around content

import androidx.compose.foundation.layout.size
// size = Set both width and height to same value

import androidx.compose.foundation.layout.width
// width = Set fixed width

import androidx.compose.foundation.lazy.LazyColumn
// LazyColumn = Efficient scrollable list (like RecyclerView)
// Only renders visible items

import androidx.compose.foundation.lazy.items
// items = Helper function to display list items in LazyColumn

import androidx.compose.foundation.shape.CircleShape
// CircleShape = Circular shape for clipping/drawing

import androidx.compose.foundation.shape.RoundedCornerShape
// RoundedCornerShape = Rectangle with rounded corners

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
// Icons = Provides Material Design icons
// filled.* = Imports all filled icon variants

import androidx.compose.material3.*
// Material3.* = Material Design 3 components (Button, Card, Text, etc.)

import androidx.compose.runtime.*
// runtime.* = Compose runtime functions:
//   - remember, mutableStateOf, collectAsState, etc.

import androidx.compose.ui.Alignment
// Alignment = How to align children (Center, Top, Bottom, etc.)

import androidx.compose.ui.Modifier
// Modifier = Chain of transformations for composables

import androidx.compose.ui.draw.clip
// clip = Clips composable to a shape

import androidx.compose.ui.graphics.Color
// Color = Color values (Color.Red, Color(0xFF123456), etc.)

import androidx.compose.ui.unit.dp
// dp = Density-independent pixels (scales with screen density)

import androidx.compose.ui.unit.sp
// sp = Scalable pixels (for text, respects user's font size settings)

import androidx.lifecycle.viewmodel.compose.viewModel
// viewModel() = Gets or creates ViewModel instance

import com.example.jobify.model.Job
// Job = Our data class representing a job posting


// ==================== MAIN SCREEN COMPOSABLE ====================

@OptIn(ExperimentalMaterial3Api::class)
// @OptIn = Acknowledges we're using experimental API
// ExperimentalMaterial3Api = Some Material3 features are still experimental

@Composable
// @Composable = Marks function as composable (can create UI)
// Composable functions:
//   - Must be called from other composable functions or setContent{}
//   - Can recompose (re-run) when state changes
//   - Should be fast and side-effect free

fun PostsScreen(
    // Function name - PascalCase by convention
    modifier: Modifier = Modifier,
    // modifier = Optional parameter to apply transformations from parent
    // Default value = Modifier (empty modifier)
    
    viewModel: PostsViewModel = viewModel()
    // viewModel = ViewModel instance that manages state
    // viewModel() = Compose function that creates/retrieves ViewModel
    // Benefits:
    //   - Survives configuration changes (rotation)
    //   - Scoped to activity/navigation destination
    //   - Auto-cleaned when no longer needed
) {
    // ==================== OBSERVE STATE FROM VIEWMODEL ====================
    
    val uiState by viewModel.uiState.collectAsState()
    // val = Immutable variable (can't reassign)
    // by = Kotlin delegate - unwraps the value automatically
    // viewModel.uiState = StateFlow<PostsUiState> (observable stream)
    // collectAsState() = Converts Flow to Compose State
    //   - Subscribes to Flow
    //   - Triggers recomposition when value changes
    //   - Auto-unsubscribes when composable leaves composition
    // Result: uiState is type PostsUiState (not State<PostsUiState>)
    
    val showNotPublished by viewModel.showNotPublished.collectAsState()
    // Observes filter state (show all jobs or only unpublished)
    
    val selectedJob by viewModel.selectedJob.collectAsState()
    // Observes which job is selected for detail view
    // null = no job selected
    
    val jobToEdit by viewModel.jobToEdit.collectAsState()
    // Observes which job is being edited
    // null = not editing any job
    
    
    // ==================== LOAD BOOKMARKS ON FIRST COMPOSITION ====================
    
    val bookmarkRepository = remember { com.example.jobify.data.BookmarkRepository.getInstance() }
    // remember { } = Caches value across recompositions
    // Without remember:
    //   - Every recomposition would create new instance
    //   - Wasteful and might lose state
    // With remember:
    //   - First composition: runs lambda, stores result
    //   - Subsequent recompositions: returns cached value
    // .getInstance() = Singleton pattern - same instance everywhere
    
    LaunchedEffect(Unit) {
        // LaunchedEffect = Runs suspend function in composable
        // Unit = Key parameter
        //   - When key changes, cancels old coroutine and starts new one
        //   - Unit never changes, so this runs ONLY ONCE when composable first appears
        // Use cases:
        //   - One-time initialization
        //   - Loading data
        //   - Starting animations
        
        bookmarkRepository.loadBookmarks()
        // Loads bookmarked jobs from API/storage
        // suspend function - runs on background thread
    }

    
    // ==================== SHOW DIALOGS CONDITIONALLY ====================
    
    selectedJob?.let { JobDetailsDialog(job = it, onDismiss = viewModel::dismissJobDetails) }
    // selectedJob?.let { } = Safe call
    //   - If selectedJob is null: nothing happens
    //   - If selectedJob has value: runs lambda with unwrapped value
    // JobDetailsDialog = Composable that shows job details in dialog
    // job = it = The unwrapped Job object
    // onDismiss = viewModel::dismissJobDetails
    //   - :: = Method reference (passes function as parameter)
    //   - When dialog is dismissed, calls viewModel.dismissJobDetails()
    
    jobToEdit?.let {
        EditJobDialog(
            job = it,
            onDismiss = viewModel::dismissEditJobDialog,
            onSave = viewModel::saveJob
            // When user clicks Save, calls viewModel.saveJob(updatedJob)
        )
    }

    
    // ==================== HANDLE DIFFERENT UI STATES ====================
    
    when (val state = uiState) {
        // when = Switch statement (but expression - returns value)
        // val state = uiState = Smart cast
        //   - Inside each branch, state has specific type
        //   - No need to cast manually
        
        is PostsUiState.Loading -> {
            // is = Type check + smart cast
            // PostsUiState.Loading = Sealed interface variant
            // This branch runs when data is being loaded
            
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Box = Container that stacks children
                // Modifier.fillMaxSize() = Fill entire screen
                // contentAlignment = Alignment.Center = Center content
                
                CircularProgressIndicator()
                // Shows spinning loading indicator
            }
        }
        
        is PostsUiState.Error -> {
            // This branch runs when error occurred
            // state is smart-casted to Error type
            
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = Color.Red)
                // state.message = Error message from sealed class
                // Shows red error text in center of screen
            }
        }
        
        is PostsUiState.Success -> {
            // This branch runs when data loaded successfully
            // state is smart-casted to Success type
            // state.jobs = List<Job>
            
            val jobsToShow = if (showNotPublished) state.jobs.filter { !it.published } else state.jobs
            // Conditional filtering:
            //   - If showNotPublished = true: show only unpublished jobs
            //   - If showNotPublished = false: show all jobs
            // filter { !it.published } = Keep jobs where published is false
            
            LazyColumn(
                // LazyColumn = Efficient scrollable vertical list
                // Only renders items visible on screen
                // Recycles views like RecyclerView
                
                modifier = modifier.fillMaxSize(),
                // Apply modifier from parameter + fillMaxSize
                
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                // Padding around content (not between items)
                // horizontal = left + right padding
                // vertical = top + bottom padding
            ) {
                // LazyColumn uses DSL (Domain Specific Language)
                // Inside {}, use special functions: item, items, stickyHeader
                
                item {
                    // item { } = Single item in list
                    // Doesn't repeat - shown once
                    
                    CreatePostWidget { newJob ->
                        // CreatePostWidget = Composable with form to create job
                        // { newJob -> } = Lambda callback
                        // When user submits form, this lambda is called with new job
                        
                        viewModel.createJob(newJob)
                        // Calls ViewModel to create job via API
                    }
                }
                
                item {
                    var searchText by remember { mutableStateOf("") }
                    // var = Mutable variable (can reassign)
                    // remember { mutableStateOf("") } = Creates observable state
                    //   - mutableStateOf = When changed, triggers recomposition
                    //   - remember = Survives recompositions
                    // by = Delegate - searchText is String, not State<String>
                    
                    OutlinedTextField(
                        // Material3 text input with outline border
                        
                        value = searchText,
                        // Current text value
                        
                        onValueChange = { searchText = it },
                        // Lambda called when text changes
                        // it = new text value
                        // searchText = it assigns new value (triggers recomposition)
                        
                        label = { Text("Search your jobs...") },
                        // Label that floats up when user types
                        // { Text(...) } = Composable lambda
                        
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                        // Icon at start of text field
                        // Icons.Filled.Search = Material icon
                        // contentDescription = Accessibility description
                        
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        // Modifier chain:
                        //   1. fillMaxWidth() = Full width
                        //   2. padding(vertical = 8.dp) = Top and bottom padding
                        // Order matters! padding after fillMaxWidth
                        
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White
                            // Background color of text field
                        )
                    )
                }
                
                item {
                    val allJobsCount = state.jobs.size
                    // Total number of jobs
                    
                    val notPublishedCount = state.jobs.filter { !it.published }.size
                    // Count jobs where published = false
                    // filter { } = Returns new list with matching items
                    // .size = Number of items in list
                    
                    SegmentedControl(allJobsCount, notPublishedCount, showNotPublished) { viewModel.toggleFilter(it) }
                    // SegmentedControl = Custom composable with tabs
                    // Parameters:
                    //   - allJobsCount = Number for "All Jobs" tab
                    //   - notPublishedCount = Number for "Not Published" tab
                    //   - showNotPublished = Currently selected tab
                    //   - { viewModel.toggleFilter(it) } = Callback when tab clicked
                }

                if (jobsToShow.isEmpty()) {
                    // If filtered list is empty
                    
                    item {
                        EmptyState(showNotPublished = showNotPublished)
                        // Shows "No jobs available" message
                    }
                } else {
                    // List has items
                    
                    items(jobsToShow, key = { it.id }) { job ->
                        // items() = Repeating items from list
                        // jobsToShow = List<Job>
                        // key = { it.id } = Unique key for each item
                        //   - Helps Compose track items when list changes
                        //   - Improves performance and animations
                        //   - it = current Job object
                        // { job -> } = Lambda for each item
                        //   - job = current Job object
                        
                        JobCard(
                            // Custom composable that displays job
                            
                            job = job,
                            // Pass job data
                            
                            onPublishClick = { viewModel.publishJob(job.id) },
                            // When "Publish" clicked, call ViewModel
                            
                            onViewDetailsClick = { viewModel.showJobDetails(job) },
                            // When "View Details" clicked, show dialog
                            
                            onEditClick = { viewModel.showEditJobDialog(job) }
                            // When "Edit" clicked, show edit dialog
                        )
                    }
                }
            }
        }
    }
}


// ==================== CREATE POST WIDGET ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostWidget(onCreateJob: (Job) -> Unit) {
    // onCreateJob: (Job) -> Unit = Function parameter
    //   - Takes Job as input
    //   - Returns nothing (Unit = void)
    //   - Called when user submits form
    
    var expanded by remember { mutableStateOf(false) }
    // expanded = Whether form is shown or hidden
    // Initial value = false (collapsed)

    Card(
        // Material3 card with elevation and rounded corners
        
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        // Full width, vertical padding
        
        shape = RoundedCornerShape(12.dp),
        // Rounded corners with 12dp radius
        
        elevation = CardDefaults.cardElevation(2.dp),
        // Shadow elevation (depth)
        
        colors = CardDefaults.cardColors(containerColor = Color.White)
        // Background color
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Column inside Card
            // 16dp padding on all sides
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                // Align children vertically in center
                
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                // 16dp space between children
                
                modifier = Modifier.clickable { expanded = !expanded }
                // When clicked, toggle expanded state
                // !expanded = opposite of current value
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    // User icon
                    
                    contentDescription = "User Profile",
                    // Accessibility description
                    
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    // 40dp size, clipped to circle
                    
                    tint = MaterialTheme.colorScheme.primary
                    // Color from theme
                )
                
                Text(
                    text = if(expanded) "Create a new job posting" else "Create a new job posting...",
                    // Different text based on state
                    
                    style = MaterialTheme.typography.bodyLarge,
                    // Typography from theme
                    
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Color from theme
                    
                    modifier = Modifier.weight(1f)
                    // weight(1f) = Take remaining space in Row
                    // Like layout_weight in XML
                )
            }

            AnimatedVisibility(visible = expanded) {
                // AnimatedVisibility = Shows/hides with animation
                // visible = expanded = Show when true, hide when false
                // Automatically animates fade + slide
                
                CreateJobForm(
                    onCreateJob = {
                        // Lambda called when form submitted
                        
                        onCreateJob(it)
                        // Call parent callback with new job
                        // it = new Job object from form
                        
                        expanded = false
                        // Collapse form after submission
                    },
                    onCancel = { expanded = false }
                    // Collapse form when cancelled
                )
            }
        }
    }
}


// ==================== CREATE JOB FORM ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobForm(onCreateJob: (Job) -> Unit, onCancel: () -> Unit) {
    // onCreateJob = Callback with new job
    // onCancel = Callback when user cancels
    
    val jobTypes = listOf("Full-time", "Part-time", "Contract", "Freelance")
    // Immutable list of job types
    // listOf() = Creates List (read-only)
    
    val experienceLevels = listOf("Entry-level", "Mid-level", "Senior-level", "Lead")
    val jobStatusOptions = listOf("open", "new", "hot job", "limited openings", "actively hiring", "urgent hiring")

    // Create state for each form field
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf(jobTypes.first()) }
    // jobTypes.first() = "Full-time" (initial value)
    
    var experience by remember { mutableStateOf(experienceLevels.first()) }
    var salary by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(emptyList<String>()) }
    // emptyList<String>() = Empty list of strings
    // Type annotation needed here
    
    var requirements by remember { mutableStateOf(emptyList<String>()) }
    var badge by remember { mutableStateOf(jobStatusOptions.first()) }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        // Form fields arranged vertically
        
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
        // Store colors to reuse
        // Avoids creating new object for each TextField

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Two text fields side by side
            
            OutlinedTextField(title, { title = it }, label = { Text("Job Title *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
            // Shorter syntax:
            //   - title = value
            //   - { title = it } = onValueChange
            //   - label = { Text(...) }
            //   - modifier = Modifier.weight(1f) = Take equal space
            //   - colors = textFieldColors
            
            OutlinedTextField(company, { company = it }, label = { Text("Company *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        // Empty space (8dp height)
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(location, { location = it }, label = { Text("Location *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
            DropdownInput(jobType, { jobType = it }, jobTypes, "Job Type *", Modifier.weight(1f))
            // DropdownInput = Custom composable for dropdown
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DropdownInput(experience, { experience = it }, experienceLevels, "Experience Level *", Modifier.weight(1f))
            OutlinedTextField(salary, { salary = it }, label = { Text("Salary Range *") }, modifier = Modifier.weight(1f), colors = textFieldColors)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(description, { description = it }, label = { Text("Job Description *") }, modifier = Modifier.fillMaxWidth().height(120.dp), colors = textFieldColors)
        // .height(120.dp) = Multiline text field

        EditableChipSection("Required Skills", skills) { skills = it }
        // Custom composable to add/remove skills as chips
        
        EditableListSection("Requirements", requirements) { requirements = it }
        // Custom composable to add/remove requirements

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
            // Align children to end (right in LTR)
        ) {
            OutlinedButton(onClick = onCancel) {
                // OutlinedButton = Button with outline, no fill
                Text("Cancel")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(onClick = {
                // Button = Filled button
                // onClick = Lambda when clicked
                
                val newJob = Job(
                    // Create new Job object with form data
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
                // Call callback with new job
            }) {
                Text("Publish Job")
            }
        }
    }
}


// ==================== SEGMENTED CONTROL (TABS) ====================

@Composable
fun SegmentedControl(
    allJobsCount: Int,
    notPublishedCount: Int,
    showNotPublished: Boolean,
    onTabSelected: (Boolean) -> Unit
    // onTabSelected = Callback with selected tab
    // Boolean = true for "Not Published", false for "All Jobs"
) {
    TabRow(
        // Material3 tab row
        
        selectedTabIndex = if (showNotPublished) 1 else 0,
        // 0 = first tab (All Jobs)
        // 1 = second tab (Not Published)
        
        containerColor = Color.White,
        // Background color
        
        contentColor = MaterialTheme.colorScheme.primary
        // Selected tab color
    ) {
        Tab(
            selected = !showNotPublished,
            // Selected if showNotPublished is false
            
            onClick = { onTabSelected(false) },
            // When clicked, tell parent to show all jobs
            
            text = { Text(text = "All Jobs ($allJobsCount)") }
            // Tab label with count
        )
        
        Tab(
            selected = showNotPublished,
            // Selected if showNotPublished is true
            
            onClick = { onTabSelected(true) },
            // When clicked, tell parent to show unpublished jobs
            
            text = { Text(text = "Not Published ($notPublishedCount)") }
        )
    }
}


// ==================== EMPTY STATE ====================

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
```

---

## PostsViewModel.kt - Complete Breakdown

```kotlin
package com.example.jobify.ui.posts
// Package declaration

import androidx.lifecycle.ViewModel
// ViewModel = Android Architecture Component
//   - Survives configuration changes (rotation)
//   - Holds UI state
//   - Business logic layer
//   - Auto-cleaned when no longer needed

import androidx.lifecycle.viewModelScope
// viewModelScope = CoroutineScope tied to ViewModel lifecycle
//   - Automatically cancelled when ViewModel is cleared
//   - Use for background operations

import com.example.jobify.data.JobsRepository
// Repository = Data layer (API calls, database operations)

import com.example.jobify.model.Job
// Job data class

import kotlinx.coroutines.delay
// delay() = Suspend function that pauses coroutine

import kotlinx.coroutines.flow.MutableStateFlow
// MutableStateFlow = Hot observable stream (always has value)
//   - Can be modified
//   - Emits latest value to new subscribers
//   - Like LiveData but better

import kotlinx.coroutines.flow.StateFlow
// StateFlow = Read-only version of MutableStateFlow

import kotlinx.coroutines.flow.asStateFlow
// asStateFlow() = Converts MutableStateFlow to StateFlow (read-only)

import kotlinx.coroutines.launch
// launch = Starts new coroutine
//   - Fire-and-forget (doesn't return result)
//   - Catches exceptions

import java.util.UUID
// UUID = Generates unique IDs


// ==================== UI STATE SEALED INTERFACE ====================

sealed interface PostsUiState {
    // sealed interface = Restricted class hierarchy
    //   - All subclasses must be in same file
    //   - Compiler knows all possible types
    //   - Perfect for representing states
    
    data class Success(val jobs: List<Job>) : PostsUiState
    // Success state
    // data class = Automatically generates:
    //   - equals(), hashCode(), toString()
    //   - copy() function
    //   - componentN() for destructuring
    // val jobs: List<Job> = Holds the data
    
    data class Error(val message: String) : PostsUiState
    // Error state with error message
    
    object Loading : PostsUiState
    // Loading state
    // object = Singleton (only one instance)
    //   - No data needed
    //   - Same instance everywhere
}

// Benefits of sealed interface:
//   1. Type-safe state representation
//   2. Compiler ensures all cases handled in when()
//   3. No need for nullable fields or flags
//   4. Clear and explicit


// ==================== VIEWMODEL CLASS ====================

class PostsViewModel : ViewModel() {
    // : ViewModel() = Inherits from ViewModel
    // ViewModel provides:
    //   - viewModelScope
    //   - onCleared() lifecycle method
    //   - Automatic cleanup
    
    
    // ==================== UI STATE ====================
    
    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    // private = Only accessible within this class
    // val = Immutable reference (can't reassign _uiState, but can modify its value)
    // MutableStateFlow<PostsUiState> = Type annotation
    // (PostsUiState.Loading) = Initial value (starts in loading state)
    
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()
    // public = Accessible from outside (UI layer)
    // StateFlow = Read-only (UI can't modify directly)
    // = _uiState.asStateFlow() = Exposes private mutable flow as public read-only flow
    
    // Pattern: Private mutable, public read-only
    // UI can only read, ViewModel controls changes
    
    
    private val _showNotPublished = MutableStateFlow(false)
    // Filter state: false = show all, true = show unpublished only
    // Initial value = false
    
    val showNotPublished: StateFlow<Boolean> = _showNotPublished.asStateFlow()
    // Public read-only version
    
    
    private val _selectedJob = MutableStateFlow<Job?>(null)
    // Selected job for detail view
    // Job? = Nullable (can be null)
    // null = No job selected initially
    
    val selectedJob: StateFlow<Job?> = _selectedJob.asStateFlow()
    
    
    private val _jobToEdit = MutableStateFlow<Job?>(null)
    // Job being edited
    // null = Not editing
    
    val jobToEdit: StateFlow<Job?> = _jobToEdit.asStateFlow()

    
    // ==================== REPOSITORY ====================
    
    private val repository = JobsRepository()
    // Create repository instance
    // Handles all data operations (API calls)
    // In real app, should inject via dependency injection

    
    // ==================== INITIALIZATION ====================
    
    init {
        // init block = Runs when ViewModel is created
        // Before constructor finishes
        
        loadJobs()
        // Load jobs immediately when ViewModel created
    }

    
    // ==================== LOAD JOBS ====================
    
    fun loadJobs() {
        // public function = Can be called from UI
        
        viewModelScope.launch {
            // viewModelScope = CoroutineScope from ViewModel
            // launch = Start coroutine
            //   - Runs in background
            //   - Auto-cancelled if ViewModel destroyed
            //   - Catches exceptions automatically
            
            _uiState.value = PostsUiState.Loading
            // Set state to Loading
            // .value = Current value of StateFlow
            // UI will recompose and show loading indicator
            
            try {
                // try-catch for error handling
                
                // network call
                val jobs = repository.getJobs()
                // repository.getJobs() = suspend function
                //   - Makes HTTP request to backend
                //   - Transforms JSON to List<Job>
                //   - Runs on background thread (Dispatchers.IO)
                // Execution pauses here until response received
                
                _uiState.value = PostsUiState.Success(jobs)
                // Update state to Success with jobs data
                // UI recomposes and shows list
                
            } catch (e: Exception) {
                // catch = Handles any exception
                // e: Exception = Exception object
                
                _uiState.value = PostsUiState.Error("Failed to load jobs: ${e.message}")
                // Update state to Error
                // ${e.message} = String interpolation (inserts exception message)
                // UI recomposes and shows error
            }
        }
    }

    
    // ==================== CREATE JOB ====================
    
    fun createJob(job: Job) {
        // Called when user submits create job form
        // job = New job data from form
        
        viewModelScope.launch {
            // Launch coroutine
            
            try {
                // Prepare payload expected by backend
                val payload = mapOf(
                    // mapOf() = Creates immutable map
                    // "key" to value = Key-value pairs
                    
                    "title" to job.title,
                    "jobPosition" to job.title,
                    // Backend expects "jobPosition" field
                    
                    "company" to job.company,
                    "companyLogo" to (job.companyLogoUrl ?: ""),
                    // (job.companyLogoUrl ?: "") = Elvis operator
                    //   - If companyLogoUrl is null, use ""
                    //   - Avoids sending null to backend
                    
                    "location" to job.location,
                    "type" to job.jobType,
                    "experience" to job.experience,
                    "salary" to job.salaryRange,
                    "description" to job.shortDescription,
                    "skills" to job.skills,
                    "requirements" to job.requirements,
                    "status" to "open",
                    "published" to (job.published)
                    // (job.published) = Parentheses for clarity
                )

                repository.createJob(payload)
                // Call repository to create job
                // suspend function - makes HTTP POST request
                // Sends payload as JSON to backend
                // Backend saves to database and returns created job
                
                // refresh authoritative list
                loadJobs()
                // Reload all jobs to show new job
                // Ensures UI matches backend state
                
            } catch (e: Exception) {
                // If API call fails
                
                // keep previous UI state and optionally expose error
                _uiState.value = PostsUiState.Error("Failed to create job: ${e.message}")
                // Show error to user
            }
        }
    }

    
    // ==================== PUBLISH JOB ====================
    
    fun publishJob(jobId: String) {
        // Called when user clicks "Publish" button
        // jobId = ID of job to publish
        
        viewModelScope.launch {
            try {
                // Call backend to update published flag
                val payload = mapOf("published" to true)
                // Only send published field
                // Backend will update just this field
                
                repository.updateJob(jobId, payload)
                // suspend function - HTTP PUT request
                // PUT /api/jobs/{jobId} with payload
                
                // Refresh list
                loadJobs()
                // Reload to show updated job
                
            } catch (e: Exception) {
                _uiState.value = PostsUiState.Error("Failed to publish job: ${e.message}")
            }
        }
    }

    
    // ==================== SAVE EDITED JOB ====================
    
    fun saveJob(updatedJob: Job) {
        // Called when user saves edited job
        // updatedJob = Job with modified fields
        
        viewModelScope.launch {
            try {
                val payload = mapOf(
                    // Map all fields that can be edited
                    "title" to updatedJob.title,
                    "jobPosition" to updatedJob.title,
                    "company" to updatedJob.company,
                    "location" to updatedJob.location,
                    "type" to updatedJob.jobType,
                    "experience" to updatedJob.experience,
                    "salary" to updatedJob.salaryRange,
                    "description" to updatedJob.shortDescription,
                    "skills" to updatedJob.skills,
                    "requirements" to updatedJob.requirements,
                    "status" to "open",
                    "published" to updatedJob.published
                )

                repository.updateJob(updatedJob.id, payload)
                // HTTP PUT request with all updated fields
                
                // Refresh authoritative list
                loadJobs()
                // Reload to show changes
                
            } catch (e: Exception) {
                _uiState.value = PostsUiState.Error("Failed to save job: ${e.message}")
            } finally {
                // finally = Runs whether try succeeds or fails
                
                dismissEditJobDialog()
                // Close edit dialog regardless of result
            }
        }
    }

    
    // ==================== UI ACTIONS ====================
    
    fun toggleFilter(show: Boolean) {
        // Called when user clicks tab
        // show = true for "Not Published", false for "All Jobs"
        
        _showNotPublished.value = show
        // Update filter state
        // UI recomposes and shows filtered list
    }
    
    fun showJobDetails(job: Job) {
        // Called when user clicks "View Details"
        
        _selectedJob.value = job
        // Set selected job
        // UI recomposes and shows dialog
    }
    
    fun dismissJobDetails() {
        // Called when user closes detail dialog
        
        _selectedJob.value = null
        // Clear selected job
        // UI recomposes and hides dialog
    }
    
    fun showEditJobDialog(job: Job) {
        // Called when user clicks "Edit"
        
        _jobToEdit.value = job
        // Set job to edit
        // UI shows edit dialog
    }
    
    fun dismissEditJobDialog() {
        // Called when user closes edit dialog
        
        _jobToEdit.value = null
        // Clear edit state
        // UI hides dialog
    }
}
```

---

## Summary: How It All Works Together

### 1. **Initial Load:**
```
App Starts
    → PostsActivity.onCreate()
        → setContent { PostsScreen() }
            → PostsScreen composable function runs
                → viewModel() creates PostsViewModel
                    → init { loadJobs() }
                        → viewModelScope.launch { }
                            → _uiState.value = Loading
                                → UI shows CircularProgressIndicator
                            → repository.getJobs()
                                → HTTP GET to backend
                                → Backend queries MongoDB
                                → Returns JSON
                            → Transform JSON to List<Job>
                            → _uiState.value = Success(jobs)
                                → UI recomposes
                                → Shows LazyColumn with jobs
```

### 2. **Creating a Job:**
```
User Types in Form
    → State variables update (title, company, etc.)
        → Each keystroke triggers recomposition of TextField
            
User Clicks "Publish Job"
    → onClick lambda runs
        → Creates Job object
        → Calls viewModel.createJob(newJob)
            → viewModelScope.launch
                → Build payload map
                → repository.createJob(payload)
                    → HTTP POST to backend
                    → Backend saves to MongoDB
                    → Returns created job with ID
                → loadJobs() refreshes list
                    → _uiState.value = Success(updatedJobs)
                        → UI recomposes
                        → New job appears in list
```

### 3. **State Flow:**
```
ViewModel:  _uiState.value = Success(jobs)
                ↓
            uiState: StateFlow emits new value
                ↓
UI:         val uiState by viewModel.uiState.collectAsState()
                ↓
            Recomposition triggered
                ↓
            when (uiState) { is Success -> ... }
                ↓
            LazyColumn reruns with new data
                ↓
            User sees updated list
```

---

## Key Takeaways:

1. **@Composable** = Functions that create UI
2. **State** = Observable data that triggers recomposition
3. **ViewModel** = Holds state, survives rotation
4. **StateFlow** = Observable stream (like LiveData)
5. **Coroutines** = Async operations (network calls)
6. **Repository** = Data layer (API calls)
7. **Sealed Interface** = Type-safe state representation
8. **Recomposition** = UI automatically updates when state changes

This is the complete flow from UI → ViewModel → Repository → API → Backend and back!
