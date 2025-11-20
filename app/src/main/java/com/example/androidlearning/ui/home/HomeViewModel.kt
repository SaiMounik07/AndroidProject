package com.example.androidlearning.ui.home

import androidx.lifecycle.ViewModel
import com.example.androidlearning.data.repository.MainRepository

class HomeViewModel: ViewModel() {
    val mainRepository= MainRepository()
    fun getValueByKey(key: String, value: String): String? {
        return mainRepository.getValueByKey(key, value)
    }
    fun clearData(){
        mainRepository.clearData()
    }

}