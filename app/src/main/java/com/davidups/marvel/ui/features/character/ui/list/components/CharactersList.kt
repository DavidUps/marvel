package com.davidups.marvel.ui.features.character.ui.list.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.davidups.marvel.R
import com.davidups.marvel.ui.features.character.models.CharacterView
import com.davidups.marvel.ui.features.character.models.CharactersState

@Composable
fun CharactersList(state: CharactersState, onItemClick: (CharacterView) -> Unit) {
    if (state.characters?.results.isNullOrEmpty()) {
        Text(text = stringResource(R.string.no_characters_found))
    } else {
        LazyColumn {
            itemsIndexed(
                items = state.characters?.results!!,
                key = { _, character -> character.id }
            ) { _, character ->
                CharacterItem(
                    image = character.image,
                    name = character.name,
                    description = character.description,
                    onItemClick = {
                        onItemClick(character)
                    })
            }
        }
    }
}