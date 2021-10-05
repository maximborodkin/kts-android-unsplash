package ru.maxim.unsplash.model

data class Location (
    val city: String?,
    val country: String?,
    val position: Position
) {
    data class Position (
        val latitude: Double,
        val longitude: Double
    )
}
