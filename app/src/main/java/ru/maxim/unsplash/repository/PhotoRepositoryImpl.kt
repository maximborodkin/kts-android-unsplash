package ru.maxim.unsplash.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.domain.model.Tag
import ru.maxim.unsplash.network.model.PhotoDto
import ru.maxim.unsplash.network.service.PhotoService
import ru.maxim.unsplash.persistence.dao.PhotoDao
import ru.maxim.unsplash.persistence.dao.TagDao
import ru.maxim.unsplash.persistence.model.CollectionPhotoCrossRef
import ru.maxim.unsplash.persistence.model.PhotoEntity
import ru.maxim.unsplash.persistence.model.TagEntity
import ru.maxim.unsplash.util.Result
import ru.maxim.unsplash.util.networkBoundResource

class PhotoRepositoryImpl(
    private val photoService: PhotoService,
    private val photoDao: PhotoDao,
    private val tagDao: TagDao,
    private val photoDtoMapper: DomainMapper<PhotoDto, Photo>,
    private val photoEntityMapper: DomainMapper<PhotoEntity, Photo>,
    private val tagEntityMapper: DomainMapper<TagEntity, Tag>
) : PhotoRepository {

    override suspend fun getFeedPage(page: Int): Flow<Result<List<Photo>>> =
        networkBoundResource(
            query = {
                if (page == 1) {
                    photoDao.getAll().map { photoEntityMapper.toDomainModelList(it) }
                } else flowOf(listOf())
            },
            fetch = {
                val loadSize = if (page == 1) initialPageSize else pageSize
                photoService.getPage(page, loadSize)
            },
            cacheFetchResult = { response: List<PhotoDto> ->
                if (page == 1) {
                    photoDao.deleteAll()
                }
                val domainModelList = photoDtoMapper.toDomainModelList(response)
                photoDao.insertAll(photoEntityMapper.fromDomainModelList(domainModelList))
            },
            shouldFetch = {
                // TODO: create cache validation algorithm
                true
            }
        )

    override suspend fun getById(photoId: String): Flow<Result<Photo?>> =
        networkBoundResource(
            query = {
                photoDao.getById(photoId).map { photoEntity ->
                    photoEntity?.let { photoEntityMapper.toDomainModel(it) }
                }
            },
            fetch = {
                photoService.getById(photoId)
            },
            cacheFetchResult = { response: PhotoDto ->
                val domainModel = photoDtoMapper.toDomainModel(response)
                photoDao.insert(photoEntityMapper.fromDomainModel(domainModel))
                domainModel.tags?.let {
                    tagDao.insertAll(tagEntityMapper.fromDomainModelList(it, domainModel.id))
                }
            },
            shouldFetch = { true }
        )

    override suspend fun getSearchPage(query: String, page: Int): Flow<Result<List<Photo>>> =
        networkBoundResource(
            query = {
                photoDao.search(query).map { photoEntityMapper.toDomainModelList(it) }
            },
            fetch = {
                delay(5000)
                val loadSize = if (page == 1) initialPageSize else pageSize
                photoService.getSearchPage(query, page, loadSize)
            },
            cacheFetchResult = { response ->
                val domainModelList = photoDtoMapper.toDomainModelList(response.results)
                photoDao.insertAll(photoEntityMapper.fromDomainModelList(domainModelList))
            },
            shouldFetch = { true }
        )

    override suspend fun setLike(photoId: String): Flow<Result<Photo>> {
        TODO("Not yet implemented")
    }

    override suspend fun removeLike(photoId: String): Flow<Result<Photo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCollectionPhotosPage(
        collectionId: String,
        page: Int
    ): Flow<Result<List<Photo>>> =
        networkBoundResource(
            query = {
                photoDao.getByCollectionId(collectionId)
                    .map { photoEntityMapper.toDomainModelList(it) }
            },
            fetch = {
                val loadSize = if (page == 1) initialPageSize else pageSize
                photoService.getCollectionPhotosPage(collectionId, page, loadSize)
            },
            cacheFetchResult = { response ->
                val domainModelList = photoDtoMapper.toDomainModelList(response)

                domainModelList.forEach { photo ->
                    photoDao.insert(photoEntityMapper.fromDomainModel(photo))

                    photoDao.insertCollectionRelation(
                        CollectionPhotoCrossRef(collectionId, photo.id)
                    )
                }
            },
            shouldFetch = { true }
        )

    private companion object {
        private const val pageSize = 10
        private const val initialPageSize = 30
    }
}