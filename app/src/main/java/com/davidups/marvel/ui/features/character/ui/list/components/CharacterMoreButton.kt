package com.davidups.marvel.ui.features.character.ui.list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.davidups.core.extensions.empty
import com.davidups.marvel.R

@Composable
fun CharacterMoreButton(
    expandDescription: Boolean,
    description: String,
    onClick: () -> Unit
) {
    var buttonText by rememberSaveable {
        mutableStateOf(String.empty())
    }
    buttonText = getButtonText(expandDescription)

    AnimatedVisibility(visible = description.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End

        ) {
            Button(
                onClick = { onClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                AnimatedContent(targetState = buttonText, label = buttonText) {
                    Text(text = it)
                }
            }
        }
    }
}

@Composable
fun getButtonText(expandDescription: Boolean): String {
    return if (expandDescription)
        stringResource(R.string.see_less)
    else
        stringResource(R.string.see_more)
}
