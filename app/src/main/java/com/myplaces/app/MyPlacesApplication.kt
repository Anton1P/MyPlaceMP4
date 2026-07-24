package com.myplaces.app

import android.app.Application
import com.myplaces.app.di.AppContainer

class MyPlacesApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
