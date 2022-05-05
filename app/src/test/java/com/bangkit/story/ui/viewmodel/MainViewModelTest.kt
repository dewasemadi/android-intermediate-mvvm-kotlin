package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.bangkit.story.DataDummy
import com.bangkit.story.data.remote.response.Story
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
class MainViewModelTest : BaseViewModelTest() {

    private lateinit var mainViewModel: MainViewModel
    private val dummyResponse = DataDummy.allStoriesResponse()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(repository)
    }

    @Test
    fun `when get all stories should return list of story`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<PagingData<Story>>()
            expectedResponse.value = PagingData.from(dummyResponse)
            `when`(repository.getAllStories(context)).thenReturn(expectedResponse)

            val actualResponse = mainViewModel.getAllStories(context).getOrAwaitValue()
            Mockito.verify(repository).getAllStories(context)
            assertNotNull(actualResponse)
        }
}