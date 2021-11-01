package ru.maxim.unsplash.persistence.mapper

import kotlinx.coroutines.flow.first
import ru.maxim.unsplash.persistence.dao.PhotoDao
import ru.maxim.unsplash.persistence.dao.UserDao
import ru.maxim.unsplash.persistence.model.CollectionEntity
import ru.maxim.unsplash.persistence.model.LinksEntity
import ru.maxim.unsplash.persistence.model.PhotoEntity
import ru.maxim.unsplash.persistence.model.UserEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.domain.model.Links
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.domain.model.User

class CollectionEntityMapper(
    private val userEntityMapper: DomainMapper<UserEntity, User>,
    private val photoEntityMapper: DomainMapper<PhotoEntity, Photo>,
    private val linksEntityMapper: DomainMapper<LinksEntity, Links>,
    private val userDao: UserDao,
    private val photoDao: PhotoDao
) : DomainMapper<CollectionEntity, Collection> {

    override suspend fun toDomainModel(model: CollectionEntity): Collection {
        val user = model.userId?.let { userId ->
            userDao.getById(userId)?.let { userEntity ->
                userEntityMapper.toDomainModel(userEntity)
            }
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
        domainModel.user?.let { user ->
            userDao.insert(userEntityMapper.fromDomainModel(user))
        }

        domainModel.coverPhoto?.let { photo ->
            val photoEntity = photoEntityMapper.fromDomainModel(photo)
            photoDao.insert(photoEntity)
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
            userId = domainModel.user?.id,
            coverPhotoId = domainModel.coverPhoto?.id,
            links = linksEntityMapper.fromDomainModel(domainModel.links),
            System.currentTimeMillis()
        )
    }
}