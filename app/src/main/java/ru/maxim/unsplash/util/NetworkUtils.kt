package ru.maxim.unsplash.util

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
import kotlinx.coroutines.launch
/**
 * Utility class for observing network connection state
 **/
class NetworkUtils(
    private val scope: CoroutineScope,
    connectivityManager: ConnectivityManager
) {
    private val mutableNetworkStateFlow = MutableSharedFlow<Boolean>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val networkStateFlow: SharedFlow<Boolean> = mutableNetworkStateFlow.asSharedFlow()

    private val networkCallback = object :
        ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            scope.launch { mutableNetworkStateFlow.emit(true) }
        }

        override fun onLost(network: Network) {
            scope.launch { mutableNetworkStateFlow.emit(false) }
        }
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }
}