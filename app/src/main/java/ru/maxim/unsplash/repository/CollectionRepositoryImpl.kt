package ru.maxim.unsplash.repository

import ru.maxim.unsplash.database.dao.CollectionDao
import ru.maxim.unsplash.database.model.CollectionEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.network.model.CollectionDto
import ru.maxim.unsplash.network.service.CollectionService

class CollectionRepositoryImpl(
    private val collectionService: CollectionService,
    private val photoRepository: PhotoRepository,
    private val collectionDao: CollectionDao,
    private val collectionDtoMapper: DomainMapper<CollectionDto, Collection>,
    private val collectionEntityMapper: DomainMapper<CollectionEntity, Collection>
) : CollectionRepository {

    override suspend fun getAllPaginated(page: Int, pageSize: Int): List<Collection> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: String): Collection? {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchPaginated(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Collection> {
        TODO("Not yet implemented")
    }

    override suspend fun share(shareKey: String): Boolean {
        TODO("Not yet implemented")
    }
}