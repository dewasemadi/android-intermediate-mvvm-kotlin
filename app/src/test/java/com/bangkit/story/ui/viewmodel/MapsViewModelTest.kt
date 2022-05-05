package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.bangkit.story.DataDummy
import com.bangkit.story.data.local.entity.MapStory
import com.bangkit.story.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest : BaseViewModelTest() {

    private lateinit var mapsViewModel: MapsViewModel
    private val dummyResponse = DataDummy.allStoriesForMapsResponse()

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(repository)
    }

    @Test
    fun `when get all stories for map should return list of story for map`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<List<MapStory>>()
            expectedResponse.value = dummyResponse
            `when`(repository.getAllStoriesForMaps()).thenReturn(expectedResponse)

            val actualResponse = mapsViewModel.getAllStoriesForMaps().getOrAwaitValue()
            Mockito.verify(repository).getAllStoriesForMaps()
            assertNotNull(actualResponse)
            assertEquals(dummyResponse.size, actualResponse.size)
        }
}