package com.davidups.characters.data.models

import com.davidups.characters.data.local.model.CharactersLocalEntity
import com.davidups.core.extensions.empty

data class CharactersEntity(
    val id: Int?,
    var offset: Int?,
    val limit: Int?,
    val total: Int?,
    val count: Int?,
    var results: MutableList<CharacterEntity>?
) {
    companion object {
        fun empty() =
            CharactersEntity(
                Int.empty(),
                Int.empty(),
                Int.empty(),
                Int.empty(),
                Int.empty(),
                mutableListOf()
            )
    }
}

fun CharactersEntity.toCharactersLocalEntity() = CharactersLocalEntity(
    id = id,
    offset = offset,
    limit = limit,
    total = total,
    count = count,
    results = results?.map { it.toCharacterLocalEntity() }?.toMutableList()
)
