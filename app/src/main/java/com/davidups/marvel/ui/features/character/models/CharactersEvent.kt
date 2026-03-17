package com.davidups.marvel.ui.features.character.models

sealed interface CharactersIntent {
    data class LoadCharacters(val fromPagination: Boolean = false) : CharactersIntent
    data class CharacterClicked(val character: CharacterView) : CharactersIntent
    data object ErrorDismissed : CharactersIntent
}