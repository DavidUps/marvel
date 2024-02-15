package com.davidups.core.platform.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkHandler
(private val context: Context) {
    val isConnected get() = hasInternetConnection()

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCap = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
