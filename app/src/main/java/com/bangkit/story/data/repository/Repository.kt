package com.bangkit.story.data.repository

import com.bangkit.story.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(private val apiService: ApiService) : BaseRepository() {
    suspend fun login(email: String, password: String) = safeApiCall {
        apiService.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String) = safeApiCall {
        apiService.register(name, email, password)
    }

    suspend fun addNewStory(description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?) = safeApiCall {
        apiService.addNewStory(description, file, lat, lon)
    }

    suspend fun getAllStories(page: Int, size: Int) = safeApiCall {
        apiService.getAllStories(page, size)
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(apiService: ApiService): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService)
            }.also { instance = it }
    }
}