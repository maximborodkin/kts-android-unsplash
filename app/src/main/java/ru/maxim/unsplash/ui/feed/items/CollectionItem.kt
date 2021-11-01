package ru.maxim.unsplash.ui.feed.items

data class CollectionItem(
    val id: String,
    val title: String,
    val description: String?,
    val totalPhotos: Int,
    val cover: String?,
    val coverHash: String?,
    val authorName: String?,
    val authorAvatar: String?,
) : BaseFeedListItem()