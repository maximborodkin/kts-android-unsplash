package ru.maxim.unsplash.database.mapper

import ru.maxim.unsplash.database.model.PositionEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Position

class PositionEntityMapper : DomainMapper<PositionEntity, Position> {

    override suspend fun toDomainModel(model: PositionEntity) =
        Position(
            latitude = model.latitude,
            longitude = model.longitude
        )

    override suspend fun fromDomainModel(domainModel: Position, vararg params: String) =
        PositionEntity(
            latitude = domainModel.latitude,
            longitude = domainModel.longitude
        )
}