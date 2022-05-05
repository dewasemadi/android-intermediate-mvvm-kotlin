package com.bangkit.story.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bangkit.story.DataDummy
import com.bangkit.story.MainCoroutineRule
import com.bangkit.story.data.local.room.StoryDao
import com.bangkit.story.data.remote.response.Story
import com.bangkit.story.data.remote.retrofit.ApiService
import com.bangkit.story.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var storyDao: StoryDao

    @Before
    fun setUp() {
        storyDao = FakeStoryDao()
    }

    @Test
    fun `when getAllStories should return data not null`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = DataDummy.apiAllStoriesResponse()
            `when`(apiService.getAllStories(1, 5, 1)).thenReturn(expectedResponse)

            val actualResponse = apiService.getAllStories(1,5,1)
            Mockito.verify(apiService).getAllStories(1,5,1)
            assertNotNull(actualResponse)
            assertEquals(expectedResponse.listStory?.size, actualResponse.listStory?.size)
        }

    @Test
    fun `when insert stories should exist in getAllStoriesForMap`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = DataDummy.allStoriesResponse() as ArrayList<Story>
            storyDao.insertStory(expectedResponse)

            val actualResponse = storyDao.getAllStoriesForMap().getOrAwaitValue()
            assertNotNull(actualResponse)
            assertEquals(expectedResponse.size, actualResponse.size)
            assertEquals(expectedResponse[0].name, actualResponse[0].name)
        }

    @Test
    fun `when delete all stories should not exist in getAllStoriesForMap`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = DataDummy.allStoriesResponse() as ArrayList<Story>
            storyDao.apply {
                insertStory(expectedResponse)
                deleteAll()
            }
            val actualResponse = storyDao.getAllStoriesForMap().getOrAwaitValue()
            assertTrue(actualResponse.isEmpty())
            assertNotEquals(expectedResponse.size, actualResponse.size)
        }
}