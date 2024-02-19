package com.davidups.characters.data.service.models

import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.extensions.empty

data class CharactersServiceEntity(
    val id: Int?,
    var offset: Int?,
    val limit: Int?,
    val total: Int?,
    val count: Int?,
    var results: MutableList<CharacterServiceEntity>?
) {
    companion object {
        fun empty() =
            CharactersServiceEntity(
                Int.empty(),
                Int.empty(),
                Int.empty(),
                Int.empty(),
                Int.empty(),
                mutableListOf()
            )
    }
}

fun CharactersServiceEntity.toCharactersEntity() = CharactersEntity(
    id = id,
    offset = offset,
    limit = limit,
    total = total,
    count = count,
    results = results?.map { it.toCharacterEntity() }?.toMutableList()
)

