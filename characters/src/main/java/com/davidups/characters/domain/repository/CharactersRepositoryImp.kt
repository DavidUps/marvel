package com.davidups.characters.domain.repository

import com.davidups.characters.data.datasource.CharactersDataSource
import com.davidups.characters.domain.models.toDomain
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import javax.inject.Inject

class CharactersRepositoryImp @Inject constructor(
    private val charactersDataSource: CharactersDataSource
) : CharactersRepository {

    override suspend fun getCharacters(fromPagination: Boolean) = runCatching {
        charactersDataSource.getCharacters(fromPagination)
    }.map { response ->
        when (response) {
            is Either.Left -> Either.Left(error = response.error)
            is Either.Right -> Either.Right(success = response.success.toDomain())
        }
    }.getOrElse {
        Either.Left(error = Failure.Throwable(it))
    }
}
