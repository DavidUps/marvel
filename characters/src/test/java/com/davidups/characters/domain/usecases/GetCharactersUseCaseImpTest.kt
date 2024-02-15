package com.davidups.characters.domain.usecases

import com.davidups.characters.domain.models.mockCharactersDomain
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetCharactersUseCaseImpTest {

    private lateinit var useCase: GetCharactersUseCaseImp
    private val repository: CharactersRepository = mock()

    @Before
    fun setUp() {
        useCase = GetCharactersUseCaseImp(repository)
    }

    @Test
    fun `invoke() should emit successful characters from repository and sort them`() = runTest {
        val characters = mockCharactersDomain()
        whenever(repository.getCharacters(false)).thenReturn(Either.Right(characters))

        useCase.invoke(fromPagination = false).collect { result ->
            result as Either.Right
            assertEquals(result.success, characters)
        }

        verify(repository).getCharacters(false)
    }

    @Test
    fun `invoke() should emit error if repository fails`() = runTest {
        val failure = Failure.NetworkConnection
        whenever(repository.getCharacters(true)).thenReturn(Either.Left(failure))

        useCase.invoke(fromPagination = true).collect { result ->
            result as Either.Left
            assertEquals(result.error, failure)
        }


        verify(repository).getCharacters(true)
    }
}
