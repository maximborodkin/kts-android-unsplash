package ru.maxim.unsplash.network.model.response

import com.google.gson.annotations.SerializedName
import ru.maxim.unsplash.network.model.CollectionDto
import ru.maxim.unsplash.network.model.PhotoDto
import ru.maxim.unsplash.network.model.UserDto

data class AddPhotoToCollectionResponse(
    val photo: PhotoDto,
    val collection: CollectionDto,
    val user: UserDto,
    @SerializedName("created_at")
    val createdAt: String
)