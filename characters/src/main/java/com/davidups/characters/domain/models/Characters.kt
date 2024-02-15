package com.davidups.characters.domain.models

import com.davidups.characters.data.models.CharactersEntity
import com.davidups.core.extensions.empty

data class Characters(
    val offset: Int?,
    val limit: Int?,
    val total: Int?,
    val count: Int?,
    val results: List<Character>?,
) {
    constructor(offset: Int, results: MutableList<Character>) : this(
        offset,
        null,
        null,
        null,
        results
    )

    companion object {
        fun empty() =
            Characters(Int.empty(), Int.empty(), Int.empty(), Int.empty(), mutableListOf())
    }
}

fun CharactersEntity.toDomain(): Characters =
    Characters(this.offset, this.limit, this.total, this.count, this.results?.map { it.toDomain() })
