package com.davidups.marvel.ui.features.character.models

import com.davidups.characters.domain.models.Characters
import com.davidups.core.extensions.empty
import com.davidups.core.extensions.orEmpty

data class CharactersView(
    val offset: Int,
    val results: List<CharacterView>
)

fun Characters.toView() =
    CharactersView(this.offset.orEmpty(), this.results?.map { it.toView() }.orEmpty())
