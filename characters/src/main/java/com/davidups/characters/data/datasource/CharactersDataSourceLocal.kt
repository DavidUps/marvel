package com.davidups.characters.data.datasource

import com.davidups.characters.data.local.model.CharactersLocalEntity
import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either

interface CharactersDataSourceLocal {

    suspend fun getCharacters(): Either<Failure, CharactersLocalEntity?>
    suspend fun saveCharacters(characters: CharactersLocalEntity)
    suspend fun getOffset(): Either<Failure, Int>
}
