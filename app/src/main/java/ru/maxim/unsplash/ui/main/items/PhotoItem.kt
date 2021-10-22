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
) : BaseMainListItem() {
    companion object {
        @JvmStatic
        fun fromPhoto(photo: Photo): PhotoItem =
            PhotoItem(
                id = photo.id,
                width = photo.width,
                height = photo.height,
                regular = photo.urls.regular,
                raw = photo.urls.raw,
                thumbnail = photo.urls.thumb,
                blurHash = photo.blurHash,
                likesCount = photo.likes,
                likedByUser = photo.likedByUser,
                authorName = photo.user?.name,
                authorAvatar = photo.user?.profileImage?.small
            )
    }
}