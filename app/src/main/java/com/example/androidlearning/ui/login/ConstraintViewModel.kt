package com.example.androidlearning.ui.login

import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.androidlearning.data.repository.MainRepository

class ConstraintViewModel : ViewModel() {
    val mainRepository = MainRepository()
    fun saveValues(key: String, value: String) {
        mainRepository.saveValueByKey(key, value)
    }

    fun getValueByKey(key: String, value: String): String? {
        return mainRepository.getValueByKey(key, value)
    }

    fun validateLogin(
        name: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val username: String = mainRepository.getValueByKey("username", name).toString()
        val passwordFromRepository: String = mainRepository.getValueByKey("password", password).toString()
        if (!name.isEmpty() && name == username && !password.isEmpty() && password == passwordFromRepository) {
            onSuccess.invoke()
        } else {
            onFailure.invoke()
        }
    }

}