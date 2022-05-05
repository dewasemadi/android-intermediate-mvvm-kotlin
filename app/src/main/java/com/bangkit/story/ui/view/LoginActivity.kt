package com.bangkit.story.ui.view

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.story.R
import com.bangkit.story.data.local.entity.User
import com.bangkit.story.data.local.preferences.SessionManager
import com.bangkit.story.databinding.ActivityLoginBinding
import com.bangkit.story.ui.viewmodel.LoginViewModel
import com.bangkit.story.ui.viewmodel.ViewModelFactory
import com.bangkit.story.utils.*

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
        setFormValue()
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

    private fun setFormValue(){
        val user = intent.getParcelableExtra<User>(EXTRA_USER)

        binding.apply {
            emailEditText.setTextValue(user?.email)
            passwordEditText.setTextValue(user?.password)
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
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(this@LoginActivity, getString(R.string.form_empty), Toast.LENGTH_SHORT).show()
                } else {
                    if (isEmailValid && isPasswordValid) {
                        emailEditTextLayout.clearFocus()
                        passwordEditText.clearFocus()
                        initObserver(email, password)
                    }
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
                    sessionManager.setToken(response.data.loginResult?.token.toString())
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

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}