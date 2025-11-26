package com.example.androidlearning.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.androidlearning.base.constants.Constants.JSON_NAME
import com.example.androidlearning.base.utils.KeyboardUtils
import com.example.androidlearning.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchViewModel: ViewModel() {
    var keyboardUtils= KeyboardUtils()

    fun loadProductsFromJson(context: Context): List<Product>{
        val json = context.assets.open(JSON_NAME)
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Product>>() {}.type
       return gson.fromJson(json, type)
    }
    fun filteredProducts(allProducts:List<Product>,query:String): List<Product> {
        return allProducts.filter { i ->
            i.name.lowercase().contains(query,ignoreCase = false)
        }
    }

}