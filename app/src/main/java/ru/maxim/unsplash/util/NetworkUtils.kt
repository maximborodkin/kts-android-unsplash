package ru.maxim.unsplash.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch

object NetworkUtils {
    private lateinit var application: Application
    private lateinit var scope: CoroutineScope
    private val connectivityManager by lazy {
        application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val mutableNetworkStateFlow = MutableSharedFlow<Boolean>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val networkStateFlow: SharedFlow<Boolean> = mutableNetworkStateFlow.asSharedFlow()

    fun init(application: Application, scope: CoroutineScope) {
        this.application = application
        this.scope = scope

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    suspend fun hasNetworkConnection(): Boolean = mutableNetworkStateFlow.lastOrNull() == true

    private val networkCallback = object :
        ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            scope.launch { mutableNetworkStateFlow.emit(true) }
        }

        override fun onLost(network: Network) {
            scope.launch { mutableNetworkStateFlow.emit(false) }
        }
    }
}