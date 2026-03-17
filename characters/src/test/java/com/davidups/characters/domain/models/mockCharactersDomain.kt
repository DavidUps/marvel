package com.davidups.characters.domain.models

fun mockCharactersDomain(): Characters = Characters.empty()

fun mockCharacterDomain(): Character = Character.empty()

fun mockCharactersWithData(
    offset: Int = 0,
    limit: Int = 20,
    total: Int = 100,
    count: Int = 20,
    results: List<Character> = emptyList()
): Characters = Characters(
    offset = offset,
    limit = limit,
    total = total,
    count = count,
    results = results
)

fun mockCharacterWithData(
    id: Int = 1,
    name: String = "Spider-Man",
    description: String = "Friendly neighborhood hero",
    modified: String = "2023-01-01T00:00:00-0500",
    resourceURI: String = "http://gateway.marvel.com/v1/public/characters/1",
    thumbnail: CharacterThumbnail? = mockCharacterThumbnail()
): Character = Character(
    id = id,
    name = name,
    description = description,
    modified = modified,
    resourceURI = resourceURI,
    characterImage = thumbnail
)

fun mockCharacterThumbnail(
    path: String = "http://i.annihil.us/u/prod/marvel/i/mg/3/50/526548a343e4b",
    extension: String = "jpg"
): CharacterThumbnail = CharacterThumbnail(
    path = path,
    extension = extension
)

fun mockCharactersList(vararg ids: Int): List<Character> =
    ids.map { id ->
        mockCharacterWithData(
            id = id,
            name = "Character $id",
            description = "Description for character $id"
        )
    }
