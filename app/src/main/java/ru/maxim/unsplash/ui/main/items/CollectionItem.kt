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
) : BaseMainListItem() {
    companion object {
        @JvmStatic
        fun fromCollection(collection: Collection): CollectionItem =
            CollectionItem(
                id = collection.id,
                title = collection.title,
                description = collection.description,
                totalPhotos = collection.totalPhotos,
                cover = collection.coverPhoto?.urls?.regular,
                coverHash = collection.coverPhoto?.blurHash,
                authorName = collection.user?.name,
                authorAvatar = collection.user?.profileImage?.small
            )
    }
}