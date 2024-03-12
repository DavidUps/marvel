package com.davidups.characters.data.datasource

import com.davidups.characters.data.local.CharacterLocal
import com.davidups.characters.data.models.mockCharactersLocalData
import com.davidups.core.functional.Either
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
class CharactersDataSourceLocalImpTest {

    @Mock
    private lateinit var mockCharacterLocal: CharacterLocal

    private lateinit var charactersDataSourceLocalImp: CharactersDataSourceLocalImp

    @Before
    fun setUp() {
        charactersDataSourceLocalImp = CharactersDataSourceLocalImp(local = mockCharacterLocal)
    }

    @Test
    fun `getCharacters returns local`() =
        runBlocking {
            val charactersLocal = Either.Right(success = mockCharactersLocalData())
            whenever(mockCharacterLocal.getCharacters()).thenReturn(mockCharactersLocalData())

            val result = charactersDataSourceLocalImp.getCharacters()

            assertEquals(charactersLocal, result)
        }

    @Test
    fun `saveCharacters local`() =
        runBlocking {
            val charactersLocalEntity = mockCharactersLocalData()
            whenever(mockCharacterLocal.getCharacters()).thenReturn(null)

            charactersDataSourceLocalImp.saveCharacters(charactersLocalEntity)

            verify(mockCharacterLocal).putCharacters(charactersLocalEntity)
        }

    @Test
    fun `getOffset returns data`() =
        runBlocking {
            val expectedData = Either.Right(success = 1)
            whenever(mockCharacterLocal.getOffset()).thenReturn(1)

            val result = charactersDataSourceLocalImp.getOffset()

            assertEquals(expectedData, result)
        }

}