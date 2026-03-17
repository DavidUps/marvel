package com.davidups.characters.data.repository

import com.davidups.characters.data.datasource.CharactersDataSourceLocal
import com.davidups.characters.data.datasource.CharactersDataSourceService
import com.davidups.characters.data.local.model.toCharactersEntity
import com.davidups.characters.data.models.toCharactersLocalEntity
import com.davidups.characters.data.service.models.toCharactersEntity
import com.davidups.characters.domain.models.Characters
import com.davidups.characters.domain.models.toDomain
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.core.di.IoDispatcher
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.empty
import com.davidups.core.extensions.flatMap
import com.davidups.core.extensions.orEmpty
import com.davidups.core.functional.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharactersRepositoryImp @Inject constructor(
    private val service: CharactersDataSourceService,
    private val local: CharactersDataSourceLocal,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CharactersRepository {

    override suspend fun getCharacters(fromPagination: Boolean): Either<Failure, Characters> =
        withContext(ioDispatcher) {
            local.getCharacters().flatMap { characterLocal ->
                characterLocal?.let {
                    Either.Right(characterLocal.toCharactersEntity().toDomain())
                } ?: run {
                    getCharactersFromService(fromPagination)
                }
            }
        }

    private suspend fun getCharactersFromService(fromPagination: Boolean): Either<Failure, Characters> {
        return service.getCharacters(fromPagination, calculateOffset()).flatMap { result ->
            local.saveCharacters(result.toCharactersEntity().toCharactersLocalEntity())
            Either.Right(result.toCharactersEntity().toDomain())
        }
    }

    private suspend fun calculateOffset(): Int =
        when (val offset = local.getOffset()) {
            is Either.Left -> Int.empty()
            is Either.Right -> offset.success.orEmpty()
        }
}
