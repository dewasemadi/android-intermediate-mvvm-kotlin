package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.story.data.repository.Repository

class LoginViewModel(private val repository: Repository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)
}