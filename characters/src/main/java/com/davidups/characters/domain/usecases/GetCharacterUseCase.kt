package com.davidups.characters.domain.usecases

import com.davidups.characters.domain.models.Characters
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import kotlinx.coroutines.flow.Flow

interface GetCharacterUseCase {
    fun invoke(fromPagination: Boolean): Flow<Either<Failure, Characters>>
}
