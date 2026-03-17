package com.davidups.marvel.ui.features.character.models

sealed interface CharactersEffect {
    data class NavigateToDetail(val character: CharacterView) : CharactersEffect
}
