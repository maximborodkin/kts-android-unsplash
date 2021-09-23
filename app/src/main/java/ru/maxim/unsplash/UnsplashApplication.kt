package ru.maxim.unsplash

import android.app.Application
import timber.log.Timber

class UnsplashApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}