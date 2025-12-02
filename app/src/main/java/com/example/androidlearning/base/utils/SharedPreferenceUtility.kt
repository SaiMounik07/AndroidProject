package com.example.androidlearning.base.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.androidlearning.base.App
import com.example.androidlearning.base.constants.Constants.PREFS_FILE_LOGIN
import com.example.androidlearning.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class SharedPreferenceUtility @Inject constructor(private val sharedPreference: SharedPreferences) {
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
    fun save(key:String,product:Product){
        val gson = Gson()
        val json = sharedPreference.getString(key, null)
        val type = object : TypeToken<MutableList<Product>>() {}.type
        val currentList: MutableList<Product> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
        currentList.add(product)
        val updatedJson = gson.toJson(currentList)
        edit.putString(key, updatedJson).apply()
    }
    fun getProducts(key: String): List<Product> {
        val gson=Gson()
        val json=sharedPreference.getString(key,null)
        val type=object :TypeToken<List<Product>>(){}.type
        return gson.fromJson(json,type)?: emptyList()
    }
    fun deleteProduct(key:String,product:Product){
        val productList=getProducts(key).toMutableList()
        productList.remove(product)
        val gson=Gson()
        val json=gson.toJson(productList)
        edit.putString(key,json).apply()
    }
}