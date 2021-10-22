package ru.maxim.unsplash.network.model.response

import ru.maxim.unsplash.network.model.PhotoDto
import ru.maxim.unsplash.network.model.UserDto

data class LikeResponse(
    val photo: PhotoDto,
    val user: UserDto
)