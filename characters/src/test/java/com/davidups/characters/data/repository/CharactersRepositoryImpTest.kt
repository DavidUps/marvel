package com.davidups.characters.data.repository

import com.davidups.characters.data.datasource.CharactersDataSourceLocal
import com.davidups.characters.data.datasource.CharactersDataSourceService
import com.davidups.characters.data.models.mockCharactersDomain
import com.davidups.characters.data.models.mockCharactersLocalData
import com.davidups.characters.data.models.mockCharactersServiceData
import com.davidups.core.functional.Either
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CharactersRepositoryImpTest {

    @Mock
    private lateinit var mockCharactersDataSourceService: CharactersDataSourceService

    @Mock
    private lateinit var mockCharactersDataSourceLocal: CharactersDataSourceLocal

    private lateinit var charactersRepositoryImp: CharactersRepositoryImp

    @Before
    fun setUp() {
        charactersRepositoryImp = CharactersRepositoryImp(
            service = mockCharactersDataSourceService,
            local = mockCharactersDataSourceLocal
        )
    }

    @Test
    fun `getCharacters returns local data when not from pagination`() =
        runBlocking {
            val expectedResult = Either.Right(success = mockCharactersDomain())
            val expectedServerResponse = Either.Right(success = mockCharactersLocalData())
            whenever(mockCharactersDataSourceLocal.getCharacters()).thenReturn(
                expectedServerResponse
            )

            val result = charactersRepositoryImp.getCharacters(fromPagination = false)

            assertEquals(expectedResult, result)
        }

    @Test
    fun `getCharacters returns service data when not from pagination`() =
        runBlocking {
            val expectedResult = Either.Right(success = mockCharactersDomain())
            val charactersLocal = Either.Right(success = null)
            val charactersService = Either.Right(success = mockCharactersServiceData())
            whenever(
                mockCharactersDataSourceLocal.getOffset()
            ).thenReturn(Either.Right(success = 0))
            whenever(
                mockCharactersDataSourceLocal.getCharacters()
            ).thenReturn(charactersLocal)
            whenever(
                mockCharactersDataSourceService.getCharacters(
                    fromPagination = false,
                    offset = charactersRepositoryImp.calculateOffset()
                )
            ).thenReturn(charactersService)

            val result = charactersRepositoryImp.getCharacters(fromPagination = false)

            assertEquals(expectedResult, result)
        }

    @Test
    fun `after updateCharacters call to getCharacters returns local data when not from pagination`() =
        runBlocking {
            val expectedResult = Either.Right(success = mockCharactersLocalData())
            mockCharactersDataSourceLocal.saveCharacters(mockCharactersLocalData())
            val charactersLocal = Either.Right(success = mockCharactersDomain())
            whenever(mockCharactersDataSourceLocal.getCharacters()).thenReturn(expectedResult)

            val result = charactersRepositoryImp.getCharacters(fromPagination = false)

            assertEquals(charactersLocal, result)
        }


}