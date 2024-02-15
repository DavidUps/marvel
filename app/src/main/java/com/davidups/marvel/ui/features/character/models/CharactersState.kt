package com.davidups.marvel.ui.features.character.models

import androidx.annotation.StringRes

data class CharactersState(
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null,
    val characters: CharactersView? = null
)


