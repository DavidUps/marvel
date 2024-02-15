package com.davidups.marvel.ui.features.character.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidups.characters.domain.usecases.GetCharacterUseCase
import com.davidups.core.exception.Failure
import com.davidups.core.extensions.cancelIfActive
import com.davidups.core.extensions.onFailure
import com.davidups.core.extensions.onSuccess
import com.davidups.marvel.ui.features.character.models.CharactersEvent
import com.davidups.marvel.ui.features.character.models.CharactersState
import com.davidups.marvel.ui.features.character.models.toView
import com.davidups.marvel.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharacters: GetCharacterUseCase
) : ViewModel() {

    private var getCharactersJob: Job? = null

    var state by mutableStateOf(CharactersState())
        private set

    init {
        dispatch(CharactersEvent.GetCharacters(false))
    }

    private fun dispatch(event: CharactersEvent) {
        when (event) {
            is CharactersEvent.GetCharacters -> getCharacters(event.fromPagination)
        }
    }

    private fun getCharacters(fromPagination: Boolean) {
        getCharactersJob.cancelIfActive()
        getCharactersJob = viewModelScope.launch {
            getCharacters.invoke(fromPagination)
                .onStart {
                    state = state.copy(isLoading = true)
                }
                .onCompletion {
                    state = state.copy(isLoading = false)
                }
                .catch { throwable ->
                    Log.e("CharactersViewModel", "getCharacters catch: ${throwable.message}")
                    state = state.copy(error = R.string.get_characters_error)
                }
                .collect { result ->
                    result.onFailure { failure ->
                        Log.e("CharactersViewModel", "getCharacters collect error: ${failure}")
                        state = state.copy(
                            error = when (failure) {
                                is Failure.ServerError,
                                is Failure.Throwable,
                                is Failure.CustomError -> R.string.get_characters_error
                                Failure.NetworkConnection -> R.string.internet_error
                            }
                        )
                    }
                    result.onSuccess { characters ->
                        state = state.copy(characters = characters.toView())
                    }
                }
        }
    }
}
