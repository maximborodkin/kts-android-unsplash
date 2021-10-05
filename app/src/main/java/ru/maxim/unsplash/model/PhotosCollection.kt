package ru.maxim.unsplash.model

import com.google.gson.annotations.SerializedName

data class PhotosCollection(
    val id: String,
    val title: String,
    val description: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("cover_photo")
    val coverPhoto: Photo,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    val user: User,
    val links: Links?
) {
    data class Links (
        val self: String,
        val html: String,
        val photos: String,
        val related: String
    )
}
