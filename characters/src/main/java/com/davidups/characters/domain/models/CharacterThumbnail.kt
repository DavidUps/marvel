package com.davidups.characters.domain.models

data class CharacterThumbnail(val path: String?, val extension: String?, val image: String?) {
    constructor(path: String, extension: String) : this(path, extension, null)
    constructor(image: String) : this(null, null, image)

    fun image() = "${this.path?.replace("http", "https")}.$extension"

    companion object {
        fun thumbail(image: String): CharacterThumbnail {
            val imageSplit = image.split(".")
            return CharacterThumbnail(imageSplit[0], imageSplit[1])
        }
    }
}
