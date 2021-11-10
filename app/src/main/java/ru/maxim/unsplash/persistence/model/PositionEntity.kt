package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo

data class PositionEntity(
    @ColumnInfo(name = PositionContract.Columns.latitude)
    val latitude: Double,

    @ColumnInfo(name = PositionContract.Columns.longitude)
    val longitude: Double
) {
    object PositionContract {
        object Columns {
            const val latitude = "latitude"
            const val longitude = "longitude"
        }
    }
}