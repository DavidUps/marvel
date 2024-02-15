package com.davidups.characters.data.local.database.typeconverters

import androidx.room.TypeConverter
import com.davidups.characters.data.models.CharacterEntity
import com.davidups.core.extensions.empty
import com.google.gson.Gson

object CharacterConverter {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun toCharacter(data: String?): MutableList<CharacterEntity> {
        return if (data.isNullOrEmpty()) {
            mutableListOf()
        } else {
            gson.fromJson(data, Array<CharacterEntity>::class.java).map { it }
                .toMutableList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromCharacter(data: MutableList<CharacterEntity>?): String {
        return if (data == null) {
            String.empty()
        } else {
            gson.toJson(data)
        }
    }
}
