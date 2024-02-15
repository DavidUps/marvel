package com.davidups.characters.domain.models

import com.davidups.characters.data.models.CharacterEntity

data class Character(
    val id: Int?,
    val name: String?,
    val description: String?,
    val modified: String?,
    val resourceURI: String?,
    val characterImage: CharacterThumbnail?
) {
    companion object {
        fun empty() = Character(
            id = null,
            name = null,
            description = null,
            modified = null,
            resourceURI = null,
            characterImage = null
        )
    }
}

fun CharacterEntity.toDomain() = Character(
    this.id,
    this.name,
    this.description,
    this.modified,
    this.resourceURI,
    this.thumbnail?.toCharacterThumbnail()
)
