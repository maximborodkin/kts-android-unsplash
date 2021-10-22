package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class CollectionDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("published_at")
    val publishedAt: Date?,

    @SerializedName("updated_at")
    val updatedAt: Date?,

    @SerializedName("total_photos")
    val totalPhotos: Int,

    @SerializedName("private")
    val isPrivate: Boolean,

    @SerializedName("share_key")
    val shareKey: String,

    @SerializedName("cover_photo")
    val coverPhoto: PhotoDto?,

    @SerializedName("user")
    val user: UserDto?,

    @SerializedName("links")
    val links: LinksDto,
)
