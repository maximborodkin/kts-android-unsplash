package ru.maxim.unsplash.domain.model

data class User(
    val id: String,
    val username: String,
    val name: String,
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    val totalLikes: Int,
    val totalPhotos: Int,
    val totalCollections: Int,
    val profileImage: Urls,
    val links: Links
)