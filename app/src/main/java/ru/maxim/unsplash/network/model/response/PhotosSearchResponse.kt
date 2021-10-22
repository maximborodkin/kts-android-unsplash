package ru.maxim.unsplash.network.model.response

import ru.maxim.unsplash.network.model.PhotoDto

data class PhotosSearchResponse(
    val total: Int,
    val pages: Int,
    val results: List<PhotoDto>
)