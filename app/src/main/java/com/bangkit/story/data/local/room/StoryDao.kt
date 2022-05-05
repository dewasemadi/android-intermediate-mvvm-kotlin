package com.bangkit.story.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit.story.data.local.entity.MapStory
import com.bangkit.story.data.remote.response.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: ArrayList<Story>)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, Story>

    @Query("SELECT name, description, lat, lon  FROM story")
    fun getAllStoriesForMap(): LiveData<List<MapStory>>

    @Query("SELECT photoUrl FROM story LIMIT 5")
    fun getImagesForWidget(): List<String>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}