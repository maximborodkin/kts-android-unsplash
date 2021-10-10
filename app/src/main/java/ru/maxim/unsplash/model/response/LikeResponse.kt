package ru.maxim.unsplash.model.response

import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.model.User

data class LikeResponse(
    val photo: Photo,
    val user: User
)