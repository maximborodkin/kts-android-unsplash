package ru.maxim.unsplash.model.response

import ru.maxim.unsplash.model.Collection

data class CollectionsSearchResponse (
    val total: Int,
    val pages: Int,
    val results: ArrayList<Collection>
)