package com.bangkit.story

import com.bangkit.story.data.local.entity.MapStory
import com.bangkit.story.data.remote.response.*

object DataDummy {
    fun loginResponse(): LoginResponse =
        LoginResponse(
            false,
            "success",
            LoginResult(
                "123",
                "I Dewa Putu Semadi",
                "jwt-token"
            )
        )

    fun registerResponse(): BaseResponse =
        BaseResponse(
            false,
            "user created"
        )

    fun addNewStoryResponse(): BaseResponse =
        BaseResponse(
            false,
            "success"
        )

    fun allStoriesResponse(): List<Story> {
        val stories = ArrayList<Story>()
        for (i in 0..5) {
            val story = Story(
                "id-$i",
                "name-$i",
                "description-$i",
                "www.google.com",
                "2022-02-22T22:22:22Z",
                1.23,
                1.23
            )
            stories.add(story)
        }
        return stories
    }

    fun allStoriesForMapsResponse(): List<MapStory> {
        val stories = ArrayList<MapStory>()
        for (i in 0..5) {
            val story = MapStory(
                "name-$i",
                "description-$i",
                1.23,
                1.23
            )
            stories.add(story)
        }
        return stories
    }

    fun apiAllStoriesResponse(): StoryResponse {
        return StoryResponse(
            false,
            "success",
            allStoriesResponse() as ArrayList<Story>
        )
    }
}