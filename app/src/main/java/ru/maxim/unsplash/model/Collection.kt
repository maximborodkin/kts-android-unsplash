package ru.maxim.unsplash.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    @SerializedName("published_at") val publishedAt: Date?,
    @SerializedName("updated_at") val updatedAt: Date?,
    @SerializedName("total_photos") val totalPhotos: Int,
    @SerializedName("private") val isPrivate: Boolean,
    @SerializedName("share_key") val shareKey: String,
    @SerializedName("cover_photo") val coverPhoto: Photo?,
    val user: User?,
    val links: Links,
) {
    data class Links(
        val self: String,
        val html: String,
        val photos: String,
        val related: String
    )
}
