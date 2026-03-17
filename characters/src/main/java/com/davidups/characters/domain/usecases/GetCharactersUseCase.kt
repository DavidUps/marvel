package com.davidups.characters.domain.usecases

import com.davidups.characters.domain.models.Characters
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.mapSuccess
import com.davidups.core.extensions.orEmpty
import com.davidups.core.functional.Either
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharactersRepository,
) {
    suspend operator fun invoke(fromPagination: Boolean): Either<Failure, Characters> =
        repository.getCharacters(fromPagination.orEmpty())
            .mapSuccess { characters ->
                characters.copy(
                    results = characters.results?.sortedByDescending { it.id }
                )
            }
}
