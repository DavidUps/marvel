package com.davidups.marvel.features.characters

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.davidups.characters.domain.models.Characters
import com.davidups.characters.domain.usecases.GetCharacterUseCase
import com.davidups.core.exception.Failure
import com.davidups.core.exception.FailureView
import com.davidups.core.functional.Either
import com.davidups.marvel.LogWrapper
import com.davidups.marvel.features.CoroutineTestRule
import com.davidups.marvel.ui.features.character.models.toView
import com.davidups.marvel.ui.features.character.viewmodels.CharactersViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever


class CharactersViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesRule = CoroutineTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var getCharactersUseCase = mock<GetCharacterUseCase>()

    @Mock
    private lateinit var logWrapper: LogWrapper

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testLogError() {
        `when`(logWrapper.e(anyString(), anyString())).thenReturn(0)
    }

    @Test
    fun `when GetCharacters event is dispatched, getCharacters function is called`() =
        coroutinesRule.dispatcher.runBlockingTest {

            val viewModel = CharactersViewModel(getCharactersUseCase)
            verify(getCharactersUseCase).invoke(false)
        }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `should emit get characters when is success`() = coroutinesRule.dispatcher.runBlockingTest {
        val channel = Channel<Either<FailureView, Characters>>()
        val flow = channel.consumeAsFlow()

        doReturn(flow)
            .whenever(getCharactersUseCase)
            .invoke(false)

        val job = launch {
            channel.send(mockCharactersResponseSuccess)
        }

        val viewModel = CharactersViewModel(getCharactersUseCase)

        verify(getCharactersUseCase, times(1)).invoke(false)
        assertEquals(viewModel.state.characters, characters.toView())

        job.cancel()
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `should emit get characters when is error`() = coroutinesRule.dispatcher.runBlockingTest {
        val channel = Channel<Either<Failure, Characters>>()
        val flow = channel.consumeAsFlow()

        doReturn(flow)
            .whenever(getCharactersUseCase)
            .invoke(false)

        val job = launch {
            channel.send(mockCharactersResponseError)
        }

        val viewModel = CharactersViewModel(getCharactersUseCase)

        verify(getCharactersUseCase, times(1)).invoke(false)
        assertEquals(viewModel.state.error, 2131427384)

        job.cancel()
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `should emit get characters when catch`() = coroutinesRule.dispatcher.runBlockingTest {
        val channel = Channel<Either<Failure, Characters>>()
        val flow = channel.consumeAsFlow()

        doReturn(flow)
            .whenever(getCharactersUseCase)
            .invoke(false)

        val job = launch {
            channel.close(Throwable())
        }

        val viewModel = CharactersViewModel(getCharactersUseCase)

        verify(getCharactersUseCase, times(1)).invoke(false)

        assertEquals(viewModel.state.error,2131427381)

        job.cancel()
    }

    companion object {
        val characters = Characters.empty()
        val mockCharactersResponseSuccess = Either.Right(characters)
        private val error = Failure.NetworkConnection
        val mockCharactersResponseError = Either.Left(error)
    }
}