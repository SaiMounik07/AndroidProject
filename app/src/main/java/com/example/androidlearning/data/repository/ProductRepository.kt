package com.example.androidlearning.data.repository

import com.example.androidlearning.data.di.NetworkModule
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.remote.api.ProductApi
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor() {
    @Inject
    lateinit var productApi: ProductApi

    suspend fun getProducts(
                     multiCategory: String?,
                     searchTerm: String?,
                     showFacets: Boolean = true,
                     channelId: String,
                     start: Int = 0,
                     page: Int = 0): Response<List<Product>>{
        val queryParams = mapOf<String, Any>(
            "multiCategory" to (multiCategory ?: ""),
            "searchTerm" to (searchTerm ?: ""),
            "showFacets" to showFacets,
            "channelId" to channelId,
            "start" to start,
            "page" to page
        )
        return productApi.getProducts(queryParams = queryParams)
    }



}