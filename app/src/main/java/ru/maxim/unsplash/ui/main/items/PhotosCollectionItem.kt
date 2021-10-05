package ru.maxim.unsplash.ui.main.items

data class PhotosCollectionItem (
    val id: Long,
    val title: String,
    val description: String?,
    val totalPhotos: Int,
    val cover: String,
    val authorName: String,
    val authorAvatar: String,
)