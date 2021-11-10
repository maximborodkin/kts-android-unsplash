package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Exif
import ru.maxim.unsplash.network.model.ExifDto

class ExifDtoMapper : DomainMapper<ExifDto, Exif> {

    override suspend fun toDomainModel(model: ExifDto) =
        Exif(
            make = model.make,
            model = model.model,
            exposureTime = model.exposureTime,
            aperture = model.aperture,
            focalLength = model.focalLength,
            iso = model.iso
        )

    override suspend fun fromDomainModel(domainModel: Exif, vararg params: String) =
        ExifDto(
            make = domainModel.make,
            model = domainModel.model,
            exposureTime = domainModel.exposureTime,
            aperture = domainModel.aperture,
            focalLength = domainModel.focalLength,
            iso = domainModel.iso
        )
}