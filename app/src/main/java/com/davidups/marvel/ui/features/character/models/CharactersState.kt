package com.davidups.marvel.ui.features.character.models

data class CharactersUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val characters: CharactersView? = null,
)

