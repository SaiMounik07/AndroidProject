package com.example.androidlearning.data.model

import com.google.gson.annotations.SerializedName

data class Badge(
    @SerializedName("logisticBadge_stock")
    val logisticBadge_stock: String = "",
    
    @SerializedName("merchantBadgeUrl")
    val merchantBadgeUrl: String = "",
    
    @SerializedName("merchantBadge")
    val merchantBadge: String = "",
    
    @SerializedName("logisticBadge")
    val logisticBadge: String = ""
)