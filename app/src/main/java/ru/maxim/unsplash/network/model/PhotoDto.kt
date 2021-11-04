package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PhotoDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    val updatedAt: Date,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("color")
    val color: String?,

    @SerializedName("blur_hash")
    val blurHash: String,

    @SerializedName("likes")
    var likes: Int,

    @SerializedName("liked_by_user")
    var likedByUser: Boolean,

    @SerializedName("description")
    val description: String?,

    @SerializedName("exif")
    val exif: ExifDto?,

    @SerializedName("location")
    val location: LocationDto?,

    @SerializedName("tags")
    val tags: List<TagDto>?,

    @SerializedName("user")
    val user: UserDto,

    @SerializedName("urls")
    val urls: UrlsDto,

    @SerializedName("links")
    val links: LinksDto
)