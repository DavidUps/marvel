package com.davidups.characters.data.datasource

import com.davidups.characters.data.models.CharactersEntity
import com.davidups.characters.data.service.models.CharactersServiceEntity
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either

interface CharactersDataSourceService {

    suspend fun getCharacters(fromPagination: Boolean, offset: Int): Either<Failure, CharactersServiceEntity>
}
