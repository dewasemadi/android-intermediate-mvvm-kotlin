package com.bangkit.story.ui.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.story.R
import com.bangkit.story.data.local.SessionManager
import com.bangkit.story.databinding.ActivitySettingsBinding
import com.bangkit.story.utils.onAlertDialog

class SettingsActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSessionManager()
        initViewBinding()
        initToolbar()
        onLogoutPressed()
        onLanguagePressed()
    }

    private fun initSessionManager(){
        sessionManager = SessionManager(this)
    }

    private fun initViewBinding() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initToolbar() {
        supportActionBar?.apply {
            this.title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun onLogoutPressed() {
        binding.logoutButton.setOnClickListener {
            val title = getString(R.string.logout)
            val message = getString(R.string.logout_message)
            val yes = getString(R.string.yes)
            val cancel = getString(R.string.cancel)

            onAlertDialog(this, title, message, cancel, yes) {
                // callback impl
                sessionManager.removeToken()
                onMoveToLoginActivity()
            }
        }
    }

    private fun onLanguagePressed() {
        binding.languageSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun onMoveToLoginActivity() {
        val toLogin = Intent(this, LoginActivity::class.java)
        toLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(toLogin)
    }
}