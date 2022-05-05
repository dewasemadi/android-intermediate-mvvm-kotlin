package com.bangkit.story.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.bangkit.story.data.local.entity.MapStory
import com.bangkit.story.data.local.room.StoryDao
import com.bangkit.story.data.remote.response.Story

class FakeStoryDao : StoryDao {
    private var stories = ArrayList<Story>()

    override suspend fun insertStory(story: ArrayList<Story>) {
        stories.addAll(story)
    }

    override fun getAllStories(): PagingSource<Int, Story> {
        TODO("Not yet implemented")
    }

    override fun getAllStoriesForMap(): LiveData<List<MapStory>> {
        val liveDataMapStories = MutableLiveData<List<MapStory>>()
        val mapStories = ArrayList<MapStory>()

        stories.forEach {
            val mapStory = MapStory(it.name, it.description, it.lat, it.lon)
            mapStories.add(mapStory)
        }

        liveDataMapStories.value = mapStories
        return liveDataMapStories
    }

    override fun getImagesForWidget(): List<String> {
        val images = ArrayList<String>()

        stories.forEach {
            val image = it.photoUrl
            if (image != null)
                images.add(image)
        }

        return images.toList()
    }

    override suspend fun deleteAll() {
        stories.clear()
    }
}