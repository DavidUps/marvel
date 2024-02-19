package com.davidups.characters.data.local

import com.davidups.characters.data.local.dao.CharactersDAO
import com.davidups.characters.data.local.model.CharactersLocalEntity
import com.davidups.characters.data.models.CharactersEntity
import javax.inject.Inject

class CharacterLocal @Inject constructor(private val characterDao: CharactersDAO) {

    fun putCharacters(characters: CharactersLocalEntity) = characterDao.insert(characters)

    fun updateCharacters(characters: CharactersLocalEntity) = characterDao.update(characters)

    fun getCharacters() = characterDao.getCharacters()

    fun getOffset() = characterDao.getOffset()
}
