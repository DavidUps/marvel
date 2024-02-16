package com.davidups.core.di

import com.davidups.core.BuildConfig
import com.davidups.core.platform.service.clients.Client
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder().apply {
        this.baseUrl(BuildConfig.BASE_URL)
        this.client(Client.createClient())
        this.addConverterFactory(GsonConverterFactory.create())
    }.build()

}
