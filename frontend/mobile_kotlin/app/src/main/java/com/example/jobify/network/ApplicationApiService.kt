package com.example.jobify.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApplicationApiService {
    @GET("application-service/api/applications/joboffer/{jobOfferId}")
    suspend fun getByJobOffer(@Path("jobOfferId") jobOfferId: String): Response<List<Map<String, Any>>>

    @POST("application-service/api/applications")
    suspend fun createApplication(@Body payload: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>
}
