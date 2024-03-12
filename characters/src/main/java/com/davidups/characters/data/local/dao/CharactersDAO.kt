package com.davidups.characters.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.davidups.characters.data.local.model.CharactersLocalEntity
import com.davidups.core.platform.BaseDao

@Dao
interface CharactersDAO : BaseDao<CharactersLocalEntity> {

    @Query("SELECT * FROM CharactersLocalEntity")
    fun getCharacters(): CharactersLocalEntity?

    @Query("SELECT `offset` FROM CharactersLocalEntity")
    fun getOffset(): Int?
}
