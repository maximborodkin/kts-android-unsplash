package ru.maxim.unsplash.ui.main.items

import ru.maxim.unsplash.domain.model.Photo

data class PhotoItem(
    val id: String,
    val width: Int,
    val height: Int,
    val regular: String?,
    val raw: String?,
    val thumbnail: String?,
    val blurHash: String?,
    var likesCount: Int,
    var likedByUser: Boolean,
    var authorName: String?,
    val authorAvatar: String?
) : BaseMainListItem()