package ru.maxim.unsplash.repository

import ru.maxim.unsplash.domain.model.Photo
import ru.maxim.unsplash.ui.main.PhotosOrderType
import ru.maxim.unsplash.ui.main.PhotosSearchOrderType

interface PhotoRepository {

    suspend fun getAllPaginated(page: Int, pageSize: Int, order: PhotosOrderType): List<Photo>

    suspend fun getById(photoId: String): Photo?

    suspend fun getSearchPaginated(
        query: String,
        page: Int,
        pageSize: Int,
        order: PhotosSearchOrderType
    ): List<Photo>

    suspend fun setLike(photoId: String): Photo

    suspend fun removeLike(photoId: String): Photo

    suspend fun getCollectionPhotosPaginated(
        collectionId: String,
        page: Int,
        pageSize: Int
    ): List<Photo>
}