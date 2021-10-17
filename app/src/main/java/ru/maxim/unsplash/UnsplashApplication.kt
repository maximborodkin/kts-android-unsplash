package ru.maxim.unsplash

import android.app.Application
import kotlinx.coroutines.*
import ru.maxim.unsplash.repository.local.PreferencesManager
import ru.maxim.unsplash.util.NetworkUtils
import timber.log.Timber

class UnsplashApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        NetworkUtils.init(this, applicationScope)
        PreferencesManager.init(applicationContext)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("onLowMemory called in application class")
    }
}