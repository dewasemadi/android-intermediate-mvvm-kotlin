package com.bangkit.story.data.local.preferences

import android.content.Context
import com.bangkit.story.utils.PREF_LOCATION_KEY
import com.bangkit.story.utils.PREF_NAME
import com.bangkit.story.utils.PREF_TOKEN_KEY

class SessionManager(context: Context) {
    private var pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor = pref.edit()

    fun setToken(token: String) {
        editor.apply {
            putString(PREF_TOKEN_KEY, token)
            apply()
        }
    }

    fun setIsWithLocation(value: Boolean){
        editor.apply{
            putBoolean(PREF_LOCATION_KEY, value)
            apply()
        }
    }

    // if token exists { user logged-in } else { user log-out }
    fun getToken() = pref.getString(PREF_TOKEN_KEY, "").toString()

    fun getIsWithLocation() = pref.getBoolean(PREF_LOCATION_KEY, false)

    fun clearSession() {
        editor.apply {
            clear()
            apply()
        }
    }
}