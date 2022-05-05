package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.bangkit.story.DataDummy
import com.bangkit.story.data.remote.response.BaseResponse
import com.bangkit.story.getOrAwaitValue
import com.bangkit.story.utils.State
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
class RegisterViewModelTest: BaseViewModelTest() {

    private lateinit var registerViewModel: RegisterViewModel
    private val dummyResponse = DataDummy.registerResponse()
    private val name = "company"
    private val email = "company@gmail.com"
    private val password = "secret"

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(repository)
    }

    @Test
    fun `when register success should return user created message`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<State<BaseResponse>>()
            expectedResponse.value = State.Success(dummyResponse)
            `when`(repository.register(name, email, password)).thenReturn(expectedResponse)

            val actualResponse = registerViewModel.register(name, email, password).getOrAwaitValue()
            Mockito.verify(repository).register(name, email, password)
            assertNotNull(actualResponse)
            assertTrue(actualResponse is State.Success)
            assertEquals(dummyResponse.message, (actualResponse as State.Success).data.message)
        }

    @Test
    fun `when network error should return error`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<State<BaseResponse>>()
            expectedResponse.value = State.Error("error")
            `when`(repository.register(name, email, password)).thenReturn(expectedResponse)

            val actualResponse = registerViewModel.register(name, email, password).getOrAwaitValue()
            Mockito.verify(repository).register(name, email, password)
            assertNotNull(actualResponse)
            assertTrue(actualResponse is State.Error)
        }
}