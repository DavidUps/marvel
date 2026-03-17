package com.davidups.marvel.ui.features.character.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidups.characters.domain.usecases.GetCharactersUseCase
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.onFailure
import com.davidups.core.extensions.onSuccess
import com.davidups.marvel.ui.features.character.models.CharactersEffect
import com.davidups.marvel.ui.features.character.models.CharactersIntent
import com.davidups.marvel.ui.features.character.models.CharactersUiState
import com.davidups.marvel.ui.features.character.models.toView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private val _effect = Channel<CharactersEffect>(Channel.BUFFERED)
    val effect: Flow<CharactersEffect> = _effect.receiveAsFlow()

    init {
        onIntent(CharactersIntent.LoadCharacters(fromPagination = false))
    }

    fun onIntent(intent: CharactersIntent) {
        when (intent) {
            is CharactersIntent.LoadCharacters -> loadCharacters(intent.fromPagination)
            is CharactersIntent.CharacterClicked -> navigateToDetail(intent.character)
            is CharactersIntent.ErrorDismissed -> reduce { copy(error = null) }
        }
    }

    private fun loadCharacters(fromPagination: Boolean) {
        viewModelScope.launch {
            reduce { copy(isLoading = true, error = null) }

            getCharactersUseCase(fromPagination)
                .onSuccess { characters ->
                    reduce {
                        copy(
                            isLoading = false,
                            characters = characters.toView()
                        )
                    }
                }
                .onFailure { failure ->
                    reduce {
                        copy(
                            isLoading = false,
                            error = failure.toErrorMessage()
                        )
                    }
                }
        }
    }

    private fun navigateToDetail(character: com.davidups.marvel.ui.features.character.models.CharacterView) {
        viewModelScope.launch {
            _effect.send(CharactersEffect.NavigateToDetail(character))
        }
    }

    private inline fun reduce(block: CharactersUiState.() -> CharactersUiState) {
        _uiState.update { it.block() }
    }

    private fun Failure.toErrorMessage(): String = when (this) {
        is Failure.NetworkConnection -> "No internet connection"
        is Failure.ServerError -> "Server error: $message"
        is Failure.Throwable -> "An unexpected error occurred"
        is Failure.CustomError -> errorMessage ?: "An error occurred"
    }
}
