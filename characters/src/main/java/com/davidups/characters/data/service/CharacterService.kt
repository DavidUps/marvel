package com.davidups.characters.data.service

import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import com.davidups.core.platform.BaseResponse
import com.davidups.core.platform.service.Request
import javax.inject.Inject

class CharacterService @Inject constructor(
    private val characterApi: CharacterApi,
    private val request: Request
) {

    suspend fun getCharacters(
        limit: Int?,
        offset: Int?
    ): Either<Failure, BaseResponse<CharactersEntity>> =
        request.launch(characterApi.getCharacters(limit, offset))

    suspend fun getCharacter(id: String?) = request.launch(characterApi.getCharacter(id))
}
