package ru.maxim.unsplash

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.maxim.unsplash.di.*
import timber.log.Timber

class UnsplashApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(applicationContext)
            modules(listOf(
                appModule,
                dataModule,
                networkModule,
                persistenceModule,
                presentationModule
            ))
        }
    }
}