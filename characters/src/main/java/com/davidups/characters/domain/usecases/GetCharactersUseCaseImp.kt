package com.davidups.characters.domain.usecases

import com.davidups.characters.domain.models.Characters
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.orEmpty
import com.davidups.core.functional.Either
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetCharactersUseCaseImp @Inject constructor(private val repository: CharactersRepository) :
    GetCharacterUseCase {

    override fun invoke(fromPagination: Boolean): Flow<Either<Failure, Characters>> = flow {
        runCatching {
            repository.getCharacters(fromPagination.orEmpty())
        }.map { response ->
            when (response) {
                is Either.Left -> emit(Either.Left(error = response.error))
                is Either.Right -> {
                    val result = response.success
                    result.let { characters ->
                        characters.results?.toMutableList()
                            ?.sortByDescending { character -> character.id }
                    }
                    emit(Either.Right(success = result))
                }
            }
        }.getOrElse {
            Either.Left(error = Failure.Throwable(it))
        }
    }.flowOn(Dispatchers.IO)
}
