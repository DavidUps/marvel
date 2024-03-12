package com.davidups.characters.data.models

import com.davidups.characters.data.local.model.CharacterLocalEntity
import com.davidups.core.extensions.empty

data class CharacterEntity(
    val id: Int?,
    val name: String?,
    val description: String?,
    val modified: String?,
    val resourceURI: String?,
    val thumbnail: CharacterThumbnailEntity?
) {
    companion object {
        fun empty() =
            CharacterEntity(
                Int.empty(),
                String.empty(),
                String.empty(),
                null,
                String.empty(),
                CharacterThumbnailEntity.empty()
            )
    }
}

fun CharacterEntity.toCharacterLocalEntity() = CharacterLocalEntity(
    id = id,
    name = name,
    description = description,
    modified = modified,
    resourceURI = resourceURI,
    thumbnail = thumbnail?.toCharacterThumbnailLocalEntity()
)
