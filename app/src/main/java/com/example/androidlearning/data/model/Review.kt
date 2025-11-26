package com.example.androidlearning.data.model

data class Review(
    val rating: Int,
    val count: Int,
    val absoluteRating: Double,
    val sellerRating: Double,
    val isNewSeller: Boolean
)