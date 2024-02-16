package com.davidups.characters.di

import com.davidups.characters.data.datasource.CharactersDataSourceLocal
import com.davidups.characters.data.datasource.CharactersDataSourceLocalImp
import com.davidups.characters.data.datasource.CharactersDataSourceService
import com.davidups.characters.data.datasource.CharactersDataSourceServiceImp
import com.davidups.characters.data.local.CharacterLocal
import com.davidups.characters.data.local.dao.CharactersDAO
import com.davidups.characters.data.local.database.CharactersDatabase
import com.davidups.characters.data.repository.CharactersRepositoryImp
import com.davidups.characters.data.service.CharacterApi
import com.davidups.characters.data.service.CharacterService
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.characters.domain.usecases.GetCharacterUseCase
import com.davidups.characters.domain.usecases.GetCharactersUseCaseImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideCharacterDao(database: CharactersDatabase): CharactersDAO =
        database.characterDao()

    @Provides
    @Singleton
    fun provideCharacterLocal(charactersDAO: CharactersDAO) =
        CharacterLocal(charactersDAO)

    @Provides
    @Singleton
    fun provideCharacterDataSourceLocal(
        local: CharacterLocal,
    ): CharactersDataSourceLocal = CharactersDataSourceLocalImp(local)

    @Provides
    @Singleton
    fun provideCharacterDataSourceService(
        service: CharacterService,
    ): CharactersDataSourceService = CharactersDataSourceServiceImp(service)

    @Provides
    @Singleton
    fun provideCharacterRepository(
        local: CharactersDataSourceLocal,
        service: CharactersDataSourceService,
    ): CharactersRepository {
        return CharactersRepositoryImp(
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
    abstract fun provideGetCharacterUseCase(imp: GetCharactersUseCaseImp): GetCharacterUseCase
}
