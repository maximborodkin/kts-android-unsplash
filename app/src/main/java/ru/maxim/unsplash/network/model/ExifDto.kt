package ru.maxim.unsplash.network.model

import com.google.gson.annotations.SerializedName

data class ExifDto(
    @SerializedName("make")
    val make: String?,

    @SerializedName("model")
    val model: String?,

    @SerializedName("exposure_time")
    val exposureTime: String?,

    @SerializedName("aperture")
    val aperture: String?,

    @SerializedName("focal_length")
    val focalLength: String?,

    @SerializedName("iso")
    val iso: Int?
)