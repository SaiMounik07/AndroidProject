package com.example.androidlearning.data.repository

import com.example.androidlearning.base.constants.Constants.PREFS_FILE_LOGIN
import com.example.androidlearning.base.utils.SharedPreferenceUtility

class MainRepository {
    private val sharedPreferences= SharedPreferenceUtility(PREFS_FILE_LOGIN)

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