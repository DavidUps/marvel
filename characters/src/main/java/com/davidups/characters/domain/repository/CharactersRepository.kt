package com.davidups.characters.domain.repository

import com.davidups.characters.domain.models.Characters
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either

interface CharactersRepository {

    suspend fun getCharacters(fromPagination: Boolean): Either<Failure, Characters>
}
