package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("city")
    val city: String?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("position")
    val position: PositionDto
)
