package com.example.androidlearning.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("name")
    val name: String = "",
    
    @SerializedName("price")
    val price: Price = Price(),
    
    @SerializedName("brand")
    val brand: String = "",
    
    @SerializedName("review")
    val review: Review = Review(),
    
    @SerializedName("tags")
    val tags: List<String> = emptyList(),
    
    @SerializedName("location")
    val location: String = "",
    
    @SerializedName("badge")
    val badge: Badge = Badge(),
    
    @SerializedName("soldCountTotal")
    val soldCountTotal: Int = 0,
    
    @SerializedName("uspLabelsTags")
    val uspLabelsTags: List<String> = emptyList(),
    
    @SerializedName("images")
    val images: List<String> = emptyList()
) {
    val id: String
        get() = name.lowercase()
            .replace(Regex("[^a-z0-9]"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
}
