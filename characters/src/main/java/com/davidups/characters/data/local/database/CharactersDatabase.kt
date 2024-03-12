package com.davidups.characters.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davidups.characters.data.local.dao.CharactersDAO
import com.davidups.characters.data.local.database.typeconverters.CharacterConverter
import com.davidups.characters.data.local.model.CharactersLocalEntity

@Database(entities = [CharactersLocalEntity::class], version = 1, exportSchema = false)
@TypeConverters(CharacterConverter::class)
abstract class CharactersDatabase : RoomDatabase() {

    abstract fun characterDao(): CharactersDAO
}
