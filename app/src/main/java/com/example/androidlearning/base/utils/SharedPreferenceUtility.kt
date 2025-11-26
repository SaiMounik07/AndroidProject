package com.example.androidlearning.base.utils

import android.content.Context
import com.example.androidlearning.App

class SharedPreferenceUtility(name:String) {
    private val sharedPreference= App.instance.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val edit=sharedPreference.edit()


    fun saveValue(key:String,value:String){
        edit.putString(key,value).apply()
    }
    fun getValue(key:String,value:String):String?{
        return sharedPreference.getString(key,value)
    }
    fun clearData(){
        edit.clear().apply()
    }
    fun clearSpecificData(key:String){
        edit.remove(key).apply()
    }

}