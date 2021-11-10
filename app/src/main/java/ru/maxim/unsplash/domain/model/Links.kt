package ru.maxim.unsplash.domain.model

data class Links(
    val self: String,
    val html: String,
    val photos: String?,
    val related: String?,
    val download: String?,
    val downloadLocation: String?,
    val likes: String?,
    val portfolio: String?
)