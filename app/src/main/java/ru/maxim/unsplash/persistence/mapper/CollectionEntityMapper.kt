package ru.maxim.unsplash.persistence.mapper

import kotlinx.coroutines.flow.first
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.domain.model.Links
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.domain.model.User
import ru.maxim.unsplash.persistence.dao.PhotoDao
import ru.maxim.unsplash.persistence.dao.UserDao
import ru.maxim.unsplash.persistence.model.CollectionEntity
import ru.maxim.unsplash.persistence.model.LinksEntity
import ru.maxim.unsplash.persistence.model.PhotoEntity
import ru.maxim.unsplash.persistence.model.UserEntity

class CollectionEntityMapper(
    private val userEntityMapper: DomainMapper<UserEntity, User>,
    private val photoEntityMapper: DomainMapper<PhotoEntity, Photo>,
    private val linksEntityMapper: DomainMapper<LinksEntity, Links>,
    private val userDao: UserDao,
    private val photoDao: PhotoDao
) : DomainMapper<CollectionEntity, Collection> {

    override suspend fun toDomainModel(model: CollectionEntity): Collection {
        val user = model.userUsername.let { username ->
            userDao.getByUsername(username).first()?.let { userEntity ->
                userEntityMapper.toDomainModel(userEntity)
            } ?: throw IllegalStateException(
                "User with username $username for collection ${model.id} not found"
            )
        }

        val coverPhoto = model.coverPhotoId?.let { photoId ->
            photoDao.getById(photoId).first()?.let { photoEntity ->
                photoEntityMapper.toDomainModel(photoEntity)
            }
        }

        return Collection(
            id = model.id,
            title = model.title,
            description = model.description,
            publishedAt = model.publishedAt,
            updatedAt = model.updatedAt,
            totalPhotos = model.totalPhotos,
            isPrivate = model.isPrivate,
            shareKey = model.shareKey,
            coverPhoto = coverPhoto,
            user = user,
            links = linksEntityMapper.toDomainModel(model.links),
        )
    }

    override suspend fun fromDomainModel(
        domainModel: Collection,
        vararg params: String
    ): CollectionEntity {
        userDao.insert(userEntityMapper.fromDomainModel(domainModel.user))

        domainModel.coverPhoto?.let { photo ->
            photoDao.insert(photoEntityMapper.fromDomainModel(photo))
        }

        return CollectionEntity(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            publishedAt = domainModel.publishedAt,
            updatedAt = domainModel.updatedAt,
            totalPhotos = domainModel.totalPhotos,
            isPrivate = domainModel.isPrivate,
            shareKey = domainModel.shareKey,
            userUsername = domainModel.user.username,
            coverPhotoId = domainModel.coverPhoto?.id,
            links = linksEntityMapper.fromDomainModel(domainModel.links),
            System.currentTimeMillis()
        )
    }
}