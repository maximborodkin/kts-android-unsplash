package ru.maxim.unsplash.ui.main.items

data class PhotoItem (
    val id: String,
    val width: Int,
    val height: Int,
    val source: String,
    val thumbnail: String,
    val likesCount: Int,
    val likedByUser: Boolean,
    var authorName: String,
    val authorAvatar: String?
)