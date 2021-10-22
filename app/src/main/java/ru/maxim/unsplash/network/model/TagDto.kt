package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName

data class TagDto(
    @SerializedName("type")
    val type: String?,

    @SerializedName("title")
    val title: String
)
