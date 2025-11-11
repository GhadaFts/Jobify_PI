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
            delay(500)
            try {
                _uiState.value = PostsUiState.Success(repository.getJobs())
            } catch (e: Exception) {
                _uiState.value = PostsUiState.Error("Failed to load jobs: ${e.message}")
            }
        }
    }

    fun createJob(job: Job) {
        viewModelScope.launch {
            if (_uiState.value is PostsUiState.Success) {
                val currentJobs = (_uiState.value as PostsUiState.Success).jobs
                val newJob = job.copy(id = UUID.randomUUID().toString(), postedAt = System.currentTimeMillis())
                _uiState.value = PostsUiState.Success(listOf(newJob) + currentJobs)
            }
        }
    }

    fun publishJob(jobId: String) {
        viewModelScope.launch {
            delay(1000) // Simulate network call
            if (_uiState.value is PostsUiState.Success) {
                val currentJobs = (_uiState.value as PostsUiState.Success).jobs.toMutableList()
                val jobIndex = currentJobs.indexOfFirst { it.id == jobId }
                if (jobIndex != -1) {
                    currentJobs[jobIndex] = currentJobs[jobIndex].copy(published = true)
                    _uiState.value = PostsUiState.Success(currentJobs)
                }
            }
        }
    }

    fun saveJob(updatedJob: Job) {
        viewModelScope.launch {
            if (_uiState.value is PostsUiState.Success) {
                val currentJobs = (_uiState.value as PostsUiState.Success).jobs.toMutableList()
                val jobIndex = currentJobs.indexOfFirst { it.id == updatedJob.id }
                if (jobIndex != -1) {
                    currentJobs[jobIndex] = updatedJob
                    _uiState.value = PostsUiState.Success(currentJobs)
                }
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
