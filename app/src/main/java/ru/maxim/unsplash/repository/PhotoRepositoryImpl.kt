package ru.maxim.unsplash.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.maxim.unsplash.persistence.dao.PhotoDao
import ru.maxim.unsplash.persistence.dao.TagDao
import ru.maxim.unsplash.persistence.model.CollectionPhotoCrossRef
import ru.maxim.unsplash.persistence.model.PhotoEntity
import ru.maxim.unsplash.persistence.model.TagEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.domain.model.Tag
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.network.model.PhotoDto
import ru.maxim.unsplash.network.service.PhotoService
import ru.maxim.unsplash.ui.main.PhotosOrderType
import ru.maxim.unsplash.ui.main.PhotosSearchOrderType
import java.io.IOException

class PhotoRepositoryImpl(
    private val photoService: PhotoService,
    private val photoDao: PhotoDao,
    private val tagDao: TagDao,
    private val photoDtoMapper: DomainMapper<PhotoDto, Photo>,
    private val tagDtoMapper: DomainMapper<TagDao, Tag>,
    private val photoEntityMapper: DomainMapper<PhotoEntity, Photo>,
    private val tagEntityMapper: DomainMapper<TagEntity, Tag>
) : PhotoRepository {

    override suspend fun getAllPaginated(
        page: Int,
        pageSize: Int,
        order: PhotosOrderType,
    ): List<Photo> {
        withContext(IO) {
            try {
                val response = photoService.getAllPaginated(page, pageSize, order.queryParam)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val photos = photoDtoMapper.toDomainModelList(responseBody)
                    updateCache(photos)
                    return@withContext photos
                } else {
                    throw when(response.code()) {
                        401 -> UnauthorizedException(response)
                        403 -> ForbiddenException(response)
                        404 -> NotFoundException(response)
                        408 -> TimeoutException(response)
                        in 500..599 -> ServerErrorException(response)
                        else -> NetworkException(response)
                    }
                }
            } catch (e: IOException) {

            }
        }
        return listOf()
    }

    override suspend fun getById(photoId: String): Photo? = null

    override suspend fun getSearchPaginated(
        query: String,
        page: Int,
        pageSize: Int,
        order: PhotosSearchOrderType
    ): List<Photo> {
        //TODO("Not yet implemented")
        return listOf()
    }

    override suspend fun setLike(photoId: String): Photo {
        TODO("Not yet implemented")
    }

    override suspend fun removeLike(photoId: String): Photo {
        TODO("Not yet implemented")
    }

    override suspend fun getCollectionPhotosPaginated(
        collectionId: String,
        page: Int,
        pageSize: Int
    ): List<Photo> {
        TODO("Not yet implemented")
    }

    private suspend fun updateCache(photos: List<Photo>) {

    }

    private suspend fun updateCache(photo: Photo) = updateCache(listOf(photo))
}