package com.davidups.marvel.ui.features.character.models

import android.os.Parcelable
import com.davidups.characters.domain.models.Character
import com.davidups.core.extensions.empty
import com.davidups.core.extensions.orEmpty
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CharacterView(
    val id: Int,
    val name: String,
    val description: String,
    val modified: String?,
    val resourceURI: String,
    val image: String
) : Parcelable

fun Character.toView() =
    CharacterView(
        this.id.orEmpty(),
        this.name.orEmpty(),
        this.description.orEmpty(),
        this.modified,
        this.resourceURI.orEmpty(),
        this.characterImage?.image().orEmpty()
    )
