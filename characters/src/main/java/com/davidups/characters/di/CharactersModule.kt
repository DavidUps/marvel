package com.davidups.characters.di

import android.content.Context
import androidx.room.Room
import com.davidups.characters.data.datasource.CharactersDataSource
import com.davidups.characters.data.datasource.CharactersDataSourceImp
import com.davidups.characters.data.local.CharacterLocal
import com.davidups.characters.data.local.dao.CharactersDAO
import com.davidups.characters.data.local.database.CharactersDatabase
import com.davidups.characters.data.service.CharacterApi
import com.davidups.characters.data.service.CharacterService
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.characters.domain.repository.CharactersRepositoryImp
import com.davidups.characters.domain.usecases.GetCharacterUseCase
import com.davidups.characters.domain.usecases.GetCharactersUseCaseImp
import com.davidups.core.platform.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideCharacterDao(database: CharactersDatabase): CharactersDAO =
        database.characterDao()

    @Provides
    @Singleton
    fun provideCharacterDataBase(@ApplicationContext context: Context): CharactersDatabase =
        Room.databaseBuilder(
            context = context,
            klass = CharactersDatabase::class.java,
            name = "Constants.Characters.DATABASE_NAME"
        ).build()

    @Provides
    @Singleton
    fun provideCharacterLocal(charactersDAO: CharactersDAO) =
        CharacterLocal(charactersDAO)

    @Provides
    @Singleton
    fun provideCharacterDataSource(
        local: CharacterLocal,
        service: CharacterService,
    ): CharactersDataSource {
        return CharactersDataSourceImp(
            local = local,
            service = service,
        )
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): CharacterApi = retrofit.create(CharacterApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun provideCharacterRepository(imp: CharactersRepositoryImp): CharactersRepository

    @Binds
    @Singleton
    abstract fun provideGetCharacterUseCase(imp: GetCharactersUseCaseImp): GetCharacterUseCase
}
