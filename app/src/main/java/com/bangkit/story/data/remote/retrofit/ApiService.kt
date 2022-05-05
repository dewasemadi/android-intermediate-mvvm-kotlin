package com.bangkit.story.data.remote.retrofit

import androidx.annotation.Nullable
import com.bangkit.story.data.remote.response.Base
import com.bangkit.story.data.remote.response.Login
import com.bangkit.story.data.remote.response.Stories
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
    ): Base

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Login

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Nullable @Part("lat") lat: Float? = null,
        @Nullable @Part("lon") lon: Float? = null,
    ): Base

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Stories
}