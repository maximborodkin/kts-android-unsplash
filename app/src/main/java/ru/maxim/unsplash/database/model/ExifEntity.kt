package ru.maxim.unsplash.database.model

import androidx.room.ColumnInfo

data class ExifEntity(
    @ColumnInfo(name = ExifContract.Columns.make)
    val make: String?,

    @ColumnInfo(name = ExifContract.Columns.model)
    val model: String?,

    @ColumnInfo(name = ExifContract.Columns.exposureTime)
    val exposureTime: String?,

    @ColumnInfo(name = ExifContract.Columns.aperture)
    val aperture: String?,

    @ColumnInfo(name = ExifContract.Columns.focalLength)
    val focalLength: String?,

    @ColumnInfo(name = ExifContract.Columns.iso)
    val iso: Int?
) {
    object ExifContract {
        object Columns {
            const val make = "make"
            const val model = "model"
            const val exposureTime = "exposure_time"
            const val aperture = "aperture"
            const val focalLength = "focal_length"
            const val iso = "iso"
        }
    }
}