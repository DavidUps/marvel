package com.davidups.marvel.features.characters

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.davidups.characters.domain.models.Characters
import com.davidups.characters.domain.usecases.GetCharactersUseCase
import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import com.davidups.marvel.features.CoroutineTestRule
import com.davidups.marvel.ui.features.character.models.CharactersEffect
import com.davidups.marvel.ui.features.character.models.CharactersIntent
import com.davidups.marvel.ui.features.character.models.toView
import com.davidups.marvel.ui.features.character.viewmodels.CharactersViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class CharactersViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = CoroutineTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCharactersUseCase: GetCharactersUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `initial state is loading with empty data`() = runTest {
        whenever(getCharactersUseCase(any())).thenReturn(mockCharactersResponseSuccess)

        val viewModel = CharactersViewModel(getCharactersUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.characters)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `on success, uiState contains characters`() = runTest {
        whenever(getCharactersUseCase(any())).thenReturn(mockCharactersResponseSuccess)

        val viewModel = CharactersViewModel(getCharactersUseCase)

        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(characters.toView(), state.characters)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `on network error, uiState shows error message`() = runTest {
        whenever(getCharactersUseCase(any())).thenReturn(mockCharactersResponseError)

        val viewModel = CharactersViewModel(getCharactersUseCase)

        viewModel.uiState.test {
            awaitItem()
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)
            assertEquals("No internet connection", errorState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        val characters = Characters.empty()
        val mockCharactersResponseSuccess = Either.Right(characters)
        private val error = Failure.NetworkConnection
        val mockCharactersResponseError = Either.Left(error)
    }
}