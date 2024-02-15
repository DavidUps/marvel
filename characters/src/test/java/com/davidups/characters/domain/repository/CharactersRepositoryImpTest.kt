package com.davidups.characters.domain.repository

import com.davidups.characters.data.datasource.CharactersDataSourceService
import com.davidups.characters.data.models.mockCharactersData
import com.davidups.characters.domain.models.toDomain
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CharactersRepositoryImpTest {

    @Mock
    private lateinit var mockCharactersDataSource: CharactersDataSourceService

    private lateinit var charactersRepositoryImp: CharactersRepositoryImp

    @Before
    fun setUp() {
        charactersRepositoryImp = CharactersRepositoryImp(mockCharactersDataSource)
    }

    @Test
    fun `getCharacters returns expected result`() = runBlocking {
        val expected = Either.Right(mockCharactersData().toDomain())
        whenever(mockCharactersDataSource.getCharacters(any())).thenReturn(
            Either.Right(mockCharactersData())
        )

        val result = charactersRepositoryImp.getCharacters(fromPagination = false)

        assert(result == expected)
    }

    @Test
    fun `getCharacters handles Failure`() = runBlocking {
        val expected = Either.Left(Failure.Throwable(Throwable("Test Error")))
        whenever(mockCharactersDataSource.getCharacters(false)).thenReturn(expected)

        val result = charactersRepositoryImp.getCharacters(fromPagination = false)

        assert(result == expected)
    }
}