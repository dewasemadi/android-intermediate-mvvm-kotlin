package com.bangkit.story.ui.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bangkit.story.MainCoroutineRule
import com.bangkit.story.data.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito

@ExperimentalCoroutinesApi
open class BaseViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var repository: Repository
    val context: Context = Mockito.mock(Context::class.java)
}