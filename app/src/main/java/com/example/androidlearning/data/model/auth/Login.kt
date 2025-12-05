package com.example.androidlearning.data.model.auth

data class Login(
    val code: Int,
    val error: Any,
    val result: String,
    val success: Boolean,
    val value: Value
)