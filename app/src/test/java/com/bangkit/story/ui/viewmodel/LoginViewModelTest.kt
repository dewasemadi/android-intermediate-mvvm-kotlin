package com.bangkit.story.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.bangkit.story.DataDummy
import com.bangkit.story.data.remote.response.LoginResponse
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
class LoginViewModelTest: BaseViewModelTest() {

    private lateinit var loginViewModel: LoginViewModel
    private val dummyResponse = DataDummy.loginResponse()
    private val email = "company@gmail.com"
    private val password = "secret"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(repository)
    }

    @Test
    fun `when login success should return token`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<State<LoginResponse>>()
            expectedResponse.value = State.Success(dummyResponse)
            `when`(repository.login(email, password)).thenReturn(expectedResponse) // stuntman returned dummy data

            val actualResponse = loginViewModel.login(email, password).getOrAwaitValue()
            Mockito.verify(repository).login(email, password) // does the stuntman has called?
            assertNotNull(actualResponse)
            assertTrue(actualResponse is State.Success)
            assertEquals(dummyResponse.loginResult?.token, (actualResponse as State.Success).data.loginResult?.token)
        }

    @Test
    fun `when network error should return error`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = MutableLiveData<State<LoginResponse>>()
            expectedResponse.value = State.Error("error")
            `when`(repository.login(email, password)).thenReturn(expectedResponse)

            val actualResponse = loginViewModel.login(email, password).getOrAwaitValue()
            Mockito.verify(repository).login(email, password)
            assertNotNull(actualResponse)
            assertTrue(actualResponse is State.Error)
        }
}