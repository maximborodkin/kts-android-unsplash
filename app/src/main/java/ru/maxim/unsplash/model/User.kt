package ru.maxim.unsplash.model

import com.google.gson.annotations.SerializedName

data class User (
    val id: String,
    val username: String,
    val name: String,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    @SerializedName("total_likes")
    val totalLikes: Int,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    @SerializedName("total_collection")
    val totalCollections: Int,
    @SerializedName("profile_image")
    val profileImage: ProfileImage,
    val links: Links?
) {
    data class Links (
        val self: String,
        val html: String,
        val photos: String,
        val likes: String,
        val portfolio: String
    )

    data class ProfileImage (
        val small: String,
        val medium: String,
        val large: String
    )
}