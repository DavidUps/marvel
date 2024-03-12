package com.davidups.characters.data.repository

import com.davidups.characters.data.datasource.CharactersDataSourceLocal
import com.davidups.characters.data.datasource.CharactersDataSourceService
import com.davidups.characters.data.local.model.toCharactersEntity
import com.davidups.characters.data.models.toCharactersLocalEntity
import com.davidups.characters.data.service.models.toCharactersEntity
import com.davidups.characters.domain.models.Characters
import com.davidups.characters.domain.models.toDomain
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.empty
import com.davidups.core.extensions.orEmpty
import com.davidups.core.functional.Either
import javax.inject.Inject

class CharactersRepositoryImp @Inject constructor(
    private val service: CharactersDataSourceService,
    private val local: CharactersDataSourceLocal
) : CharactersRepository {

    override suspend fun getCharacters(fromPagination: Boolean) = runCatching {
        local.getCharacters()
    }.map { charactersLocal ->
        when (charactersLocal) {
            is Either.Left -> {
                Either.Left(error = charactersLocal.error)
            }
            is Either.Right -> {
                if (fromPagination.not()) {
                    charactersLocal.success?.let {
                        Either.Right(
                            success = it.toCharactersEntity().toDomain()
                        )
                    } ?: run {
                        getCharactersFromService(fromPagination)
                    }
                } else {
                    getCharactersFromService(fromPagination)
                }
            }
        }
    }.getOrElse {
        Either.Left(error = Failure.Throwable(it))
    }

    private suspend fun getCharactersFromService(fromPagination: Boolean): Either<Failure, Characters> =
        runCatching {
            service.getCharacters(fromPagination, calculateOffset())
        }.map { response ->
            when (response) {
                is Either.Left -> Either.Left(error = response.error)
                is Either.Right -> {
                    response.success.let { result ->
                        local.saveCharacters(result.toCharactersEntity().toCharactersLocalEntity())
                        Either.Right(success = result.toCharactersEntity().toDomain())
                    }
                }
            }
        }.getOrElse {
            Either.Left(error = Failure.Throwable(it))
        }

    suspend fun calculateOffset(): Int = runCatching {
        when (val offset = local.getOffset()) {
            is Either.Left -> Int.empty()
            is Either.Right -> offset.success.orEmpty()
        }
    }.getOrElse { Int.empty() }

}
