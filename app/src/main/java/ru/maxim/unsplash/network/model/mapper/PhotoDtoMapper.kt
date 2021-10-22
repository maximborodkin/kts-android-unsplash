package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.*
import ru.maxim.unsplash.network.model.*

class PhotoDtoMapper(
    private val exifDtoMapper: DomainMapper<ExifDto, Exif>,
    private val locationDtoMapper: DomainMapper<LocationDto, Location>,
    private val tagDtoMapper: DomainMapper<TagDto, Tag>,
    private val userDtoMapper: DomainMapper<UserDto, User>,
    private val urlsDtoMapper: DomainMapper<UrlsDto, Urls>,
    private val linksDtoMapper: DomainMapper<LinksDto, Links>
) : DomainMapper<PhotoDto, Photo> {

    override fun toDomainModel(model: PhotoDto) =
        Photo(
            id = model.id,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt,
            width = model.width,
            height = model.height,
            color = model.color,
            blurHash = model.blurHash,
            likes = model.likes,
            likedByUser = model.likedByUser,
            description = model.description,
            exif = model.exif?.let { exifDtoMapper.toDomainModel(it) },
            location = model.location?.let { locationDtoMapper.toDomainModel(it) },
            tags = model.tags?.let { tagDtoMapper.toDomainModelList(it) },
            user = model.user?.let { userDtoMapper.toDomainModel(it) },
            urls = urlsDtoMapper.toDomainModel(model.urls),
            links = linksDtoMapper.toDomainModel(model.links)
        )

    override fun fromDomainModel(domainModel: Photo, vararg params: String) =
        PhotoDto(
            id = domainModel.id,
            createdAt = domainModel.createdAt,
            updatedAt = domainModel.updatedAt,
            width = domainModel.width,
            height = domainModel.height,
            color = domainModel.color,
            blurHash = domainModel.blurHash,
            likes = domainModel.likes,
            likedByUser = domainModel.likedByUser,
            description = domainModel.description,
            exif = domainModel.exif?.let { exifDtoMapper.fromDomainModel(it) },
            location = domainModel.location?.let { locationDtoMapper.fromDomainModel(it) },
            tags = domainModel.tags?.let { tagDtoMapper.fromDomainModelList(it) },
            user = domainModel.user?.let { userDtoMapper.fromDomainModel(it) },
            urls = urlsDtoMapper.fromDomainModel(domainModel.urls),
            links = linksDtoMapper.fromDomainModel(domainModel.links)
        )
}