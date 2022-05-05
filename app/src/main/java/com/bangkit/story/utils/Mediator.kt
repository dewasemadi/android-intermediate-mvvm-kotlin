package com.bangkit.story.utils

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

// wrapping a suspend function(in our example body lambda function) into a LiveData
inline fun <T> liveResponse(crossinline body: suspend () -> State<T>) =
    liveData(Dispatchers.IO) {
        emit(State.Loading)
        emit(body())
    }