package com.davidups.characters.data.datasource

import com.davidups.characters.data.local.CharacterLocal
import com.davidups.characters.data.models.mockCharactersData
import com.davidups.characters.data.models.mockCharactersService
import com.davidups.characters.data.service.CharacterService
import com.davidups.core.functional.Either
import com.davidups.core.platform.Constants
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CharactersDataSourceImpTest {

    @Mock
    private lateinit var mockCharacterService: CharacterService

    @Mock
    private lateinit var mockCharacterLocal: CharacterLocal

    private lateinit var charactersDataSourceImp: CharactersDataSourceImp

    @Before
    fun setUp() {
        charactersDataSourceImp = CharactersDataSourceImp(mockCharacterService, mockCharacterLocal)
    }

    @Test
    fun `getCharacters returns local data when not from pagination`() =
        runBlocking {
            val charactersLocal = Either.Right(success = mockCharactersData())
            whenever(mockCharacterLocal.getCharacters()).thenReturn(mockCharactersData())

            val result = charactersDataSourceImp.getCharacters(fromPagination = false)

            assertEquals(charactersLocal, result)
        }

    @Test
    fun `getCharacters returns service data when not from pagination`() =
        runBlocking {
            val charactersService = Either.Right(success = mockCharactersData())
            whenever(mockCharacterService.getCharacters(Constants.Characters.LIMIT, charactersDataSourceImp.calculateOffset())).thenReturn(mockCharactersService())

            val result = charactersDataSourceImp.getCharacters(fromPagination = false)

            assertEquals(charactersService, result)
        }

    @Test
    fun `after updateCharacters call to getCharacters returns local data when not from pagination`() =
        runBlocking {
            mockCharacterLocal.updateCharacters(mockCharactersData())
            val charactersLocal = Either.Right(success = mockCharactersData())
            whenever(mockCharacterLocal.getCharacters()).thenReturn(mockCharactersData())

            val result = charactersDataSourceImp.getCharacters(fromPagination = false)

            assertEquals(charactersLocal, result)
        }


}