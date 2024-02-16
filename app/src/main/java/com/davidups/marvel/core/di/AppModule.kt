package com.davidups.marvel.core.di

import android.content.Context
import android.content.SharedPreferences
import com.davidups.core.BuildConfig
import com.davidups.core.platform.Constants
import com.davidups.core.platform.service.NetworkHandler
import com.davidups.core.platform.service.clients.Client
import com.davidups.marvel.core.platform.NetworkHandlerImp
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
    fun provideNetworkHandlerImp(@ApplicationContext appContext: Context): NetworkHandler =
        NetworkHandlerImp(context = appContext)

}
