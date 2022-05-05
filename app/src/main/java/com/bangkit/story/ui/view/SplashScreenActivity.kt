package com.bangkit.story.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.bangkit.story.R
import com.bangkit.story.data.local.preferences.SessionManager
import com.bangkit.story.utils.DELAY

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        initSessionManager()
        initToolbar()
        initTheme()
        isUserLoggedIn()
    }

    private fun initSessionManager(){
        sessionManager = SessionManager(this)
    }

    private fun initToolbar() {
        supportActionBar?.hide()
    }

    private fun initTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun isUserLoggedIn() {
        val isLogin = sessionManager.getToken()
        Handler(Looper.getMainLooper()).postDelayed({
            val destination = if (isLogin.isNotEmpty()) Intent(this, MainActivity::class.java)
                              else Intent(this, LoginActivity::class.java)
            startActivity(destination)
            finish()
        }, DELAY)
    }
}