package ru.maxim.unsplash.database.mapper

import ru.maxim.unsplash.database.dao.TagDao
import ru.maxim.unsplash.database.dao.UserDao
import ru.maxim.unsplash.database.model.*
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.*

class PhotoEntityMapper(
    private val tagDao: TagDao,
    private val userDao: UserDao,
    private val locationEntityMapper: DomainMapper<LocationEntity, Location>,
    private val exifEntityMapper: DomainMapper<ExifEntity, Exif>,
    private val tagEntityMapper: DomainMapper<TagEntity, Tag>,
    private val userEntityMapper: DomainMapper<UserEntity, User>,
    private val urlsEntityMapper: DomainMapper<UrlsEntity, Urls>,
    private val linksEntityMapper: DomainMapper<LinksEntity, Links>,
) : DomainMapper<PhotoEntity, Photo> {

    override suspend fun toDomainModel(model: PhotoEntity): Photo {
        val tags = ArrayList(tagEntityMapper.toDomainModelList(tagDao.getByPhotoId(model.id)))
        val user = model.userId?.let { userId ->
            userDao.getById(userId)?.let { userEntity ->
                userEntityMapper.toDomainModel(userEntity)
            }
        }

        return Photo(
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
            exif = model.exif?.let { exifEntity -> exifEntityMapper.toDomainModel(exifEntity) },
            location = model.location?.let { locationEntity ->
                locationEntityMapper.toDomainModel(locationEntity)
            },
            tags = tags,
            user = user,
            urls = urlsEntityMapper.toDomainModel(model.urls),
            links = linksEntityMapper.toDomainModel(model.links)
        )
    }

    override suspend fun fromDomainModel(domainModel: Photo, vararg params: String): PhotoEntity {
        domainModel.user?.let { user ->
            userDao.insert(userEntityMapper.fromDomainModel(user))
        }

        return PhotoEntity(
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
            userId = domainModel.user?.id,
            location = domainModel.location?.let { locationEntityMapper.fromDomainModel(it) },
            exif = domainModel.exif?.let { exifEntityMapper.fromDomainModel(it) },
            urls = urlsEntityMapper.fromDomainModel(domainModel.urls),
            links = linksEntityMapper.fromDomainModel(domainModel.links),
            System.currentTimeMillis()
        )
    }
}