package com.davidups.characters.data.models

import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import com.davidups.core.platform.BaseResponse

fun mockCharactersData(): CharactersEntity = CharactersEntity.empty()

fun mockCharactersService(): Either<Failure, BaseResponse<CharactersEntity>> {

    // Wrap it in a BaseResponse
    val baseResponse = BaseResponse(
        data = mockCharactersData(),
        code = 200,
        status = null,
        copyright = null,
        attributionHTML = null,
        attributionText = null,
        etag = null
    )

    // Return it as the success case of Either
    return Either.Right(success = baseResponse)
}

fun mockCharacterData() : CharacterEntity = CharacterEntity.empty()

fun mockCharacterService(): Either<Failure, BaseResponse<CharacterEntity>> {

    // Wrap it in a BaseResponse
    val baseResponse = BaseResponse(
        data = mockCharacterData(),
        code = 200,
        status = null,
        copyright = null,
        attributionHTML = null,
        attributionText = null,
        etag = null
    )

    // Return it as the success case of Either
    return Either.Right(success = baseResponse)
}