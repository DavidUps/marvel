package com.davidups.marvel.ui.features.character.models

sealed class CharactersEvent {
    data class GetCharacters(val fromPagination: Boolean = false) : CharactersEvent()
    data class ClickCharacterDetail(val character: CharacterView) : CharactersEvent()
}