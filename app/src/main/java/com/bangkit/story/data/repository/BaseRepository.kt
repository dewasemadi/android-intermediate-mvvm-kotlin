package com.bangkit.story.data.repository

import com.bangkit.story.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException

abstract class BaseRepository {
    suspend inline fun <T> safeApiCall(crossinline body: suspend () -> T): State<T> {
        return try {
            val response = withContext(Dispatchers.IO) { body() }   // blocking block
            State.Success(response)
        } catch (throwable: Throwable) {
            val message = throwable.message.toString()

            when (throwable) {
                is HttpException -> {
                    val nullableMessage = "{'message': 'Network Error'}"
                    val errorBody = JSONObject(throwable.response()?.errorBody()?.string() ?: nullableMessage )
                    State.Error(errorBody.getString("message"))
                }
                is SocketException -> State.Error(message)
                is UnknownHostException -> State.Error(message)
                else -> State.Error(message)
            }
        }
    }
}