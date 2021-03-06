package ru.maxim.unsplash.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.network.exception.*
import ru.maxim.unsplash.network.model.CollectionDto
import ru.maxim.unsplash.network.service.CollectionService
import ru.maxim.unsplash.persistence.dao.CollectionDao
import ru.maxim.unsplash.persistence.model.CollectionEntity
import ru.maxim.unsplash.util.Result
import ru.maxim.unsplash.util.networkBoundResource
import timber.log.Timber

class CollectionRepositoryImpl(
    private val collectionService: CollectionService,
    private val collectionDao: CollectionDao,
    private val collectionDtoMapper: DomainMapper<CollectionDto, Collection>,
    private val collectionEntityMapper: DomainMapper<CollectionEntity, Collection>
) : CollectionRepository {

    override suspend fun getFeedPage(page: Int): Flow<Result<List<Collection>>> =
        networkBoundResource(
            query = {
                collectionDao.getAll().map { collectionEntityMapper.toDomainModelList(it) }
            },
            fetch = {
                val loadSize = if (page == 1) initialPageSize else pageSize
                collectionService.getFeedPage(page, loadSize)
            },
            cacheFetchResult = { response: List<CollectionDto> ->
                val domainList = collectionDtoMapper.toDomainModelList(response)
                val collectionEntityList = collectionEntityMapper.fromDomainModelList(domainList)
                collectionDao.insertOrUpdate(collectionEntityList)
            },
            shouldFetch = {
                // TODO: create cache validation algorithm
                true
            }
        )

    override suspend fun getById(collectionId: String): Flow<Result<Collection?>> =
        networkBoundResource(
            query = {
                collectionDao.getById(collectionId).map { collectionEntity ->
                    collectionEntity?.let { collectionEntityMapper.toDomainModel(it) }
                }
            },
            fetch = {
                collectionService.getById(collectionId)
            },
            cacheFetchResult = { response: CollectionDto ->
                val domainModel = collectionDtoMapper.toDomainModel(response)
                collectionDao.insertOrUpdate(collectionEntityMapper.fromDomainModel(domainModel))
            },
            shouldFetch = { true }
        )

    override suspend fun getSearchPage(query: String, page: Int): Flow<Result<List<Collection>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserCollectionsPage(
        userUsername: String,
        page: Int
    ): Flow<Result<List<Collection>>> =
        networkBoundResource(
            query = {
                collectionDao.getByUser(userUsername)
                    .map { collectionEntityMapper.toDomainModelList(it) }
            },
            fetch = {
                val loadSize = if (page == 1) initialPageSize else pageSize
                collectionService.getUserCollectionsPage(userUsername, page, loadSize)
            },
            cacheFetchResult = { response: List<CollectionDto> ->
                val domainList = collectionDtoMapper.toDomainModelList(response)
                val collectionEntityList = collectionEntityMapper.fromDomainModelList(domainList)
                collectionDao.insertForUser(collectionEntityList, userUsername)
            },
            shouldFetch = {
                // TODO: create cache validation algorithm
                true
            }
        )

    override suspend fun addPhotoToCollection(
        photoId: String,
        collectionId: String
    ): Flow<Result<Unit>> = flow {
        try {
            val response = collectionService.addPhotoToCollection(
                collectionId = collectionId,
                photoId = photoId
            )
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
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
        } catch (e: Exception) {
            Timber.w(e)
            emit(Result.Error(Unit, e))
        }
    }

    private companion object {
        private const val pageSize = 10
        private const val initialPageSize = 30
    }
}