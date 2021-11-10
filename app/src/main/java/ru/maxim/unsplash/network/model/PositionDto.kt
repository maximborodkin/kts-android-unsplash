package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName

data class PositionDto(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)