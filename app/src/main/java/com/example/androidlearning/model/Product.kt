package com.example.androidlearning.model

data class Product(
    val name: String,
    val price: Price,
    val brand: String,
    val review: Review,
    val tags: List<String>,
    val location: String,
    val badge: Badge,
    val soldCountTotal: Int,
    val uspLabelsTags: List<String>,
    val images: List<String>
)
