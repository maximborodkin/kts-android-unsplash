package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName

data class LinksDto(
    @SerializedName("self")
    val self: String,

    @SerializedName("html")
    val html: String,

    @SerializedName("photos")
    val photos: String?,

    @SerializedName("related")
    val related: String?,

    @SerializedName("download")
    val download: String?,

    @SerializedName("download_location")
    val downloadLocation: String?,

    @SerializedName("likes")
    val likes: String?,

    @SerializedName("portfolio")
    val portfolio: String?
)