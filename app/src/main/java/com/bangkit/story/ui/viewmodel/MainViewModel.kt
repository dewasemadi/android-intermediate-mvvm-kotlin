package com.bangkit.story.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.data.repository.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun getAllStories(context: Context): LiveData<PagingData<Story>> =
        repository.getAllStories(context).cachedIn(viewModelScope)
}