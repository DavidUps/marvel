package com.davidups.characters.data.datasource

import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either

interface CharactersDataSource {

    suspend fun getCharacters(fromPagination: Boolean): Either<Failure, CharactersEntity>
}
