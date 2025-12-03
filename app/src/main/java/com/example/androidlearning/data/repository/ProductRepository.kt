package com.example.androidlearning.data.repository

import com.example.androidlearning.data.di.NetworkModule
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.model.ProductResponseData
import com.example.androidlearning.data.remote.api.ProductApi
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor() {
    @Inject
    lateinit var productApi: ProductApi

    suspend fun getProducts(
                     searchTerm: String?,
                     showFacets: Boolean = true,
                     channelId: String,
                     start: Int = 0,
                     page: Int = 0,
                     itemPerPage: Int = 10
    ): ProductResponseData {
        val queryParams = mapOf<String, Any>(
            "searchTerm" to (searchTerm ?: ""),
            "showFacets" to showFacets,
            "channelId" to channelId,
            "start" to start,
            "page" to page,
            "itemPerPage" to itemPerPage

        )
        return productApi.getProducts(queryParams = queryParams)
    }

}