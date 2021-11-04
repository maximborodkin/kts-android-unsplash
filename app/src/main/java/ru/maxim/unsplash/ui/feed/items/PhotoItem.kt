package ru.maxim.unsplash.ui.feed.items

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
    val authorUsername: String,
    var authorName: String?,
    val authorAvatar: String?
) : BaseFeedListItem()