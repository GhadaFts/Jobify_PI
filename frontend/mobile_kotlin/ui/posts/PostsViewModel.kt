package com.example.jobify.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobify.data.JobsRepository
import com.example.jobify.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    // State for managing the details dialog
    private val _selectedJob = MutableStateFlow<Job?>(null)
    val selectedJob: StateFlow<Job?> = _selectedJob.asStateFlow()

    private val repository = JobsRepository()

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = PostsUiState.Loading
            try {
                val jobs = repository.getJobs()
                _uiState.value = PostsUiState.Success(jobs)
            } catch (e: Exception) {
                _uiState.value = PostsUiState.Error("Failed to load jobs: ${e.message}")
            }
        }
    }

    fun publishJob(jobId: String) {
        if (_uiState.value is PostsUiState.Success) {
            val currentJobs = (_uiState.value as PostsUiState.Success).jobs.toMutableList()
            val jobIndex = currentJobs.indexOfFirst { it.id == jobId }
            if (jobIndex != -1) {
                currentJobs[jobIndex] = currentJobs[jobIndex].copy(published = true)
                _uiState.value = PostsUiState.Success(currentJobs)
            }
        }
    }

    fun toggleFilter() {
        _showNotPublished.update { !it }
    }

    // Functions to control the dialog
    fun showJobDetails(job: Job) {
        _selectedJob.value = job
    }

    fun dismissJobDetails() {
        _selectedJob.value = null
    }
}
