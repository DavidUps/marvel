package com.davidups.characters.data.local.model

import androidx.room.Entity
import com.davidups.characters.data.models.CharacterEntity
import com.davidups.characters.data.service.models.CharacterServiceEntity
import com.davidups.characters.data.service.models.toCharacterThumbnailEntity
import com.davidups.core.extensions.empty

@Entity
data class CharacterLocalEntity(
    val id: Int?,
    val name: String?,
    val description: String?,
    val modified: String?,
    val resourceURI: String?,
    val thumbnail: CharacterThumbnailLocalEntity?
) {
    companion object {
        fun empty() =
            CharacterLocalEntity(
                Int.empty(),
                String.empty(),
                String.empty(),
                null,
                String.empty(),
                CharacterThumbnailLocalEntity.empty()
            )
    }
}

fun CharacterLocalEntity.toCharacterEntity() = CharacterEntity(
    id = id,
    name = name,
    description = description,
    modified = modified,
    resourceURI = resourceURI,
    thumbnail = thumbnail?.toCharacterThumbnailEntity()
)
