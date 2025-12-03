package com.example.androidlearning.data.repository

import android.util.Log
import com.example.androidlearning.data.local.TokenManager
import com.example.androidlearning.data.model.LoginRequest
import com.example.androidlearning.data.model.auth.Value
import com.example.androidlearning.data.remote.api.AuthApi
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    
    suspend fun login(username: String, password: String): Result<Value> {
        return try {
            val response = authApi.login(LoginRequest(username, password))

            if (response.code == 200) {
                val authResponse = response.value
                tokenManager.saveToken(authResponse.token)
                tokenManager.saveUsername(username)

                Log.i("AuthRepository", "Login successful for user: $username")
                Result.success(authResponse)
            } else {
                val errorMsg = response.error
                Log.e("AuthRepository", "Login failed: $errorMsg")
                Result.failure(Exception(errorMsg.toString()))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return try {
            val response = authApi.logout()
            tokenManager.clearToken()
            
            if (response.code == 200) {
                Log.i("AuthRepository", "Logout successful")
                Result.success(Unit)
            } else {
                Log.e("AuthRepository", "Logout failed")
                Result.failure(Exception("Logout failed"))
            }
        } catch (e: Exception) {
            tokenManager.clearToken()
            Log.e("AuthRepository", "Logout exception: ${e.message}", e)
            Result.success(Unit)
        }
    }
    
    suspend fun validateSession(): Result<Unit> {
        return try {
            val response = authApi.session()
            
            if (response.code == 200) {
                Log.i("AuthRepository", "Session is valid")
                Result.success(Unit)
            } else {
                Log.e("AuthRepository", "Session invalid: ${response.code}")
                Result.failure(Exception("Session invalid"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Session check failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
    
    fun getUsername(): String? = tokenManager.getUsername()
}
