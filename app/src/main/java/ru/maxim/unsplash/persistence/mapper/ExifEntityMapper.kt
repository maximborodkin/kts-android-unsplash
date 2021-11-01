package ru.maxim.unsplash.persistence.mapper

import ru.maxim.unsplash.persistence.model.ExifEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Exif

class ExifEntityMapper : DomainMapper<ExifEntity, Exif> {

    override suspend fun toDomainModel(model: ExifEntity) =
        Exif(
            make = model.make,
            model = model.model,
            exposureTime = model.exposureTime,
            aperture = model.aperture,
            focalLength = model.focalLength,
            iso = model.iso
        )

    override suspend fun fromDomainModel(domainModel: Exif, vararg params: String) =
        ExifEntity(
            make = domainModel.make,
            model = domainModel.model,
            exposureTime = domainModel.exposureTime,
            aperture = domainModel.aperture,
            focalLength = domainModel.focalLength,
            iso = domainModel.iso
        )
}