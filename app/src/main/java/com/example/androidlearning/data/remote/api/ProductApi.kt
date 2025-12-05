package com.example.androidlearning.data.remote.api

import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.model.ProductResponseData
import com.example.androidlearning.data.remote.interceptor.ProvideMandatoryParams
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap
import retrofit2.http.Tag

interface ProductApi {
    @GET("/backend/search/products")
    suspend fun getProducts(
        @Header("User-Agent") userAgent: String = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Mobile Safari/537.36",
        @Tag mandatoryParam: ProvideMandatoryParams = ProvideMandatoryParams.Enable,
        @QueryMap queryParams: Map<String, @JvmSuppressWildcards Any>
    ): ProductResponseData
}
