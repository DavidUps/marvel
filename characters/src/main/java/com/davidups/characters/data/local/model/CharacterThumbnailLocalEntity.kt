package com.davidups.characters.data.local.model

import androidx.room.Entity
import com.davidups.characters.data.models.CharacterThumbnailEntity
import com.davidups.characters.data.service.models.CharacterThumbnailServiceEntity
import com.davidups.characters.domain.models.CharacterThumbnail
import com.davidups.core.extensions.empty

@Entity
data class CharacterThumbnailLocalEntity(
    val path: String?,
    val extension: String?,
    val image: String?
) {

    companion object {
        fun empty() = CharacterThumbnailLocalEntity(String.empty(), String.empty(), String.empty())
    }
}

fun CharacterThumbnailLocalEntity.toCharacterThumbnailEntity() =
    CharacterThumbnailEntity(path, extension, image)

