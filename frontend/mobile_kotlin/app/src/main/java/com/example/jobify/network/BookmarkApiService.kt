package com.example.jobify.network

import retrofit2.Call
import retrofit2.http.*

data class BookmarkDTO(
    val id: String,
    val jobSeekerId: String,
    val jobOfferId: Long,
    val createdAt: String
)

data class BookmarkRequestDTO(
    val jobOfferId: Long
)

interface BookmarkApiService {
    /**
     * Create a bookmark for a job offer
     */
    @POST("/application-service/api/bookmarks")
    fun createBookmark(@Body request: BookmarkRequestDTO): Call<BookmarkDTO>

    /**
     * Get all bookmarks for current user
     */
    @GET("/application-service/api/bookmarks/my-bookmarks")
    fun getMyBookmarks(): Call<List<BookmarkDTO>>

    /**
     * Check if a job is bookmarked
     */
    @GET("/application-service/api/bookmarks/check/{jobOfferId}")
    fun isBookmarked(@Path("jobOfferId") jobOfferId: Long): Call<Boolean>

    /**
     * Delete a bookmark by job offer ID
     */
    @DELETE("/application-service/api/bookmarks/job/{jobOfferId}")
    fun deleteBookmark(@Path("jobOfferId") jobOfferId: Long): Call<Void>
}
