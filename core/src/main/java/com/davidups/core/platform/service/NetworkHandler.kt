package com.davidups.core.platform.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
interface NetworkHandler {
    fun hasInternetConnection(): Boolean
}
