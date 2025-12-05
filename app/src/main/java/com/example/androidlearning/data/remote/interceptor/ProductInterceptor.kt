package com.example.androidlearning.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class ProductInterceptor @Inject constructor(): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var mandatoryParams = ProvideMandatoryParams.Enable

        try {
            request.tag(Invocation::class.java)?.let { invocation ->
                val args = invocation.arguments()
                if (args.isNotEmpty() && args[1] is ProvideMandatoryParams) {
                    mandatoryParams = args[1] as ProvideMandatoryParams
                }
            }
        } catch (e: Exception) {
            Log.w("ProductInterceptor", "Could not get tag: ${e.message}")
        }
        
        val newRequest = if (mandatoryParams == ProvideMandatoryParams.Enable) {
            request.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
enum class ProvideMandatoryParams {
    Enable, Disable
}