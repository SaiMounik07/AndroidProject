package com.example.androidlearning.data.model

data class Price(
    val priceDisplay: String,
                 val strikeThroughPriceDisplay: String,
                 val discount: Int,
                 val discountPrice: Double,
                 val minPrice: Double,
                 val offerPriceDisplay: String,
                 val isPriceRange: Boolean,
                 val listPrice: Double,
                 val salePrice: Double
)