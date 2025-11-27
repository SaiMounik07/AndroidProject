package com.example.androidlearning.ui.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.ITEMS_PER_PAGE
import com.example.androidlearning.base.constants.Constants.JSON_NAME
import com.example.androidlearning.base.constants.Constants.MIN_SEARCH_LENGTH
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.base.utils.KeyboardUtils
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.repository.MainRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchViewModel: ViewModel() {
    private var allProducts: List<Product> = emptyList()
    private var currentSourceList: List<Product> = emptyList()
    private var loadedCount: Int = 0
    val mainRepository: MainRepository= MainRepository()
    fun loadProductsFromJson(context: Context,flag:Boolean) {
        if (flag){
            allProducts = getProducts()
            currentSourceList = getProducts()
        }else {
            val json = context.assets.open(JSON_NAME)
                .bufferedReader()
                .use { it.readText() }
            val gson = Gson()
            val type = object : TypeToken<List<Product>>() {}.type
            val products: List<Product> = gson.fromJson(json, type)
            allProducts = products
            currentSourceList = products
        }
    }
    
    fun filterProducts(query: String): List<Product> {
        return if (query.length < MIN_SEARCH_LENGTH) {
            allProducts
        } else {
            allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true)
            }
        }
    }
    
    fun updateSourceList(filteredList: List<Product>) {
        currentSourceList = filteredList
        loadedCount = 0
    }
    
    fun resetToAllProducts() {
        currentSourceList = allProducts
        loadedCount = 0
    }
    
    fun getNextPageItems(): List<Product> {
        val endIndex = (loadedCount + ITEMS_PER_PAGE).coerceAtMost(currentSourceList.size)
        
        return if (loadedCount < endIndex) {
            val items = currentSourceList.subList(loadedCount, endIndex)
            loadedCount = endIndex
            items
        } else {
            emptyList()
        }
    }
    
    fun hasMoreItems(): Boolean {
        return loadedCount < currentSourceList.size
    }
    
    fun shouldShowNoResults(filteredList: List<Product>, query: String): Boolean {
        return query.length >= MIN_SEARCH_LENGTH && filteredList.isEmpty()
    }
    fun getProducts(): List<Product>{
        val username=mainRepository.getValueByKey(USERNAME, GUEST)
        return mainRepository.getProducts(username.toString())
    }

}