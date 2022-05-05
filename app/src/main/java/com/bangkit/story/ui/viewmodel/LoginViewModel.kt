package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.story.data.repository.Repository
import com.bangkit.story.utils.liveResponse

class LoginViewModel(private val repository: Repository): ViewModel() {
    fun login(email: String, password: String) = liveResponse {
        repository.login(email, password)
    }
}