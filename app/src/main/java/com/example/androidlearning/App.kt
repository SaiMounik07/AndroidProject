package com.example.androidlearning

import android.app.Application
class App: Application() {
    companion object{
        var instance=App()
            private set
    }

    init {
        instance=this
    }
}