package ru.maxim.unsplash.model

data class PhotosCollection(
    val id: Long,
    val title: String,
    val publishedAt: String,
    val lastCollectedAt: String,
    val updatedAt: String,
    val coverPhoto: Photo,
    val totalPhotos: Int,
    val user: User,
    val links: PhotoLinks?
)
