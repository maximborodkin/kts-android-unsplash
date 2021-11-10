package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Links
import ru.maxim.unsplash.domain.model.Urls
import ru.maxim.unsplash.domain.model.User
import ru.maxim.unsplash.network.model.LinksDto
import ru.maxim.unsplash.network.model.UrlsDto
import ru.maxim.unsplash.network.model.UserDto

class UserDtoMapper(
    private val urlsDtoMapper: DomainMapper<UrlsDto, Urls>,
    private val linksDtoMapper: DomainMapper<LinksDto, Links>
) : DomainMapper<UserDto, User> {

    override suspend fun toDomainModel(model: UserDto) =
        User(
            id = model.id,
            username = model.username,
            name = model.name,
            portfolioUrl = model.portfolioUrl,
            bio = model.bio,
            location = model.location,
            totalLikes = model.totalLikes,
            totalPhotos = model.totalPhotos,
            totalCollections = model.totalCollections,
            profileImage = urlsDtoMapper.toDomainModel(model.profileImage),
            links = linksDtoMapper.toDomainModel(model.links)
        )

    override suspend fun fromDomainModel(domainModel: User, vararg params: String) =
        UserDto(
            id = domainModel.id,
            username = domainModel.username,
            name = domainModel.name,
            portfolioUrl = domainModel.portfolioUrl,
            bio = domainModel.bio,
            location = domainModel.location,
            totalLikes = domainModel.totalLikes,
            totalPhotos = domainModel.totalPhotos,
            totalCollections = domainModel.totalCollections,
            profileImage = urlsDtoMapper.fromDomainModel(domainModel.profileImage),
            links = linksDtoMapper.fromDomainModel(domainModel.links)
        )
}