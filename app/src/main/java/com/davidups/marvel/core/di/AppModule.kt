package com.davidups.marvel.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.davidups.characters.data.local.database.CharactersDatabase
import com.davidups.core.platform.Constants
import com.davidups.core.platform.service.NetworkHandler
import com.davidups.marvel.core.platform.NetworkHandlerImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideNetworkHandlerImp(@ApplicationContext appContext: Context): NetworkHandler =
        NetworkHandlerImp(context = appContext)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(
            Constants.Core.SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideCharacterDataBase(@ApplicationContext context: Context): CharactersDatabase =
        Room.databaseBuilder(
            context = context,
            klass = CharactersDatabase::class.java,
            name = Constants.Characters.DATABASE_NAME
        ).build()

}
