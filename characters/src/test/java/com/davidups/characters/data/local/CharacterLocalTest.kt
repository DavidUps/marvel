package com.davidups.characters.data.local

import com.davidups.characters.data.local.dao.CharactersDAO
import com.davidups.characters.data.models.mockCharactersData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CharacterLocalTest {

    @Mock
    private lateinit var mockCharactersDAO: CharactersDAO

    private lateinit var characterLocal: CharacterLocal

    @Before
    fun setUp() {
        characterLocal = CharacterLocal(mockCharactersDAO)
    }

    @Test
    fun `putCharacters calls insert on DAO`() = runBlocking {
        val characters = mockCharactersData()

        characterLocal.putCharacters(characters)

        verify(mockCharactersDAO).insert(characters)
    }

    @Test
    fun `updateCharacters calls getCharacters on DAO`() = runBlocking {
        val characters = mockCharactersData()

        characterLocal.updateCharacters(characters)

        verify(mockCharactersDAO).update(characters)
    }

    @Test
    fun `getCharacters calls getCharacters on DAO`() = runBlocking {
        whenever(mockCharactersDAO.getCharacters()).thenReturn(mockCharactersData())

        val result = mockCharactersDAO.getCharacters()

        assertEquals(mockCharactersData(), result)
    }

    @Test
    fun `getOffset calls getCharacters on DAO`() = runBlocking {
        whenever(mockCharactersDAO.getOffset()).thenReturn(1)

        val result = mockCharactersDAO.getOffset()

        assertEquals(1, result)
    }
}