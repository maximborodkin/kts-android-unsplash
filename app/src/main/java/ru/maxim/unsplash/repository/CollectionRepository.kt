package ru.maxim.unsplash.repository

import kotlinx.coroutines.flow.Flow
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.util.Result

interface CollectionRepository {

    suspend fun getFeedPage(page: Int): Flow<Result<List<Collection>>>

    suspend fun getById(collectionId: String): Flow<Result<Collection?>>

    suspend fun getSearchPage(query: String, page: Int): Flow<Result<List<Collection>>>

    suspend fun getUserCollectionsPage(userUsername: String, page: Int): Flow<Result<List<Collection>>>

    suspend fun addPhotoToCollection(photoId: String, collectionId: String): Flow<Result<Unit>>
}