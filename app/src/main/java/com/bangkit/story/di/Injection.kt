package com.bangkit.story.di

import android.content.Context
import com.bangkit.story.data.remote.retrofit.ApiConfig
import com.bangkit.story.data.repository.Repository

object Injection {
    fun provideRepository(context: Context): Repository{
        val apiService = ApiConfig.getApiService(context)
        return Repository.getInstance(apiService)
    }
}