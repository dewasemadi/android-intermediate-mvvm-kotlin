package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.story.data.repository.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {
    fun getAllStoriesForMaps() = repository.getAllStoriesForMaps()
}