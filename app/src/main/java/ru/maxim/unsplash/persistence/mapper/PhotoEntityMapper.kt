package ru.maxim.unsplash.persistence.mapper

import kotlinx.coroutines.flow.first
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.*
import ru.maxim.unsplash.persistence.dao.TagDao
import ru.maxim.unsplash.persistence.dao.UserDao
import ru.maxim.unsplash.persistence.model.*
import timber.log.Timber

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

    @Throws(IllegalStateException::class)
    override suspend fun toDomainModel(model: PhotoEntity): Photo {
        val tags = tagEntityMapper.toDomainModelList(tagDao.getByPhotoId(model.id))

        val user = userDao.getByUsername(model.userUsername).first()?.let { userEntity ->
            userEntityMapper.toDomainModel(userEntity)
        } ?: run {
            val exception = IllegalStateException(
                "User with username ${model.userUsername} for photo ${model.id} not found"
            )
            Timber.w(exception)
            throw exception
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
        userDao.insertOrUpdate(userEntityMapper.fromDomainModel(domainModel.user))

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
            userUsername = domainModel.user.username,
            location = domainModel.location?.let { locationEntityMapper.fromDomainModel(it) },
            exif = domainModel.exif?.let { exifEntityMapper.fromDomainModel(it) },
            urls = urlsEntityMapper.fromDomainModel(domainModel.urls),
            links = linksEntityMapper.fromDomainModel(domainModel.links),
            System.currentTimeMillis()
        )
    }
}