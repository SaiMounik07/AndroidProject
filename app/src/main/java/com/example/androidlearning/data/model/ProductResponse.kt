package com.example.androidlearning.data.model


data class ProductResponse(
    val products: List<Product>,
    val paging: Paging?
)

data class ProductResponseData(
    val data: ProductResponse,
    val code: Int,
    val status : String
)
