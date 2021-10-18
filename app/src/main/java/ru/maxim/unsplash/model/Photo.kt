package ru.maxim.unsplash.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Photo(
    val id: String,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date,
    val width: Int,
    val height: Int,
    val color: String?,
    @SerializedName("blur_hash") val blurHash: String,
    var likes: Int,
    @SerializedName("liked_by_user") var likedByUser: Boolean,
    val description: String?,
    val exif: Exif?,
    val location: Location?,
    val tags: ArrayList<Tag>?,
    val user: User?,
    val urls: Urls,
    val links: Links
) {
    data class Links (
        val self: String,
        val html: String,
        val download: String,
        @SerializedName("download_location") val downloadLocation: String
    )

    data class Urls (
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    )

    data class Exif (
        val make: String?,
        val model: String?,
        @SerializedName("exposure_time") val exposureTime: String?,
        val aperture: String?,
        @SerializedName("focal_length") val focalLength: String?,
        val iso: Int?
    )

}