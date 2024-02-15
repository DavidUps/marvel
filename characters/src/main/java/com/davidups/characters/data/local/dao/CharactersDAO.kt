package com.davidups.characters.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.platform.BaseDao

@Dao
interface CharactersDAO : BaseDao<CharactersEntity> {

    @Query("SELECT * FROM CharactersEntity")
    fun getCharacters(): CharactersEntity?

    @Query("SELECT `offset` FROM CharactersEntity")
    fun getOffset(): Int?
}
