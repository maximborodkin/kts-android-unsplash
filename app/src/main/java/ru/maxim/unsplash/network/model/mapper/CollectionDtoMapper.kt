package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.domain.model.Links
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.domain.model.User
import ru.maxim.unsplash.network.model.CollectionDto
import ru.maxim.unsplash.network.model.LinksDto
import ru.maxim.unsplash.network.model.PhotoDto
import ru.maxim.unsplash.network.model.UserDto

class CollectionDtoMapper(
    private val photoDtoMapper: DomainMapper<PhotoDto, Photo>,
    private val userDtoMapper: DomainMapper<UserDto, User>,
    private val linksDtoMapper: DomainMapper<LinksDto, Links>
) : DomainMapper<CollectionDto, Collection> {

    override suspend fun toDomainModel(model: CollectionDto) =
        Collection(
            id = model.id,
            title = model.title,
            description = model.description,
            publishedAt = model.publishedAt,
            updatedAt = model.updatedAt,
            totalPhotos = model.totalPhotos,
            isPrivate = model.isPrivate,
            shareKey = model.shareKey,
            coverPhoto = model.coverPhoto?.let { photoDtoMapper.toDomainModel(it) },
            user = model.user?.let { userDtoMapper.toDomainModel(it) },
            links = linksDtoMapper.toDomainModel(model.links),
        )

    override suspend fun fromDomainModel(domainModel: Collection, vararg params: String) =
        CollectionDto(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            publishedAt = domainModel.publishedAt,
            updatedAt = domainModel.updatedAt,
            totalPhotos = domainModel.totalPhotos,
            isPrivate = domainModel.isPrivate,
            shareKey = domainModel.shareKey,
            coverPhoto = domainModel.coverPhoto?.let { photoDtoMapper.fromDomainModel(it) },
            user = domainModel.user?.let { userDtoMapper.fromDomainModel(it) },
            links = linksDtoMapper.fromDomainModel(domainModel.links),
        )
}