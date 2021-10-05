package ru.maxim.unsplash.model.response

import ru.maxim.unsplash.model.PhotosCollection

data class CollectionsSearchResponse (
    val total: Int,
    val pages: Int,
    val results: ArrayList<PhotosCollection>
)