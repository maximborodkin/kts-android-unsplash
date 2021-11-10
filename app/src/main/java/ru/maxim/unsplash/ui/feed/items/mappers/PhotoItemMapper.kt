package ru.maxim.unsplash.ui.feed.items.mappers

import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.ui.feed.items.PhotoItem

fun Photo.mapToItem(): PhotoItem =
    PhotoItem(
        id = id,
        width = width,
        height = height,
        regular = urls.regular,
        raw = urls.raw,
        thumbnail = urls.thumb,
        blurHash = blurHash,
        likesCount = likes,
        likedByUser = likedByUser,
        authorUsername = user.username,
        authorName = user.name,
        authorAvatar = user.profileImage.small
    )