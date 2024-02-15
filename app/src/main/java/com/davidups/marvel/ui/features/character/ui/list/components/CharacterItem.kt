package com.davidups.marvel.ui.features.character.ui.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidups.design.components.text.ExpandableText
import com.davidups.marvel.R

@Composable
fun CharacterItem(image: String, name: String, description: String, onItemClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .clickable {
                onItemClick()
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {

        var expandDescription by rememberSaveable {
            mutableStateOf(false)
        }
        AsyncImage(
            ImageRequest.Builder(LocalContext.current).data(image).crossfade(true).build(),
            stringResource(R.string.character_image),
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop,
        )
        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            ),
            text = name,
            style = MaterialTheme.typography.headlineMedium,
        )

        ExpandableText(
            modifier = Modifier.padding(
                start = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
            text = description,
            state = expandDescription,
            minLines = 2,
            style = MaterialTheme.typography.bodyMedium
        )

        CharacterMoreButton(expandDescription, description) {
            expandDescription = expandDescription.not()
        }

    }
}


@Preview(
    showBackground = true, device = Devices.PIXEL_4
)
@Composable
fun CharactersListPreview() {
    CharacterItem(image = "https://i.annihil.us/u/prod/marvel/i/mg/6/20/52602f21f29ec.jpg",
        name = "Spiderman",
        description = "Rick Jones has been Hulk's best bud since day one, but now he's more than a friend...he's a teammate! Transformed by a Gamma energy explosion, A-Bomb's thick, armored skin is just as strong and powerful as it is blue. And when he curls into action, he uses it like a giant bowling ball of destruction!",
        onItemClick = {})
}