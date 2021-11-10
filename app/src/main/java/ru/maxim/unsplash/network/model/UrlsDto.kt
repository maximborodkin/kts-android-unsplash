package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName

data class UrlsDto(
    @SerializedName("raw")
    val raw: String?,

    @SerializedName("full")
    val full: String?,

    @SerializedName("regular")
    val regular: String?,

    @SerializedName("small")
    val small: String,

    @SerializedName("thumb")
    val thumb: String?,

    @SerializedName("medium")
    val medium: String?,

    @SerializedName("large")
    val large: String?
)