package com.example.androidlearning.data.repository

import com.example.androidlearning.util.SharedPreferenceUtility

class MainRepository {
    private val sharedPreferences= SharedPreferenceUtility("login")

    fun saveValueByKey(key:String,value:String){
        sharedPreferences.saveValue(key,value)
    }

    fun getValueByKey(key:String,value:String):String?{
        return sharedPreferences.getValue(key,value)
    }
    fun clearData(){
        sharedPreferences.clearData()
    }


}