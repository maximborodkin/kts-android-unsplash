package ru.maxim.unsplash.ui.feed.items.mappers

import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.ui.feed.items.CollectionItem

fun Collection.mapToItem(): CollectionItem =
    CollectionItem(
        id = id,
        title = title,
        description = description,
        totalPhotos = totalPhotos,
        cover = coverPhoto?.urls?.regular,
        coverHash = coverPhoto?.blurHash,
        authorName = user?.name,
        authorAvatar = user?.profileImage?.small
    )