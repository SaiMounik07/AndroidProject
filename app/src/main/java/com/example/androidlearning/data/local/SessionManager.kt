package com.example.androidlearning.data.local

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.androidlearning.ui.login.ConstraintLoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager
) {

    
    fun handleUnauthorized() {
        tokenManager.clearToken()
        forceLogout()
    }
    
    private fun forceLogout() {
        val intent = Intent(context, ConstraintLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("SESSION_EXPIRED", true)
        }
        context.startActivity(intent)
    }

}
