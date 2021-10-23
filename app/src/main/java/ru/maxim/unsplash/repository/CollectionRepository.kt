package ru.maxim.unsplash.repository

import ru.maxim.unsplash.domain.model.Collection

interface CollectionRepository {

    suspend fun getAllPaginated(page: Int, pageSize: Int): List<Collection>

    suspend fun getById(id: String): Collection?

    suspend fun getSearchPaginated(query: String, page: Int, pageSize: Int): List<Collection>

    suspend fun share(shareKey: String): Boolean
}