package com.example.androidlearning.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.KEY_USERNAME
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.repository.AuthRepository
import com.example.androidlearning.data.repository.MainRepository
import com.example.androidlearning.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _sessionValid = MutableLiveData<Boolean>()
    val sessionValid: LiveData<Boolean> = _sessionValid

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isPaginating = MutableLiveData<Boolean>()
    val isPaginating: LiveData<Boolean> = _isPaginating

    private val _productDeleted = MutableLiveData<Product>()
    val productDeleted: LiveData<Product> = _productDeleted
    
    private var allProducts: MutableList<Product> = mutableListOf()
    private var currentPage = 0
    private val pageSize = 5
    
    fun getValueByKey(key: String, value: String): String? {
        return mainRepository.getValueByKey(key, value)
    }

    fun deleteProduct(product: Product) {
        val username = mainRepository.getValueByKey(KEY_USERNAME, GUEST)
        viewModelScope.launch {
            productRepository.deleteProductForUser(username.toString(), product)
            allProducts.remove(product)
            _productDeleted.value = product
        }
    }

    
    fun checkSession() {
        viewModelScope.launch {
            val result = authRepository.validateSession()
            _sessionValid.value = result.isSuccess
        }
    }
    fun clearCart(){
        viewModelScope.launch {
            val username = mainRepository.getValueByKey(KEY_USERNAME, GUEST)
            productRepository.deleteProduct(username.toString())

        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    fun loadInitialProducts() {
        viewModelScope.launch {
            val userId = mainRepository.getValueByKey(KEY_USERNAME, GUEST)
            allProducts = productRepository.getProductsByUser(userId.toString()).toMutableList()
            currentPage = 0
            loadNextPage()
        }
    }
    
    fun loadNextPage() {
        if (_isPaginating.value == true) return
        val startIndex = currentPage * pageSize
        if (startIndex >= allProducts.size) {
            return
        }
        _isPaginating.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            val endIndex = minOf(startIndex + pageSize, allProducts.size)
            val pageProducts = allProducts.subList(startIndex, endIndex)
            _products.value = pageProducts
            currentPage++
            _isPaginating.value = false
        }
    }
    
    fun hasMoreProducts(): Boolean {
        return currentPage * pageSize < allProducts.size
    }
}