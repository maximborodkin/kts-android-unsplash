package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Location
import ru.maxim.unsplash.domain.model.Position
import ru.maxim.unsplash.network.model.LocationDto
import ru.maxim.unsplash.network.model.PositionDto

class LocationDtoMapper(
    private val positionDtoMapper: DomainMapper<PositionDto, Position>
) : DomainMapper<LocationDto, Location> {

    override fun toDomainModel(model: LocationDto) =
        Location(
            city = model.city,
            country = model.country,
            position = positionDtoMapper.toDomainModel(model.position)
        )

    override fun fromDomainModel(domainModel: Location, vararg params: String) =
        LocationDto(
            city = domainModel.city,
            country = domainModel.country,
            position = positionDtoMapper.fromDomainModel(domainModel.position)
        )
}