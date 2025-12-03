package com.example.androidlearning.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidlearning.data.model.auth.Login
import com.example.androidlearning.data.model.auth.Value
import com.example.androidlearning.data.repository.AuthRepository
import com.example.androidlearning.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConstraintViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState
    fun saveValues(key: String, value: String) {
        mainRepository.saveValueByKey(key, value)
    }

    fun getValueByKey(key: String, value: String): String? {
        return mainRepository.getValueByKey(key, value)
    }

    fun login(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Please enter username and password")
            return
        }
        
        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            val result = authRepository.login(username, password)
            
            _loginState.value = if (result.isSuccess) {
                val authResponse = result.getOrNull()
                Log.i("ConstraintViewModel", "Login successful: ${authResponse.toString()}")
                LoginState.Success(authResponse)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Login failed"
                Log.e("ConstraintViewModel", "Login failed: $error")
                LoginState.Error(error)
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    
    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val authResponse: Value?) : LoginState()
    data class Error(val message: String) : LoginState()
}