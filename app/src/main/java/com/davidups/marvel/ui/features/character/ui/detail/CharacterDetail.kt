package com.davidups.marvel.ui.features.character.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidups.marvel.R
import com.davidups.marvel.ui.features.character.models.CharacterView

@Composable
fun CharacterDetail(item: CharacterView) {

    Column {
        AsyncImage(
            ImageRequest.Builder(LocalContext.current).data(item.image).crossfade(true).build(),
            stringResource(R.string.character_image),
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp)),
            painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop,
        )
        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            ),
            text = item.name,
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            ),
            text = item.description,
            style = MaterialTheme.typography.bodyMedium,
        )
    }


}