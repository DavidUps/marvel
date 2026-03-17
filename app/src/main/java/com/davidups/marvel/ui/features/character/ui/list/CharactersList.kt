package com.davidups.marvel.ui.features.character.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidups.core.ui.ObserveEffect
import com.davidups.design.components.loader.Loader
import com.davidups.marvel.R
import com.davidups.marvel.core.navigation.NavControllerWrapper.navController
import com.davidups.marvel.core.navigation.Screen
import com.davidups.marvel.ui.features.character.models.CharacterDetailNavArgs
import com.davidups.marvel.ui.features.character.models.CharactersEffect
import com.davidups.marvel.ui.features.character.models.CharactersIntent
import com.davidups.marvel.ui.features.character.models.CharactersUiState
import com.davidups.marvel.ui.features.character.ui.list.components.CharactersList
import com.davidups.marvel.ui.features.character.viewmodels.CharactersViewModel

@Composable
fun CharactersListScreen(
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is CharactersEffect.NavigateToDetail -> {
                navController?.navigate(
                    Screen.CharacterDetail.createRoute(
                        CharacterDetailNavArgs(effect.character)
                    )
                )
            }
        }
    }

    CharactersListContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
internal fun CharactersListContent(
    uiState: CharactersUiState,
    onIntent: (CharactersIntent) -> Unit,
) {
    Box {
        when {
            uiState.isLoading -> {
                Loader(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                        .background(Color.White),
                )
            }

            uiState.characters?.results.isNullOrEmpty().not() -> {
                CharactersList(
                    state = uiState,
                    onItemClick = { character ->
                        onIntent(CharactersIntent.CharacterClicked(character))
                    }
                )
            }

            uiState.error != null -> {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    text = uiState.error,
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
fun CharactersListContentPreview() {
    CharactersListContent(
        uiState = CharactersUiState(),
        onIntent = {}
    )
}
