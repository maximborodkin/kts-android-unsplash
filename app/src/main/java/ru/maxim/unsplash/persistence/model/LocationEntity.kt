package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class LocationEntity(
    @ColumnInfo(name = LocationContract.Columns.city)
    val city: String?,

    @ColumnInfo(name = LocationContract.Columns.country)
    val country: String?,

    @Embedded
    val position: PositionEntity
) {
    object LocationContract {
        object Columns {
            const val city = "city"
            const val country = "country"
            const val position = "position"
        }
    }
}
