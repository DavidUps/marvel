package com.davidups.core.di

import android.content.Context
import android.content.SharedPreferences
import com.davidups.core.BuildConfig
import com.davidups.core.platform.Constants
import com.davidups.core.platform.service.NetworkHandler
import com.davidups.core.platform.service.clients.Client
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideNetworkHandler(@ApplicationContext appContext: Context): NetworkHandler =
        NetworkHandler(context = appContext)

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder().apply {
        this.baseUrl(BuildConfig.BASE_URL)
        this.client(Client.createClient())
        this.addConverterFactory(GsonConverterFactory.create())
    }.build()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(
            Constants.Core.SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }
}
