package ru.maxim.unsplash.model

data class Photo(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val width: Int,
    val height: Int,
    val color: String?,
    val blurHash: String,
    var likes: Int,
    var likedByUser: Boolean,
    val description: String?,
    val user: User,
    val currentUserCollections: ArrayList<PhotosCollection>?,
    val urls: PhotoUrls?,
    val links: PhotoLinks?
)