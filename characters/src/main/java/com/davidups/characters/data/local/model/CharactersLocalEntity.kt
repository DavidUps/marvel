package com.davidups.characters.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.davidups.characters.data.local.CharacterLocal
import com.davidups.characters.data.local.database.typeconverters.CharacterConverter
import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.extensions.empty

@Entity
data class CharactersLocalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var offset: Int?,
    val limit: Int?,
    val total: Int?,
    val count: Int?,
    @TypeConverters(CharacterConverter::class)
    var results: MutableList<CharacterLocalEntity>?
) {
    companion object {
        fun empty() =
            CharactersLocalEntity(
                Int.empty(),
                Int.empty(),
                Int.empty(),
                Int.empty(),
                Int.empty(),
                mutableListOf()
            )
    }
}

fun CharactersLocalEntity.toCharactersEntity() = CharactersEntity(
    id = id,
    offset = offset,
    limit = limit,
    total = total,
    count = count,
    results = results?.map { it.toCharacterEntity() }?.toMutableList()
)
