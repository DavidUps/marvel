package com.davidups.characters.data.service.models

import com.davidups.characters.data.models.CharacterEntity
import com.davidups.core.extensions.empty

data class CharacterServiceEntity(
    val id: Int?,
    val name: String?,
    val description: String?,
    val modified: String?,
    val resourceURI: String?,
    val thumbnail: CharacterThumbnailServiceEntity?
) {
    companion object {
        fun empty() =
            CharacterServiceEntity(
                Int.empty(),
                String.empty(),
                String.empty(),
                null,
                String.empty(),
                CharacterThumbnailServiceEntity.empty()
            )
    }
}

fun CharacterServiceEntity.toCharacterEntity() = CharacterEntity(
    id = id,
    name = name,
    description = description,
    modified = modified,
    resourceURI = resourceURI,
    thumbnail = thumbnail?.toCharacterThumbnailEntity()
)