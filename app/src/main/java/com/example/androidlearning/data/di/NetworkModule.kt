package com.example.androidlearning.data.di

import com.example.androidlearning.data.remote.api.ProductApi
import com.example.androidlearning.data.remote.interceptor.ProductInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule{

    private val PRODCT_BASE_URL="https://www.blibli.com/"
    @Provides
    fun provideProductRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PRODCT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(
        productInterceptor: ProductInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(productInterceptor)
            .build()
    }

}