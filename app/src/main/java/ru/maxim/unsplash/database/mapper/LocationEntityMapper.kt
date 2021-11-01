package ru.maxim.unsplash.database.mapper

import ru.maxim.unsplash.database.model.LocationEntity
import ru.maxim.unsplash.database.model.PositionEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Location
import ru.maxim.unsplash.domain.model.Position

class LocationEntityMapper(
    private val positionEntityMapper: DomainMapper<PositionEntity, Position>
) : DomainMapper<LocationEntity, Location> {

    override suspend fun toDomainModel(model: LocationEntity) =
        Location(
            city = model.city,
            country = model.country,
            position = positionEntityMapper.toDomainModel(model.position)
        )

    override suspend fun fromDomainModel(domainModel: Location, vararg params: String) =
        LocationEntity(
            city = domainModel.city,
            country = domainModel.country,
            position = positionEntityMapper.fromDomainModel(domainModel.position)
        )
}