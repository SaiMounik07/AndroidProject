package com.example.androidlearning.data.local

import com.example.androidlearning.base.constants.Constants.KEY_JWT_TOKEN
import com.example.androidlearning.base.constants.Constants.KEY_USERNAME
import com.example.androidlearning.base.utils.SharedPreferenceUtility
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val sharedPreferences: SharedPreferenceUtility
) {

    fun saveToken(token: String) {
        sharedPreferences.saveValue(KEY_JWT_TOKEN, token)
    }

    fun getToken(): String? {
        return sharedPreferences.getValue(KEY_JWT_TOKEN, "")?.takeIf { it.isNotEmpty() }
    }

    fun saveUsername(username: String) {
        sharedPreferences.saveValue(KEY_USERNAME, username)
    }

    fun getUsername(): String? {
        return sharedPreferences.getValue(KEY_USERNAME, "")?.takeIf { it.isNotEmpty() }
    }

    fun clearToken() {
        sharedPreferences.clearSpecificData(KEY_JWT_TOKEN)
        sharedPreferences.clearSpecificData(KEY_USERNAME)
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}
