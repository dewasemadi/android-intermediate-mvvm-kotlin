package com.bangkit.story.data.local

import android.content.Context
import com.bangkit.story.utils.PREF_KEY
import com.bangkit.story.utils.PREF_NAME

class SessionManager(context: Context) {
    private var pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor = pref.edit()

    fun saveToken(token: String) {
        editor.apply {
            putString(PREF_KEY, token)
            apply()
        }
    }

    fun removeToken() {
        editor.apply {
            clear()
            apply()
        }
    }

    fun saveImage(key: String, url: String) {
        editor.apply {
            putString(key, url)
            apply()
        }
    }

    // if token exists { user logged-in } else { user log-out }
    fun getToken() = pref.getString(PREF_KEY, "").toString()

    fun getImage(key: String) = pref.getString(key, "").toString()
}