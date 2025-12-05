package com.example.androidlearning.base

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    companion object{
        var instance = App()
            private set
    }
}