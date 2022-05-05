package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.bangkit.story.DataDummy
import com.bangkit.story.data.remote.response.BaseResponse
import com.bangkit.story.getOrAwaitValue
import com.bangkit.story.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NewStoryViewModelTest: BaseViewModelTest() {

    private lateinit var newStoryViewModel: NewStoryViewModel
    private val dummyResponse = DataDummy.addNewStoryResponse()
    private val description = "description".toRequestBody()
    private val file = MultipartBody.Part.createFormData("photo", "101011")
    private val lat = 1.23f
    private val lon = 1.23f

    @Before
    fun setUp() {
        newStoryViewModel = NewStoryViewModel(repository)
    }

    @Test
    fun `when add new story success should return success message`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<State<BaseResponse>>()
            expectedResponse.value = State.Success(dummyResponse)
            `when`(repository.addNewStory(description, file, lat, lon)).thenReturn(expectedResponse)

            val actualResponse = newStoryViewModel.addNewStory(description, file, lat, lon).getOrAwaitValue()
            Mockito.verify(repository).addNewStory(description, file, lat, lon)
            assertNotNull(actualResponse)
            assertTrue(actualResponse is State.Success)
            assertEquals(dummyResponse.message, (actualResponse as State.Success).data.message)
        }

    @Test
    fun `when network error should return error`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<State<BaseResponse>>()
            expectedResponse.value = State.Error("error")
            `when`(repository.addNewStory(description, file, lat, lon)).thenReturn(expectedResponse)

            val actualResponse = newStoryViewModel.addNewStory(description, file, lat, lon).getOrAwaitValue()
            Mockito.verify(repository).addNewStory(description, file, lat, lon)
            assertNotNull(actualResponse)
            assertTrue(actualResponse is State.Error)
        }
}