package com.tontowi0086.assesment3

import android.app.Application
import android.content.Context

class Assesment3 : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: Assesment3
        val appContext: Context
            get() = instance.applicationContext
    }
}