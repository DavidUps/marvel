package com.davidups.characters.data.datasource

import com.davidups.characters.data.service.CharacterService
import com.davidups.characters.data.service.models.CharactersServiceEntity
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.flatMap
import com.davidups.core.extensions.onFailure
import com.davidups.core.extensions.onSuccess
import com.davidups.core.functional.Either
import com.davidups.core.platform.Constants
import javax.inject.Inject

class CharactersDataSourceServiceImp @Inject constructor(
    private val service: CharacterService,
) : CharactersDataSourceService {

    override suspend fun getCharacters(
        fromPagination: Boolean,
        offset: Int
    ): Either<Failure, CharactersServiceEntity> {
        return service.getCharacters(Constants.Characters.LIMIT, offset)
            .flatMap { response ->
                response.data?.let { result ->
                    Either.Right(success = result)
                } ?: run {
                    Either.Left(error = Failure.Throwable(Throwable(message = Constants.Error.SERVER_ERROR)))
                }
            }
    }
}