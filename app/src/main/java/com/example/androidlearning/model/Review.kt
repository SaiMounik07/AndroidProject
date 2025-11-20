package com.example.androidlearning.model

data class Review(
    val rating: Int,
    val count: Int,
    val absoluteRating: Double,
    val sellerRating: Double,
    val isNewSeller: Boolean
)