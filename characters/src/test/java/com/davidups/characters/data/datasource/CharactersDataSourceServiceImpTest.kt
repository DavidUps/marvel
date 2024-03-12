package com.davidups.characters.data.datasource

import com.davidups.characters.data.models.mockCharactersService
import com.davidups.characters.data.models.mockCharactersServiceData
import com.davidups.characters.data.service.CharacterService
import com.davidups.core.extensions.empty
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
class CharactersDataSourceServiceImpTest {

    @Mock
    private lateinit var mockCharacterService: CharacterService

    private lateinit var charactersDataSourceServiceImp: CharactersDataSourceServiceImp

    @Before
    fun setUp() {
        charactersDataSourceServiceImp =
            CharactersDataSourceServiceImp(service = mockCharacterService)
    }

    @Test
    fun `getCharacters returns server data`() =
        runBlocking {
            val charactersService = mockCharactersService()
            val expectedResult = Either.Right(mockCharactersServiceData())
            whenever(
                mockCharacterService.getCharacters(
                    limit = Constants.Characters.LIMIT,
                    offset = Int.empty()
                )
            ).thenReturn(charactersService)

            val result = charactersDataSourceServiceImp.getCharacters(fromPagination = false, offset = Int.empty())

            assertEquals(expectedResult, result)
        }
}