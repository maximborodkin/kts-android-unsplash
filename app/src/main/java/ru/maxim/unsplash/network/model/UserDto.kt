package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("portfolio_url")
    val portfolioUrl: String?,

    @SerializedName("bio")
    val bio: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("total_likes")
    val totalLikes: Int,

    @SerializedName("total_photos")
    val totalPhotos: Int,

    @SerializedName("total_collection")
    val totalCollections: Int,

    @SerializedName("profile_image")
    val profileImage: UrlsDto,

    @SerializedName("links")
    val links: LinksDto
)