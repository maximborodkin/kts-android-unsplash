package ru.maxim.unsplash.network.model.response

import ru.maxim.unsplash.network.model.CollectionDto

data class CollectionsSearchResponse(
    val total: Int,
    val pages: Int,
    val results: List<CollectionDto>
)