package com.davidups.marvel.core.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.davidups.core.platform.service.NetworkHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkHandlerImp @Inject constructor(@ApplicationContext private val context: Context) :
    NetworkHandler {

    override fun hasInternetConnection(): Boolean {
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
