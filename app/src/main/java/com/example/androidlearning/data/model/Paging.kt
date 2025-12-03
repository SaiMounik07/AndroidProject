package com.example.androidlearning.data.model

import com.google.gson.annotations.SerializedName

data class Paging(
    @SerializedName("page")
    val page: Int = 1,
    
    @SerializedName("total_page")
    val totalPage: Int = 1,
    
    @SerializedName("item_per_page")
    val itemPerPage: Int = 10,
    
    @SerializedName("total_item")
    val totalItem: Int = 0
)
