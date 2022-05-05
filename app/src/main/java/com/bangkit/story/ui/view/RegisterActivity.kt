package com.bangkit.story.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.story.databinding.ActivityRegisterBinding
import com.bangkit.story.ui.viewmodel.RegisterViewModel
import com.bangkit.story.ui.viewmodel.ViewModelFactory
import com.bangkit.story.utils.State
import com.bangkit.story.utils.isEmailValid
import com.bangkit.story.utils.isPasswordValid

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        initToolbar()
        onRegisterPressed()
    }

    private fun initViewBinding() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initToolbar() {
        supportActionBar?.hide()
    }

    private fun onRegisterPressed() {
        binding.apply {
            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable?) {}

                @Suppress("DEPRECATION")
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val (_, isValid) = isPasswordValid(this@RegisterActivity, s.toString())
                    passwordEditTextLayout.isPasswordVisibilityToggleEnabled = isValid
                }
            })

            registerButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val (_, isEmailValid) = isEmailValid(this@RegisterActivity, email)
                val (_, isPasswordValid) = isPasswordValid(this@RegisterActivity, password)
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && isEmailValid && isPasswordValid)
                    initObserver(name, email, password)
            }
        }
    }

    private fun initObserver(name: String, email: String, password: String) {
        registerViewModel.register(name, email, password).observe(this) { response ->
            when (response) {
                is State.Loading -> {
                    binding.apply {
                        registerLoading.visibility = View.VISIBLE
                        registerButton.visibility = View.INVISIBLE
                    }
                }
                is State.Success -> {
                    binding.apply {
                        registerLoading.visibility = View.INVISIBLE
                        registerButton.visibility = View.VISIBLE
                        nameEditText.text?.clear()
                        emailEditText.text?.clear()
                        passwordEditText.text?.clear()
                    }
                    Toast.makeText(this, response.data.message, Toast.LENGTH_SHORT).show()
                    onMoveToLoginActivity()
                }
                is State.Error -> {
                    binding.apply {
                        registerLoading.visibility = View.INVISIBLE
                        registerButton.visibility = View.VISIBLE
                    }
                    Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onMoveToLoginActivity() {
        val toLogin = Intent(this, LoginActivity::class.java)
        startActivity(toLogin)
        finish()
    }
}