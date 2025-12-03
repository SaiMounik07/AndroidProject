package com.example.androidlearning.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.ENTER_USERNAME
import com.example.androidlearning.ui.home.HomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConstraintLoginActivity : AppCompatActivity() {
    private lateinit var constraintViewModel: ConstraintViewModel
    private lateinit var layout: ConstraintLayout
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: MaterialButton
    
    private var loginClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_constraint)
        
        constraintViewModel = ViewModelProvider(this)[ConstraintViewModel::class.java]
        
        // Check if session expired
        val sessionExpired = intent.getBooleanExtra("SESSION_EXPIRED", false)
        if (sessionExpired) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show()
        }
        
        // Check if already logged in (and not expired)
        if (!sessionExpired && constraintViewModel.isLoggedIn()) {
            navigateToHome()
            return
        }
        
        initViews()
        observeViewModel()
        setupClickListeners()
    }
    
    private fun initViews() {
        layout = findViewById(R.id.login_page)
        username = findViewById(R.id.userfield)
        password = findViewById(R.id.password_field)
        loginButton = findViewById(R.id.loginbutton)
        username.requestFocus()
    }
    
    private fun observeViewModel() {
        constraintViewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    loginButton.isEnabled = false
                    loginButton.text = "Logging in..."
                }
                is LoginState.Success -> {
                    loginButton.isEnabled = true
                    loginButton.text = "Login"
                    Snackbar.make(layout, "Login successful!", Snackbar.LENGTH_SHORT).show()
                    navigateToHome()
                }
                is LoginState.Error -> {
                    loginButton.isEnabled = true
                    loginButton.text = "Login"
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            loginClickCount++
            
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()
            
            if (loginClickCount >= 1 && usernameText.isNotEmpty() && passwordText.isEmpty()) {
                password.visibility = EditText.VISIBLE
                username.isEnabled = false
                password.requestFocus()
            } else {
                constraintViewModel.login(usernameText, passwordText)
            }
        }
    }
    
    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}