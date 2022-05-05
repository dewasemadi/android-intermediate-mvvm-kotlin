package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.story.data.repository.Repository
import com.bangkit.story.utils.liveResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NewStoryViewModel(private val repository: Repository) : ViewModel() {
    fun addNewStory(description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?) = liveResponse {
        repository.addNewStory(description, file, lat, lon)
    }
}