package com.davidups.characters.data.datasource

import com.davidups.characters.data.local.CharacterLocal
import com.davidups.characters.data.models.CharactersEntity
import com.davidups.characters.data.service.CharacterService
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.orEmpty
import com.davidups.core.functional.Either
import com.davidups.core.platform.Constants
import javax.inject.Inject

class CharactersDataSourceLocalImp @Inject constructor(
    private val local: CharacterLocal
) : CharactersDataSourceLocal {

    override suspend fun getCharacters() = runCatching {
        local.getCharacters()
    }.map { charactersLocal ->
        Either.Right(success = charactersLocal)
    }.getOrElse {
        Either.Left(error = Failure.Throwable(it))
    }

    override suspend fun saveCharacters(characters: CharactersEntity) {
        val getCharactersLocal = local.getCharacters()
        getCharactersLocal?.let {
            it.offset = it.offset?.plus(Constants.Characters.LIMIT)
            it.results?.addAll(characters.results.orEmpty())
            local.updateCharacters(it)
        } ?: run {
            local.putCharacters(characters)
        }
    }

    override suspend fun getOffset(): Either<Failure, Int> {
        local.getOffset()?.let { offset ->
            return Either.Right(success = offset)
        } ?: run {
            return Either.Left(error = Failure.Throwable(Throwable(message = Constants.Error.DATABASE_ERROR)))
        }
    }
}
