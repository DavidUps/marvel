package com.davidups.characters.data.datasource

import com.davidups.characters.data.local.CharacterLocal
import com.davidups.characters.data.models.CharactersEntity
import com.davidups.characters.data.service.CharacterService
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.orEmpty
import com.davidups.core.functional.Either
import com.davidups.core.platform.Constants
import javax.inject.Inject

class CharactersDataSourceImp @Inject constructor(
    private val service: CharacterService,
    private val local: CharacterLocal
) : CharactersDataSource {

    override suspend fun getCharacters(fromPagination: Boolean) = runCatching {
        local.getCharacters()
    }.map { charactersLocal ->
        if (charactersLocal != null && fromPagination.not()) {
            Either.Right(success = charactersLocal)
        } else {
            getCharactersFromService()
        }
    }.getOrElse {
        Either.Left(error = Failure.Throwable(it))
    }

    private suspend fun getCharactersFromService(): Either<Failure, CharactersEntity> =
        runCatching {
            service.getCharacters(Constants.Characters.LIMIT, calculateOffset())
        }.map { response ->
            when (response) {
                is Either.Left -> Either.Left(error = response.error)
                is Either.Right -> {
                    response.success.data?.let { result ->
                        saveLocal(result)
                        Either.Right(success = result)
                    } ?: run {
                        Either.Left(error = Failure.Throwable(Throwable(message = Constants.Error.SERVER_ERROR)))
                    }
                }
            }
        }.getOrElse {
            Either.Left(error = Failure.Throwable(it))
        }

    fun calculateOffset() =
        local.getOffset()?.let { it + Constants.Characters.LIMIT }.orEmpty()

    private fun saveLocal(characters: CharactersEntity) {
        val getCharactersLocal = local.getCharacters()
        getCharactersLocal?.let {
            it.offset = it.offset?.plus(Constants.Characters.LIMIT)
            it.results?.addAll(characters.results.orEmpty())
            local.updateCharacters(it)
        } ?: run {
            local.putCharacters(characters)
        }
    }
}
