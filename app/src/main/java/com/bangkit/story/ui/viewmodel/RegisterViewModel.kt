package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit.story.data.repository.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}