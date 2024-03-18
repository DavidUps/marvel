package com.davidups.characters.domain.usecases

import com.davidups.characters.domain.models.Characters
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.onFailure
import com.davidups.core.extensions.onSuccess
import com.davidups.core.extensions.orEmpty
import com.davidups.core.functional.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCharactersUseCaseImp @Inject constructor(private val repository: CharactersRepository) :
    GetCharacterUseCase {

    override fun invoke(fromPagination: Boolean): Flow<Either<Failure, Characters>> = flow {
        repository.getCharacters(fromPagination.orEmpty())
            .onSuccess { result ->
                result.let { characters ->
                    characters.results?.toMutableList()
                        ?.sortByDescending { character -> character.id }
                }
                emit(Either.Right(success = result))
            }
            .onFailure { failure ->
                emit(Either.Left(error = failure))
            }
    }.flowOn(Dispatchers.IO)
}
