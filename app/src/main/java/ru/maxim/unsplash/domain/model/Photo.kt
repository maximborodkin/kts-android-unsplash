package ru.maxim.unsplash.domain.model

import java.util.Date

data class Photo(
    val id: String,
    val createdAt: Date,
    val updatedAt: Date,
    val width: Int,
    val height: Int,
    val color: String?,
    val blurHash: String,
    var likes: Int,
    var likedByUser: Boolean,
    val description: String?,
    val exif: Exif?,
    val location: Location?,
    val tags: List<Tag>?,
    val user: User,
    val urls: Urls,
    val links: Links
)