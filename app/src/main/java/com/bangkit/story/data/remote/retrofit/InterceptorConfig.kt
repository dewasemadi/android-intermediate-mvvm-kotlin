package com.bangkit.story.data.remote.retrofit

import android.content.Context
import com.bangkit.story.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class InterceptorConfig(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()
        val request: Request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}