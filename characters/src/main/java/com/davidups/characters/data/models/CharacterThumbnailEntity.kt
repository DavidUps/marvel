package com.davidups.characters.data.models

import com.davidups.characters.data.local.model.CharacterThumbnailLocalEntity
import com.davidups.characters.domain.models.CharacterThumbnail
import com.davidups.core.extensions.empty

data class CharacterThumbnailEntity(val path: String?, val extension: String?, val image: String?) {

    companion object {
        fun empty() = CharacterThumbnailEntity(String.empty(), String.empty(), String.empty())
    }

    fun toCharacterThumbnail() = CharacterThumbnail(path, extension, image)
}

fun CharacterThumbnailEntity.toCharacterThumbnailLocalEntity() = CharacterThumbnailLocalEntity(
    path = path,
    extension = extension,
    image = image
)
