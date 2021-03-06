package ru.maxim.unsplash.repository

import kotlinx.coroutines.flow.Flow
import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.util.Result

interface PhotoRepository {

    suspend fun getFeedPage(page: Int): Flow<Result<List<Photo>>>

    suspend fun getById(photoId: String): Flow<Result<Photo?>>

    suspend fun getSearchPage(query: String, page: Int): Flow<Result<List<Photo>>>

    suspend fun editLike(photoId: String, hasLike: Boolean): Flow<Result<Nothing>>

    suspend fun getCollectionPhotosPage(collectionId: String, page: Int): Flow<Result<List<Photo>>>

    suspend fun getUserPhotosPage(username: String, page: Int): Flow<Result<List<Photo>>>

    suspend fun getUserLikedPage(userUsername: String, page: Int): Flow<Result<List<Photo>>>

    suspend fun downloadPhoto(photoId: String)
}