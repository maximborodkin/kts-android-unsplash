package ru.maxim.unsplash.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.unsplash.network.model.PhotoDto
import ru.maxim.unsplash.network.model.response.LikeResponse
import ru.maxim.unsplash.network.model.response.PhotosSearchResponse

interface PhotoService {

    @GET("/photos")
    suspend fun getPage(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<List<PhotoDto>>

    @GET("/photos/{id}")
    suspend fun getById(@Path("id") id: String): Response<PhotoDto>

    @GET("/search/photos")
    suspend fun getSearchPage(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<PhotosSearchResponse>

    @POST("/photos/{id}/like")
    suspend fun setLike(@Path("id") photoId: String): Response<LikeResponse>

    @DELETE("/photos/{id}/like")
    suspend fun removeLike(@Path("id") photoId: String): Response<LikeResponse>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotosPage(
        @Path("id") collectionId: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<List<PhotoDto>>
}