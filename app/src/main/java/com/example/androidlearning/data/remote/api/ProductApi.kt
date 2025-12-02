package com.example.androidlearning.data.remote.api

import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.remote.interceptor.ProvideMandatoryParams
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Tag

interface ProductApi {
    @GET("/backend/search/products")
    suspend fun getProducts(
        @Tag mandatoryParam: ProvideMandatoryParams = ProvideMandatoryParams.Enable,
        @QueryMap queryParams: Map<String, Any>
    ): Response<List<Product>>
}
