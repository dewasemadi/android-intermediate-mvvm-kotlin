package com.bangkit.story.data.remote.retrofit

import com.bangkit.story.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): BaseResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
    ): BaseResponse

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int,
    ): StoryResponse
}