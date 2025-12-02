package com.example.androidlearning.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class ProductInterceptor : Interceptor{
    private var mandatoryParams: ProvideMandatoryParams= ProvideMandatoryParams.Enable
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        request.tag(Invocation::class.java)?.let {
            mandatoryParams = it.arguments()[0] as ProvideMandatoryParams
        }
            val response=if (mandatoryParams == ProvideMandatoryParams.Enable){
                request
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
            }else{
                request.newBuilder().build()
            }
        return chain.proceed(response)
    }
}
enum class ProvideMandatoryParams {
    Enable, Disable
}