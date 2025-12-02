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
    lateinit var constraintViewModel: ConstraintViewModel

    var loginClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_constraint)
        val layout=findViewById<ConstraintLayout>(R.id.login_page)
        constraintViewModel = ViewModelProvider(this)[ConstraintViewModel::class.java]
        constraintViewModel.addUser("sai","sai")
        constraintViewModel.addUser("user","user")
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
                        Snackbar.make(layout,"login success",Snackbar.LENGTH_INDEFINITE).show()
                        startActivity(Intent(this, HomeActivity::class.java))

                    },
                    onFailure = {
                        Toast.makeText(this, ENTER_USERNAME, Toast.LENGTH_SHORT).show()
                    })

            }
        }
    }

}