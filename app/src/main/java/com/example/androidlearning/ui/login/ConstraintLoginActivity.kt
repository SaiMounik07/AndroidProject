package com.example.androidlearning.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.ENTER_USERNAME
import com.example.androidlearning.ui.home.HomeActivity
import com.example.androidlearning.ui.search.SearchActivity
import com.google.android.material.button.MaterialButton

class ConstraintLoginActivity : AppCompatActivity() {
    lateinit var constraintViewModel: ConstraintViewModel

    var loginClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_constraint)
        constraintViewModel = ViewModelProvider(this)[ConstraintViewModel::class.java]
        constraintViewModel.saveValues("username", "sai")
        constraintViewModel.saveValues("password", "sai")
        Toast.makeText(this, "Constraint Layout", Toast.LENGTH_SHORT).show()
        val username = findViewById<EditText>(R.id.userfield)
        val password = findViewById<EditText>(R.id.password_field)
        val loginButton = findViewById<MaterialButton>(R.id.loginbutton)
        username.requestFocus()


        loginButton.setOnClickListener {
            loginClickCount++
            if (loginClickCount >= 1 && !username.text.toString().isEmpty()&& password.text.toString().isEmpty()) {
                password.visibility = EditText.VISIBLE
                username.isEnabled = false
                password.requestFocus()
            } else {
                constraintViewModel.validateLogin(
                    name = username.text.toString(),
                    password = password.text.toString(),
                    onSuccess = {
                        startActivity(Intent(this, HomeActivity::class.java))
                    },
                    onFailure = {
                        Toast.makeText(this, ENTER_USERNAME, Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }

}