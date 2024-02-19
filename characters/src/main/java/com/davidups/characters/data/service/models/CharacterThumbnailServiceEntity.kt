package com.davidups.characters.data.service.models

import com.davidups.characters.data.models.CharacterThumbnailEntity
import com.davidups.characters.domain.models.CharacterThumbnail
import com.davidups.core.extensions.empty

data class CharacterThumbnailServiceEntity(
    val path: String?,
    val extension: String?,
    val image: String?
) {

    companion object {
        fun empty() =
            CharacterThumbnailServiceEntity(String.empty(), String.empty(), String.empty())
    }

    fun toCharacterThumbnail() = CharacterThumbnail(path, extension, image)
}

fun CharacterThumbnailServiceEntity.toCharacterThumbnailEntity() =
    CharacterThumbnailEntity(path, extension, image)
