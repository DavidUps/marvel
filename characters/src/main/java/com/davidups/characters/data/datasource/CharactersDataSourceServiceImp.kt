package com.davidups.characters.data.datasource

import com.davidups.characters.data.service.CharacterService
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import com.davidups.core.platform.Constants
import javax.inject.Inject

class CharactersDataSourceServiceImp @Inject constructor(
    private val service: CharacterService,
) : CharactersDataSourceService {

    override suspend fun getCharacters(fromPagination: Boolean, offset: Int) = runCatching {
        service.getCharacters(Constants.Characters.LIMIT, offset)
    }.map { response ->
        when (response) {
            is Either.Left -> Either.Left(error = response.error)
            is Either.Right -> {
                response.success.data?.let { result ->
                    Either.Right(success = result)
                } ?: run {
                    Either.Left(error = Failure.Throwable(Throwable(message = Constants.Error.SERVER_ERROR)))
                }
            }
        }
    }.getOrElse {
        Either.Left(error = Failure.Throwable(it))
    }
}
