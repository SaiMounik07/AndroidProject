package com.example.androidlearning.ui.home

import androidx.lifecycle.ViewModel
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.repository.MainRepository

class HomeViewModel: ViewModel() {
    val mainRepository= MainRepository()
    fun getValueByKey(key: String, value: String): String? {
        return mainRepository.getValueByKey(key, value)
    }
    fun clearData(){
        mainRepository.clearData()
    }
    fun logout() {
        mainRepository.clearSpecificData("CURRENT_USER")
    }

}