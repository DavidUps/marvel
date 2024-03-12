package com.davidups.characters.data.models

import com.davidups.characters.data.local.model.CharactersLocalEntity
import com.davidups.characters.data.service.CharacterService
import com.davidups.characters.data.service.models.CharacterServiceEntity
import com.davidups.characters.data.service.models.CharactersServiceEntity
import com.davidups.characters.domain.models.Characters
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import com.davidups.core.platform.BaseResponse

fun mockCharactersDomain(): Characters = Characters.empty()
fun mockCharactersLocalData(): CharactersLocalEntity = CharactersLocalEntity.empty()
fun mockCharactersServiceData(): CharactersServiceEntity = CharactersServiceEntity.empty()

fun mockCharactersService(): Either<Failure, BaseResponse<CharactersServiceEntity>> {

    // Wrap it in a BaseResponse
    val baseResponse = BaseResponse(
        data = mockCharactersServiceData(),
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

fun mockCharacterServiceData(): CharacterServiceEntity = CharacterServiceEntity.empty()

fun mockCharacterService(): Either<Failure, BaseResponse<CharacterServiceEntity>> {

    // Wrap it in a BaseResponse
    val baseResponse = BaseResponse(
        data = mockCharacterServiceData(),
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