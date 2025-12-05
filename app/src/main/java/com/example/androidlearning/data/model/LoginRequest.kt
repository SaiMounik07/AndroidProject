package com.example.androidlearning.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val username: String,
    
    @SerializedName("password")
    val password: String
)
