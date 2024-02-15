package com.davidups.characters.data.service

import com.davidups.characters.data.models.mockCharacterService
import com.davidups.characters.data.models.mockCharactersService
import com.davidups.core.platform.service.Request
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CharacterServiceTest {

    @Mock
    private lateinit var mockCharacterApi: CharacterApi

    @Mock
    private lateinit var mockRequest: Request

    @Mock
    private lateinit var characterApi: CharacterApi

    private lateinit var characterService: CharacterService

    @Before
    fun setUp() {
        characterService = CharacterService(mockCharacterApi, mockRequest)
    }

    @Test
    fun `getCharacters returns expected result`() = runBlocking {
        val expected = mockCharactersService()
        whenever(mockRequest.launch(characterApi.getCharacters(10))).thenReturn(
            mockCharactersService()
        )

        val result = characterService.getCharacters(10, 0)

        assert(result == expected)
    }

    @Test
    fun `getCharacter returns expected result`() = runBlocking {
        val expected = mockCharacterService()
        whenever(mockRequest.launch(characterApi.getCharacter("1"))).thenReturn(expected)

        val result = characterService.getCharacter("1")

        assert(result == expected)
    }
}