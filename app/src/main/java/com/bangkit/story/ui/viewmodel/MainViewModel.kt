package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.story.data.repository.Repository
import com.bangkit.story.utils.liveResponse

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun getAllStories(page: Int, size: Int) = liveResponse {
        repository.getAllStories(page, size)
    }
}