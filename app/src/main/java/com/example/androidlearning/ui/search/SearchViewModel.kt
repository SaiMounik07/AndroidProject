package com.example.androidlearning.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidlearning.base.constants.Constants.GUEST
import com.example.androidlearning.base.constants.Constants.ITEMS_PER_PAGE
import com.example.androidlearning.base.constants.Constants.JSON_NAME
import com.example.androidlearning.base.constants.Constants.MIN_SEARCH_LENGTH
import com.example.androidlearning.base.constants.Constants.USERNAME
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.model.ProductResponseData
import com.example.androidlearning.data.repository.AuthRepository
import com.example.androidlearning.data.repository.MainRepository
import com.example.androidlearning.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    
    @Inject
    lateinit var productRepository: ProductRepository
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    // LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _isPaginating = MutableLiveData<Boolean>()
    val isPaginating: LiveData<Boolean> = _isPaginating
    
    private val _searchResults = MutableLiveData<List<Product>>()
    val searchResults: LiveData<List<Product>> = _searchResults
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Pagination state
    private var currentSearchTerm: String = ""
    private var currentPage: Int = 1
    private var hasMorePages: Boolean = true

    fun searchProducts(query: String) {
        if (query.length < MIN_SEARCH_LENGTH) {
            clearSearch()
            return
        }
        
        currentSearchTerm = query
        currentPage = 1
        hasMorePages = true
        loadProducts(isInitialSearch = true)
    }
    
    fun loadNextPage() {
        if (_isLoading.value == true || !hasMorePages || currentSearchTerm.isEmpty()) return
        
        currentPage++
        loadProducts(isInitialSearch = false)
    }
    
    private fun loadProducts(isInitialSearch: Boolean) {
        if (isInitialSearch) {
            _isLoading.value = true
        } else {
            _isPaginating.value = true
        }
        _error.value = null
        
        viewModelScope.launch {
            try {
                val response = productRepository.getProducts(
                    searchTerm = currentSearchTerm,
                    showFacets = false,
                    channelId = "mobile-web",
                    page = currentPage,
                    itemPerPage = ITEMS_PER_PAGE,
                    start = (currentPage - 1) * ITEMS_PER_PAGE
                )
                
                if (response.code == 200) {
                    handleSuccessResponse(response, isInitialSearch)
                } else {
                    handleErrorResponse(isInitialSearch)
                }
            } catch (e: Exception) {
                handleException(e, isInitialSearch)
            }
        }
    }
    
    private fun handleSuccessResponse(response: ProductResponseData, isInitialSearch: Boolean) {
        val products = response.data.products
        val paging = response.data.paging
        
        hasMorePages = paging?.let { currentPage < it.totalPage } ?: (products.size >= ITEMS_PER_PAGE)
        
        _searchResults.value = if (isInitialSearch) {
            products
        } else {
            (_searchResults.value ?: emptyList()) + products
        }
        
        if (isInitialSearch) {
            _isLoading.value = false
        } else {
            _isPaginating.value = false
        }

    }
    
    private fun handleErrorResponse(isInitialSearch: Boolean) {
        _error.value = if (isInitialSearch) "Search failed" else "Failed to load more"
        
        if (isInitialSearch) {
            _isLoading.value = false
        } else {
            _isPaginating.value = false
            currentPage--
        }
    }
    
    private fun handleException(e: Exception, isInitialSearch: Boolean) {
        _error.value = "Network error: ${e.message}"
        
        if (isInitialSearch) {
            _isLoading.value = false
        } else {
            _isPaginating.value = false
            currentPage--
        }
        
        Log.e("API_EXCEPTION", "Exception on page $currentPage: ${e.message}", e)
    }
    
    fun hasMorePages(): Boolean = hasMorePages
    
    fun getCurrentSearchTerm(): String = currentSearchTerm
    
    fun checkSession() {
        viewModelScope.launch {
            authRepository.validateSession()
            // If session invalid, UnauthorizedInterceptor will handle it
        }
    }
    
    fun clearSearch() {
        currentSearchTerm = ""
        currentPage = 1
        hasMorePages = true
        _searchResults.value = emptyList()
        _error.value = null
    }
    
    fun deleteProduct(product: Product) {
        val username = mainRepository.getValueByKey(USERNAME, GUEST)
        mainRepository.deleteProduct(username.toString(), product)
        
        // Remove from current results
        _searchResults.value = _searchResults.value?.filter { it.name != product.name }
    }

}