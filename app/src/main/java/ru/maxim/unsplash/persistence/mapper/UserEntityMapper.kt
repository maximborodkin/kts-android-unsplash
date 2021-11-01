package ru.maxim.unsplash.persistence.mapper

import ru.maxim.unsplash.persistence.model.LinksEntity
import ru.maxim.unsplash.persistence.model.UrlsEntity
import ru.maxim.unsplash.persistence.model.UserEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Links
import ru.maxim.unsplash.domain.model.Urls
import ru.maxim.unsplash.domain.model.User

class UserEntityMapper(
    private val urlsEntityMapper: DomainMapper<UrlsEntity, Urls>,
    private val linksEntityMapper: DomainMapper<LinksEntity, Links>,
) : DomainMapper<UserEntity, User> {

    override suspend fun toDomainModel(model: UserEntity) =
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
            profileImage = urlsEntityMapper.toDomainModel(model.profileImage),
            links = linksEntityMapper.toDomainModel(model.links)
        )

    override suspend fun fromDomainModel(domainModel: User, vararg params: String) =
        UserEntity(
            id = domainModel.id,
            username = domainModel.username,
            name = domainModel.name,
            portfolioUrl = domainModel.portfolioUrl,
            bio = domainModel.bio,
            location = domainModel.location,
            totalLikes = domainModel.totalLikes,
            totalPhotos = domainModel.totalPhotos,
            totalCollections = domainModel.totalCollections,
            profileImage = urlsEntityMapper.fromDomainModel(domainModel.profileImage),
            links = linksEntityMapper.fromDomainModel(domainModel.links),
            System.currentTimeMillis()
        )
}