package com.davidups.characters.data.datasource

import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either

interface CharactersDataSourceLocal {

    suspend fun getCharacters(): Either<Failure, CharactersEntity?>
    suspend fun saveCharacters(characters: CharactersEntity)
    suspend fun getOffset(): Either<Failure, Int>
}
