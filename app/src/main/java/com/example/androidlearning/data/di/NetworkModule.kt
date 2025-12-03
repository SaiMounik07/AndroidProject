package com.example.androidlearning.data.di

import com.example.androidlearning.data.local.SessionManager
import com.example.androidlearning.data.local.TokenManager
import com.example.androidlearning.data.remote.api.AuthApi
import com.example.androidlearning.data.remote.api.ProductApi
import com.example.androidlearning.data.remote.interceptor.AuthInterceptor
import com.example.androidlearning.data.remote.interceptor.ProductInterceptor
import com.example.androidlearning.data.remote.interceptor.UnauthorizedInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val PRODUCT_BASE_URL = "https://www.blibli.com/"
    private const val AUTH_BASE_URL = "http://10.30.1.82:8086/"

    
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }
    
    @Provides
    @Singleton
    fun provideUnauthorizedInterceptor(sessionManager: SessionManager): UnauthorizedInterceptor {
        return UnauthorizedInterceptor(sessionManager)
    }
    
    @Provides
    @Singleton
    @ProductRetrofit
    fun provideProductOkHttpClient(
        productInterceptor: ProductInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(productInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthOkHttpClient(
        authInterceptor: AuthInterceptor,
        unauthorizedInterceptor: UnauthorizedInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(unauthorizedInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @ProductRetrofit
    fun provideProductRetrofit(@ProductRetrofit okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PRODUCT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(@AuthRetrofit okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideProductApi(@ProductRetrofit retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAuthApi(@AuthRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}