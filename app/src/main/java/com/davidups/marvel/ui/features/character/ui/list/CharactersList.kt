package com.davidups.marvel.ui.features.character.ui.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.davidups.marvel.ui.features.character.models.CharacterView
import com.davidups.marvel.ui.features.character.models.CharactersState
import com.davidups.marvel.ui.features.character.ui.list.components.CharacterItem
import com.davidups.marvel.ui.features.character.viewmodels.CharactersViewModel

@Composable
fun CharactersList(viewModel: CharactersViewModel, onItemClick: (CharacterView) -> Unit) {
    CharactersList(state = viewModel.state, onItemClick = onItemClick)
}

@Composable
fun CharactersList(state: CharactersState, onItemClick: (CharacterView) -> Unit) {
    LazyColumn() {
        itemsIndexed(state.characters?.results.orEmpty()) { index, character ->
            CharacterItem(image = character.image,
                name = character.name,
                description = character.description,
                onItemClick = {
                    onItemClick(character)
                })
        }
    }
}
