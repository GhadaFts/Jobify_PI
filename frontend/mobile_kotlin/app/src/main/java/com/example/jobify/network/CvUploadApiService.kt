package com.example.jobify.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CvUploadApiService {
    @Multipart
    @POST("application-service/api/cv/upload")
    fun uploadCV(
        @Part file: MultipartBody.Part,
        @Part("jobSeekerId") jobSeekerId: RequestBody,
        @Part("jobOfferId") jobOfferId: RequestBody
    ): Call<CVUploadResponse>

    @DELETE("application-service/api/cv/delete")
    fun deleteCV(@Query("cvLink") cvLink: String): Call<Void>

    @GET("application-service/api/cv/view/{cvLink}")
    fun viewCV(@Path("cvLink") cvLink: String): Call<okhttp3.ResponseBody>

    @GET("application-service/api/cv/download/{cvLink}")
    fun downloadCV(@Path("cvLink") cvLink: String): Call<okhttp3.ResponseBody>
}

data class CVUploadResponse(
    val cvLink: String,
    val fileName: String,
    val fileSize: Long,
    val uploadedAt: String
)