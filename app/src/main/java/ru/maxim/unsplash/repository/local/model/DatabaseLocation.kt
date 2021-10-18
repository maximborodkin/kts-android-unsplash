package ru.maxim.unsplash.repository.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.maxim.unsplash.model.Location
import ru.maxim.unsplash.repository.local.model.DatabaseLocation.Position.Companion.fromPosition

data class DatabaseLocation (
    @ColumnInfo(name = LocationContract.Columns.city) val city: String?,
    @ColumnInfo(name = LocationContract.Columns.country) val country: String?,
    @Embedded val position: Position
) {
    companion object {
        fun Location.fromLocation() = DatabaseLocation(city, country, position.fromPosition())
    }

    fun toLocation() = Location(city, country, position.toPosition())

    data class Position (
        @ColumnInfo(name = PositionContract.Columns.latitude) val latitude: Double,
        @ColumnInfo(name = PositionContract.Columns.longitude) val longitude: Double
    ) {
        companion object {
            fun Location.Position.fromPosition() = Position(latitude, longitude)
        }

        fun toPosition() = Location.Position(latitude, longitude)
        object PositionContract {
            object Columns {
                const val latitude = "latitude"
                const val longitude = "longitude"
            }
        }
    }

    object LocationContract {
        object Columns {
            const val city = "city"
            const val country = "country"
            const val position = "position"
        }
    }
}
