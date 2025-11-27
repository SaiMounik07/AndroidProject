package com.example.androidlearning.data.repository

import com.example.androidlearning.base.constants.Constants.PREFS_FILE_LOGIN
import com.example.androidlearning.base.utils.SharedPreferenceUtility
import com.example.androidlearning.data.model.Product

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
    fun saveProduct(key:String,product: Product){
        sharedPreferences.save(key,product)
    }
    fun getProducts(key:String):List<Product> {
        return sharedPreferences.getProducts(key)
    }



}