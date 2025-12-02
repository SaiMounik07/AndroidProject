package com.example.androidlearning.ui.login

import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.androidlearning.base.constants.Constants.PASSWORD
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ConstraintViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
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
        if (name.isEmpty() || password.isEmpty()) {
            onFailure.invoke()
            return
        }
        
        val usersJson = mainRepository.getValueByKey("USERS", "[]")
        val usersArray = JSONArray(usersJson)
        
        var isValid = false

        for (i in 0 until usersArray.length()) {
            val user = usersArray.getJSONObject(i)
            val storedUsername = user.getString("username")
            val storedPassword = user.getString("password")
            
            if (name == storedUsername && password == storedPassword) {
                isValid = true
                mainRepository.saveValueByKey(USERNAME, name)
                break
            }
        }
        
        if (isValid) {
            onSuccess.invoke()
        } else {
            onFailure.invoke()
        }
    }
    fun addUser(username: String, password: String) {
        val existing = mainRepository.getValueByKey("USERS", "[]")
        val arr = JSONArray(existing)

        val user = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        arr.put(user)

        mainRepository.saveValueByKey("USERS", arr.toString())
    }

}