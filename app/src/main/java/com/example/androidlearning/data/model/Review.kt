package com.example.androidlearning.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("rating")
    val rating: Int = 0,
    
    @SerializedName("count")
    val count: Int = 0,
    
    @SerializedName("absoluteRating")
    val absoluteRating: Double = 0.0,
    
    @SerializedName("sellerRating")
    val sellerRating: Double = 0.0,
    
    @SerializedName("isNewSeller")
    val isNewSeller: Boolean = false
)