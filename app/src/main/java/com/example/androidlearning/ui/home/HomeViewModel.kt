package com.example.androidlearning.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidlearning.data.repository.AuthRepository
import com.example.androidlearning.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _sessionValid = MutableLiveData<Boolean>()
    val sessionValid: LiveData<Boolean> = _sessionValid
    
    fun getValueByKey(key: String, value: String): String? {
        return mainRepository.getValueByKey(key, value)
    }
    
    fun clearData() {
        mainRepository.clearData()
    }
    
    fun checkSession() {
        viewModelScope.launch {
            val result = authRepository.validateSession()
            _sessionValid.value = result.isSuccess
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}