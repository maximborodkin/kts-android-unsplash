package ru.maxim.unsplash.domain.model

data class Exif(
    val make: String?,
    val model: String?,
    val exposureTime: String?,
    val aperture: String?,
    val focalLength: String?,
    val iso: Int?
)