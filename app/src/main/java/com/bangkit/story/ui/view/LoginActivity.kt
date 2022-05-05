package com.bangkit.story.ui.view

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.story.R
import com.bangkit.story.data.local.SessionManager
import com.bangkit.story.databinding.ActivityLoginBinding
import com.bangkit.story.ui.viewmodel.LoginViewModel
import com.bangkit.story.ui.viewmodel.ViewModelFactory
import com.bangkit.story.utils.State
import com.bangkit.story.utils.fadeIn
import com.bangkit.story.utils.isEmailValid
import com.bangkit.story.utils.isPasswordValid

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSessionManager()
        initViewBinding()
        initToolbar()
        playAnimation()
        onLoginPressed()
        onMoveToRegisterActivity()
    }

    private fun initSessionManager(){
        sessionManager = SessionManager(this)
    }

    private fun initViewBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initToolbar() {
        supportActionBar?.hide()
    }

    @SuppressLint("Recycle")
    private fun playAnimation(){
        binding.apply {
            val illustration = loginIllustration.fadeIn()
            val title = tvLoginTitle.fadeIn()
            val subtitle = tvLoginSubtitle.fadeIn()
            val email = emailEditTextLayout.fadeIn()
            val password = passwordEditTextLayout.fadeIn()
            val button = loginButton.fadeIn()
            val registerContainer = registerContainer.fadeIn()

            AnimatorSet().apply {
                playSequentially(illustration, title, subtitle, email, password, button, registerContainer)
                start()
            }
        }
    }

    private fun onLoginPressed() {
        binding.apply {
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable?) {}

                @Suppress("DEPRECATION")
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val (_, isValid) = isPasswordValid(this@LoginActivity, s.toString())
                    passwordEditTextLayout.isPasswordVisibilityToggleEnabled = isValid
                }
            })

            loginButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val (_, isEmailValid) = isEmailValid(this@LoginActivity, email)
                val (_, isPasswordValid) = isPasswordValid(this@LoginActivity, password)
                if (email.isNotEmpty() && password.isNotEmpty() && isEmailValid && isPasswordValid) {
                    initObserver(email, password)
                }
            }
        }
    }

    private fun initObserver(email: String, password: String) {
        loginViewModel.login(email, password).observe(this) { response ->
            when (response) {
                is State.Loading -> {
                    binding.apply {
                        loginLoading.visibility = View.VISIBLE
                        loginButton.visibility = View.INVISIBLE
                    }
                }
                is State.Success -> {
                    binding.apply {
                        loginLoading.visibility = View.INVISIBLE
                        loginButton.visibility = View.VISIBLE
                        emailEditText.text?.clear()
                        passwordEditText.text?.clear()
                    }
                    sessionManager.saveToken(response.data.loginResult?.token.toString())
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    onMoveToMainActivity()
                }
                is State.Error -> {
                    binding.apply {
                        loginLoading.visibility = View.INVISIBLE
                        loginButton.visibility = View.VISIBLE
                    }
                    Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onMoveToMainActivity() {
        val toMain = Intent(this, MainActivity::class.java)
        toMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(toMain)
    }

    private fun onMoveToRegisterActivity() {
        binding.tvRegister.setOnClickListener {
            val toRegister = Intent(this, RegisterActivity::class.java)
            startActivity(toRegister)
        }
    }
}