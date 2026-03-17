package com.davidups.characters.domain.usecases

import com.davidups.characters.domain.models.Character
import com.davidups.characters.domain.models.mockCharactersList
import com.davidups.characters.domain.models.mockCharactersWithData
import com.davidups.characters.domain.repository.CharactersRepository
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Tests exhaustivos para GetCharactersUseCase
 * Cubre todos los escenarios posibles:
 * - Casos de éxito (con ordenamiento)
 * - Casos de error (todos los tipos de Failure)
 * - Casos edge (listas vacías, nulls, ordenamiento)
 */
@ExperimentalCoroutinesApi
class GetCharactersUseCaseTest {

    private lateinit var useCase: GetCharactersUseCase
    private val repository: CharactersRepository = mock()

    @Before
    fun setUp() {
        useCase = GetCharactersUseCase(repository)
    }

    // ========== CASOS DE ÉXITO ==========

    @Test
    fun `invoke with fromPagination false should call repository with false and return sorted characters`() = runTest {
        // Given
        val unsortedCharacters = mockCharactersWithData(
            results = mockCharactersList(5, 1, 3, 2, 4)
        )
        whenever(repository.getCharacters(false)).thenReturn(Either.Right(unsortedCharacters))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        verify(repository).getCharacters(false)
        assertTrue(result.isRight)
        
        val characters = (result as Either.Right).success
        val sortedIds = characters.results?.map { it.id } ?: emptyList()
        assertEquals(listOf(5, 4, 3, 2, 1), sortedIds)
    }

    @Test
    fun `invoke with fromPagination true should call repository with true and return sorted characters`() = runTest {
        // Given
        val unsortedCharacters = mockCharactersWithData(
            results = mockCharactersList(10, 6, 8, 7, 9)
        )
        whenever(repository.getCharacters(true)).thenReturn(Either.Right(unsortedCharacters))

        // When
        val result = useCase.invoke(fromPagination = true)

        // Then
        verify(repository).getCharacters(true)
        assertTrue(result.isRight)
        
        val characters = (result as Either.Right).success
        val sortedIds = characters.results?.map { it.id } ?: emptyList()
        assertEquals(listOf(10, 9, 8, 7, 6), sortedIds)
    }

    @Test
    fun `invoke should sort characters by id in descending order`() = runTest {
        // Given - Lista desordenada con IDs aleatorios
        val unsortedCharacters = mockCharactersWithData(
            results = mockCharactersList(100, 50, 200, 25, 150, 75)
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(unsortedCharacters))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        val sortedIds = characters.results?.map { it.id } ?: emptyList()
        
        // Verificar orden descendente
        assertEquals(listOf(200, 150, 100, 75, 50, 25), sortedIds)
        
        // Verificar que está ordenado descendentemente
        val isDescending = sortedIds.zipWithNext().all { (a, b) -> a!! >= b!! }
        assertTrue("IDs should be in descending order", isDescending)
    }

    @Test
    fun `invoke should maintain all character properties after sorting`() = runTest {
        // Given
        val characters = mockCharactersWithData(
            offset = 20,
            limit = 10,
            total = 100,
            count = 3,
            results = mockCharactersList(3, 1, 2)
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(characters))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val resultCharacters = (result as Either.Right).success
        
        // Verificar que se mantienen las propiedades del wrapper
        assertEquals(20, resultCharacters.offset)
        assertEquals(10, resultCharacters.limit)
        assertEquals(100, resultCharacters.total)
        assertEquals(3, resultCharacters.count)
        
        // Verificar que se mantienen las propiedades de cada personaje
        resultCharacters.results?.forEach { character ->
            assertNotNull("Character should have id", character.id)
            assertNotNull("Character should have name", character.name)
            assertNotNull("Character should have description", character.description)
        }
    }

    // ========== CASOS EDGE ==========

    @Test
    fun `invoke with empty results should return empty sorted list`() = runTest {
        // Given
        val emptyCharacters = mockCharactersWithData(results = emptyList())
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(emptyCharacters))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        assertNotNull(characters.results)
        assertTrue("Results should be empty", characters.results!!.isEmpty())
    }

    @Test
    fun `invoke with null results should return null sorted results`() = runTest {
        // Given
        val charactersWithNullResults = mockCharactersWithData(results = null)
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(charactersWithNullResults))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        assertEquals(null, characters.results)
    }

    @Test
    fun `invoke with single character should return same character`() = runTest {
        // Given
        val singleCharacter = mockCharactersWithData(
            results = mockCharactersList(42)
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(singleCharacter))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        assertEquals(1, characters.results?.size)
        assertEquals(42, characters.results?.first()?.id)
    }

    @Test
    fun `invoke with characters already sorted descending should maintain order`() = runTest {
        // Given - Ya ordenados descendentemente
        val sortedCharacters = mockCharactersWithData(
            results = mockCharactersList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1)
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(sortedCharacters))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        val ids = characters.results?.map { it.id }
        assertEquals(listOf(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), ids)
    }

    @Test
    fun `invoke with characters sorted ascending should reverse order`() = runTest {
        // Given - Ordenados ascendentemente
        val ascendingCharacters = mockCharactersWithData(
            results = mockCharactersList(1, 2, 3, 4, 5)
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(ascendingCharacters))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        val ids = characters.results?.map { it.id }
        assertEquals(listOf(5, 4, 3, 2, 1), ids)
    }

    @Test
    fun `invoke with duplicate character ids should maintain all duplicates in sorted order`() = runTest {
        // Given - IDs duplicados
        val duplicateIds = listOf(5, 3, 5, 1, 3, 5)
        val charactersWithDuplicates = mockCharactersWithData(
            results = duplicateIds.map { id ->
                Character(
                    id = id,
                    name = "Character $id",
                    description = "Description",
                    modified = null,
                    resourceURI = null,
                    characterImage = null
                )
            }
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(charactersWithDuplicates))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        val sortedIds = characters.results?.map { it.id }
        
        // Debería ordenar: 5, 5, 5, 3, 3, 1
        assertEquals(6, sortedIds?.size)
        assertEquals(5, sortedIds?.get(0))
        assertEquals(5, sortedIds?.get(1))
        assertEquals(5, sortedIds?.get(2))
        assertEquals(3, sortedIds?.get(3))
        assertEquals(3, sortedIds?.get(4))
        assertEquals(1, sortedIds?.get(5))
    }

    @Test
    fun `invoke with characters having null ids should handle gracefully`() = runTest {
        // Given - Algunos personajes con ID null
        val charactersWithNullIds = mockCharactersWithData(
            results = listOf(
                Character(id = 5, name = "Hero 5", description = null, modified = null, resourceURI = null, characterImage = null),
                Character(id = null, name = "Hero null", description = null, modified = null, resourceURI = null, characterImage = null),
                Character(id = 3, name = "Hero 3", description = null, modified = null, resourceURI = null, characterImage = null),
                Character(id = null, name = "Hero null 2", description = null, modified = null, resourceURI = null, characterImage = null)
            )
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(charactersWithNullIds))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val characters = (result as Either.Right).success
        assertEquals(4, characters.results?.size)
        
        // Los null deberían ir al final
        val ids = characters.results?.map { it.id }
        assertEquals(5, ids?.get(0))
        assertEquals(3, ids?.get(1))
        assertEquals(null, ids?.get(2))
        assertEquals(null, ids?.get(3))
    }

    // ========== CASOS DE ERROR ==========

    @Test
    fun `invoke should propagate NetworkConnection failure from repository`() = runTest {
        // Given
        whenever(repository.getCharacters(any())).thenReturn(Either.Left(Failure.NetworkConnection))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isLeft)
        assertEquals(Failure.NetworkConnection, (result as Either.Left).error)
    }

    @Test
    fun `invoke should propagate ServerError failure from repository`() = runTest {
        // Given
        val serverError = Failure.ServerError(500, "Internal Server Error")
        whenever(repository.getCharacters(any())).thenReturn(Either.Left(serverError))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isLeft)
        val error = (result as Either.Left).error as Failure.ServerError
        assertEquals(500, error.errorCode)
        assertEquals("Internal Server Error", error.message)
    }

    @Test
    fun `invoke should propagate Throwable failure from repository`() = runTest {
        // Given
        val throwable = Failure.Throwable(RuntimeException("Unexpected error"))
        whenever(repository.getCharacters(any())).thenReturn(Either.Left(throwable))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isLeft)
        val error = (result as Either.Left).error as Failure.Throwable
        assertEquals("Unexpected error", error.throwable?.message)
    }

    @Test
    fun `invoke should propagate CustomError failure from repository`() = runTest {
        // Given
        val customError = Failure.CustomError(404, "Resource not found")
        whenever(repository.getCharacters(any())).thenReturn(Either.Left(customError))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isLeft)
        val error = (result as Either.Left).error as Failure.CustomError
        assertEquals(404, error.errorCode)
        assertEquals("Resource not found", error.errorMessage)
    }

    @Test
    fun `invoke with different error codes should maintain error details`() = runTest {
        // Test múltiples códigos de error
        val errorCodes = listOf(400, 401, 403, 404, 500, 502, 503)
        
        errorCodes.forEach { code ->
            // Given
            val serverError = Failure.ServerError(code, "Error $code")
            whenever(repository.getCharacters(any())).thenReturn(Either.Left(serverError))

            // When
            val result = useCase.invoke(fromPagination = false)

            // Then
            assertTrue("Should be Left for error code $code", result.isLeft)
            val error = (result as Either.Left).error as Failure.ServerError
            assertEquals(code, error.errorCode)
            assertEquals("Error $code", error.message)
        }
    }

    // ========== CASOS DE INTEGRACIÓN ==========

    @Test
    fun `invoke multiple times should call repository each time`() = runTest {
        // Given
        val characters = mockCharactersWithData(results = mockCharactersList(1, 2, 3))
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(characters))

        // When - Llamar 3 veces
        repeat(3) {
            useCase.invoke(fromPagination = false)
        }

        // Then
        verify(repository, org.mockito.kotlin.times(3)).getCharacters(false)
    }

    @Test
    fun `invoke should work correctly after previous error`() = runTest {
        // Given
        whenever(repository.getCharacters(false))
            .thenReturn(Either.Left(Failure.NetworkConnection))
            .thenReturn(Either.Right(mockCharactersWithData(results = mockCharactersList(1, 2))))

        // When - Primera llamada falla
        val errorResult = useCase.invoke(fromPagination = false)
        
        // Then
        assertTrue(errorResult.isLeft)

        // When - Segunda llamada tiene éxito
        val successResult = useCase.invoke(fromPagination = false)
        
        // Then
        assertTrue(successResult.isRight)
        val characters = (successResult as Either.Right).success
        assertEquals(2, characters.results?.size)
    }

    @Test
    fun `invoke with large dataset should sort correctly`() = runTest {
        // Given - Dataset grande
        val largeList = (1..1000).shuffled().take(100)
        val characters = mockCharactersWithData(
            results = largeList.map { id ->
                Character(
                    id = id,
                    name = "Character $id",
                    description = null,
                    modified = null,
                    resourceURI = null,
                    characterImage = null
                )
            }
        )
        whenever(repository.getCharacters(any())).thenReturn(Either.Right(characters))

        // When
        val result = useCase.invoke(fromPagination = false)

        // Then
        assertTrue(result.isRight)
        val sortedCharacters = (result as Either.Right).success
        val ids = sortedCharacters.results?.map { it.id } ?: emptyList()
        
        // Verificar que está ordenado
        val expectedSorted = largeList.sortedDescending()
        assertEquals(expectedSorted, ids)
    }
}
