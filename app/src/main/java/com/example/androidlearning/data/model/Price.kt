package com.example.androidlearning.data.model

import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("priceDisplay")
    val priceDisplay: String = "",
    
    @SerializedName("strikeThroughPriceDisplay")
    val strikeThroughPriceDisplay: String = "",
    
    @SerializedName("discount")
    val discount: Int = 0,
    
    @SerializedName("discountPrice")
    val discountPrice: Double = 0.0,
    
    @SerializedName("minPrice")
    val minPrice: Double = 0.0,
    
    @SerializedName("offerPriceDisplay")
    val offerPriceDisplay: String = "",
    
    @SerializedName("isPriceRange")
    val isPriceRange: Boolean = false,
    
    @SerializedName("listPrice")
    val listPrice: Double = 0.0,
    
    @SerializedName("salePrice")
    val salePrice: Double = 0.0
)