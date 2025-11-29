package com.example.jobify.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobify.data.JobsRepository
import com.example.jobify.model.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface PostsUiState {
    data class Success(val jobs: List<Job>) : PostsUiState
    data class Error(val message: String) : PostsUiState
    object Loading : PostsUiState
}

class PostsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    private val _showNotPublished = MutableStateFlow(false)
    val showNotPublished: StateFlow<Boolean> = _showNotPublished.asStateFlow()

    private val _selectedJob = MutableStateFlow<Job?>(null)
    val selectedJob: StateFlow<Job?> = _selectedJob.asStateFlow()

    private val _jobToEdit = MutableStateFlow<Job?>(null)
    val jobToEdit: StateFlow<Job?> = _jobToEdit.asStateFlow()

    private val repository = JobsRepository()

    init { loadJobs() }

    fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = PostsUiState.Loading
            try {
                // network call
                val jobs = repository.getJobs()
                _uiState.value = PostsUiState.Success(jobs)
            } catch (e: Exception) {
                _uiState.value = PostsUiState.Error("Failed to load jobs: ${e.message}")
            }
        }
    }

    fun createJob(job: Job) {
        viewModelScope.launch {
            try {
                // Prepare payload expected by backend
                val payload = mapOf(
                    "title" to job.title,
                    "jobPosition" to job.title,
                    "company" to job.company,
                    "companyLogo" to (job.companyLogoUrl ?: ""),
                    "location" to job.location,
                    "type" to job.jobType,
                    "experience" to job.experience,
                    "salary" to job.salaryRange,
                    "description" to job.shortDescription,
                    "skills" to job.skills,
                    "requirements" to job.requirements,
                    "status" to "open",
                    "published" to (job.published)
                )

                repository.createJob(payload)
                // refresh authoritative list
                loadJobs()
            } catch (e: Exception) {
                // keep previous UI state and optionally expose error
                _uiState.value = PostsUiState.Error("Failed to create job: ${e.message}")
            }
        }
    }

    fun publishJob(jobId: String) {
        viewModelScope.launch {
            try {
                // Call backend to update published flag
                val payload = mapOf("published" to true)
                repository.updateJob(jobId, payload)
                // Refresh list
                loadJobs()
            } catch (e: Exception) {
                _uiState.value = PostsUiState.Error("Failed to publish job: ${e.message}")
            }
        }
    }

    fun saveJob(updatedJob: Job) {
        viewModelScope.launch {
            try {
                val payload = mapOf(
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
                // Refresh authoritative list
                loadJobs()
            } catch (e: Exception) {
                _uiState.value = PostsUiState.Error("Failed to save job: ${e.message}")
            } finally {
                dismissEditJobDialog()
            }
        }
    }

    fun toggleFilter(show: Boolean) { _showNotPublished.value = show }
    fun showJobDetails(job: Job) { _selectedJob.value = job }
    fun dismissJobDetails() { _selectedJob.value = null }
    fun showEditJobDialog(job: Job) { _jobToEdit.value = job }
    fun dismissEditJobDialog() { _jobToEdit.value = null }
}
