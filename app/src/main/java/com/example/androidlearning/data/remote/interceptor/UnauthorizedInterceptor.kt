package com.example.androidlearning.data.remote.interceptor

import android.util.Log
import com.example.androidlearning.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UnauthorizedInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() == 401 || response.code() == 403) {
            Log.e("UnauthorizedInterceptor", "Session expired: ${response.code()}")
            sessionManager.handleUnauthorized()
        }
        
        return response
    }
}
