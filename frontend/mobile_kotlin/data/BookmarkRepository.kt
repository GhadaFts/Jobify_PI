package com.example.jobify.data

import android.util.Log
import com.example.jobify.network.ApiClient
import com.example.jobify.network.BookmarkDTO
import com.example.jobify.network.BookmarkRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Repository for managing bookmarks (favorites)
 * Follows the same pattern as Angular's BookmarkService with reactive state
 */
class BookmarkRepository {
    private val api = ApiClient.bookmarkService

    // Cache bookmarked job offer IDs for current user
    private val _bookmarkedJobIds = MutableStateFlow<Set<Long>>(emptySet())
    val bookmarkedJobIds: StateFlow<Set<Long>> = _bookmarkedJobIds.asStateFlow()

    /**
     * Load all bookmarks for the current user
     */
    suspend fun loadBookmarks() = withContext(Dispatchers.IO) {
        try {
            val response = api.getMyBookmarks().execute()
            if (response.isSuccessful) {
                val bookmarks = response.body() ?: emptyList()
                val jobOfferIds = bookmarks.map { it.jobOfferId }.toSet()
                _bookmarkedJobIds.value = jobOfferIds
                Log.d("BookmarkRepository", "Loaded ${jobOfferIds.size} bookmarks")
            } else {
                Log.e("BookmarkRepository", "Failed to load bookmarks: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("BookmarkRepository", "Error loading bookmarks: ${e.message}", e)
        }
    }

    /**
     * Add a bookmark for a job offer
     */
    suspend fun addBookmark(jobOfferId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = BookmarkRequestDTO(jobOfferId)
            val response = api.createBookmark(request).execute()
            if (response.isSuccessful) {
                // Update local cache
                val current = _bookmarkedJobIds.value.toMutableSet()
                current.add(jobOfferId)
                _bookmarkedJobIds.value = current
                Log.d("BookmarkRepository", "Added bookmark for job $jobOfferId")
                true
            } else {
                Log.e("BookmarkRepository", "Failed to add bookmark: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("BookmarkRepository", "Error adding bookmark: ${e.message}", e)
            false
        }
    }

    /**
     * Remove a bookmark for a job offer
     */
    suspend fun removeBookmark(jobOfferId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteBookmark(jobOfferId).execute()
            if (response.isSuccessful || response.code() == 204) {
                // Update local cache
                val current = _bookmarkedJobIds.value.toMutableSet()
                current.remove(jobOfferId)
                _bookmarkedJobIds.value = current
                Log.d("BookmarkRepository", "Removed bookmark for job $jobOfferId")
                true
            } else {
                Log.e("BookmarkRepository", "Failed to remove bookmark: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("BookmarkRepository", "Error removing bookmark: ${e.message}", e)
            false
        }
    }

    /**
     * Toggle bookmark (add if not exists, remove if exists)
     */
    suspend fun toggleBookmark(jobOfferId: Long): Boolean {
        return if (isBookmarked(jobOfferId)) {
            removeBookmark(jobOfferId)
        } else {
            addBookmark(jobOfferId)
        }
    }

    /**
     * Check if a job offer is bookmarked
     */
    fun isBookmarked(jobOfferId: Long): Boolean {
        return _bookmarkedJobIds.value.contains(jobOfferId)
    }

    companion object {
        // Singleton instance
        private var instance: BookmarkRepository? = null

        fun getInstance(): BookmarkRepository {
            return instance ?: synchronized(this) {
                instance ?: BookmarkRepository().also { instance = it }
            }
        }
    }
}
