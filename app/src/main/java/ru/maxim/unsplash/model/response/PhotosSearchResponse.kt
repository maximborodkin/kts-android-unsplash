package ru.maxim.unsplash.model.response

import ru.maxim.unsplash.model.Photo

data class PhotosSearchResponse (
    val total: Int,
    val pages: Int,
    val results: ArrayList<Photo>
)