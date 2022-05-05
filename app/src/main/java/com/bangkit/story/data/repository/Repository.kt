package com.bangkit.story.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.bangkit.story.data.local.entity.MapStory
import com.bangkit.story.data.mediator.StoryRemoteMediator
import com.bangkit.story.data.local.room.StoryDatabase
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) : BaseRepository() {

    fun login(email: String, password: String) = safeApiCall {
        apiService.login(email, password)
    }

    fun register(name: String, email: String, password: String) = safeApiCall {
        apiService.register(name, email, password)
    }

    fun addNewStory(description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?) = safeApiCall {
        apiService.addNewStory(description, file, lat, lon)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(context: Context): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, context),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getAllStoriesForMaps(): LiveData<List<MapStory>> = storyDatabase.storyDao().getAllStoriesForMap()

    fun getImagesForWidget() = storyDatabase.storyDao().getImagesForWidget()

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(storyDatabase, apiService)
            }.also { instance = it }
    }
}