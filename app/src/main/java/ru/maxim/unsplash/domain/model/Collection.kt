package ru.maxim.unsplash.domain.model

import java.util.Date

data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    val publishedAt: Date?,
    val updatedAt: Date?,
    val totalPhotos: Int,
    val isPrivate: Boolean,
    val shareKey: String?,
    val coverPhoto: Photo?,
    val user: User?,
    val links: Links,
)
