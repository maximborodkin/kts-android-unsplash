package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Position
import ru.maxim.unsplash.network.model.PositionDto

class PositionDtoMapper : DomainMapper<PositionDto, Position> {

    override suspend fun toDomainModel(model: PositionDto) =
        Position(
            latitude = model.latitude,
            longitude = model.longitude
        )

    override suspend fun fromDomainModel(domainModel: Position, vararg params: String) =
        PositionDto(
            latitude = domainModel.latitude,
            longitude = domainModel.longitude
        )
}