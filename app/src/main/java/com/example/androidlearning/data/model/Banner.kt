package com.example.androidlearning.data.model

data class Banner(
    val id: Int,
    val imageUrl: String,
    val title: String = "",
    val subtitle: String = "",
    val actionUrl: String = ""
)
