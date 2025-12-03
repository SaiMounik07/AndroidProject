package com.example.androidlearning.data.remote.api

import com.example.androidlearning.data.model.LoginRequest
import com.example.androidlearning.data.model.auth.BaseResponse
import com.example.androidlearning.data.model.auth.Login
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("/member/login")
    suspend fun login(@Body request: LoginRequest): Login
    
    @POST("/member/logout")
    suspend fun logout(): BaseResponse

    @GET("/member/session")
    suspend fun session(): BaseResponse
}
