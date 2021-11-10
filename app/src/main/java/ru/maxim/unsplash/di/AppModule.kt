package ru.maxim.unsplash.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

@JvmField
val appModule = module {

    // Application scope
    factory { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
}