package com.example.androidlearning.data.model.auth

data class BaseResponse(  val code: Int,
                          val error: Any,
                          val result: String,
                          val success: Boolean,
                          val value: Any
)