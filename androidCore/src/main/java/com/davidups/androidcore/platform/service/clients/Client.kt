package com.davidups.core.platform.service.clients

import com.davidups.core.BuildConfig
import com.davidups.core.platform.Constants
import com.davidups.core.platform.service.clients.UnsafeClient.setUnsafeClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.math.BigInteger
import java.security.MessageDigest

object Client {

    internal fun createClient() = runCatching {

        OkHttpClient.Builder().apply {

            setUnsafeClient(this)

            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                this.addInterceptor(loggingInterceptor)
            }
            addInterceptor { chain ->
                val currentTimestamp = System.currentTimeMillis()
                val newUrl = chain.request().url
                    .newBuilder()
                    .addQueryParameter(Constants.Core.Request.TS, currentTimestamp.toString())
                    .addQueryParameter(Constants.Core.Request.API_KEY, BuildConfig.PUBLIC_KEY)
                    .addQueryParameter(
                        Constants.Core.Request.HASH,
                        (currentTimestamp.toString() + BuildConfig.PRIVATE_KEY + BuildConfig.PUBLIC_KEY).md5()
                    )
                    .build()

                val newRequest = chain.request()
                    .newBuilder()
                    .url(newUrl)
                    .build()
                chain.proceed(newRequest)
            }
        }
    }.map {
        it.build()
    }
        .getOrNull()

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}
