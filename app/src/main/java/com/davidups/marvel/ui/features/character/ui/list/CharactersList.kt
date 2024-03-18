package com.davidups.marvel.ui.features.character.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.davidups.design.components.loader.Loader
import com.davidups.marvel.R
import com.davidups.marvel.ui.features.character.models.CharacterView
import com.davidups.marvel.ui.features.character.models.CharactersEvent
import com.davidups.marvel.ui.features.character.models.CharactersState
import com.davidups.marvel.ui.features.character.ui.list.components.CharacterItem
import com.davidups.marvel.ui.features.character.ui.list.components.CharactersList
import com.davidups.marvel.ui.features.character.viewmodels.CharactersViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

@Composable
fun CharactersList(viewModel: CharactersViewModel) {
    Box {
        val state = viewModel.state
        when {
            state.isLoading -> {
                Loader(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                        .background(Color.White),
                )
            }

            state.characters?.results.isNullOrEmpty().not() -> {
                CharactersList(
                    state = viewModel.state,
                    onItemClick = { character ->
                        viewModel.event.update { CharactersEvent.ClickCharacterDetail(character) }
                    }
                )
            }

            state.error != null -> {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    text = stringResource(id = state.error),
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    text = stringResource(R.string.no_characters_found),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun CharactersListPreview() {
    CharactersList(state = CharactersState(), onItemClick = {})
}
