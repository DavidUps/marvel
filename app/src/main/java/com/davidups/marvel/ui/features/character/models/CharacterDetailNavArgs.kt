package com.davidups.marvel.ui.features.character.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CharacterDetailNavArgs(val characterView: CharacterView) : Parcelable