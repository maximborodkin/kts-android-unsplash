package ru.maxim.unsplash.ui.main.items

import ru.maxim.unsplash.domain.model.Collection

data class CollectionItem(
    val id: String,
    val title: String,
    val description: String?,
    val totalPhotos: Int,
    val cover: String?,
    val coverHash: String?,
    val authorName: String?,
    val authorAvatar: String?,
) : BaseMainListItem()