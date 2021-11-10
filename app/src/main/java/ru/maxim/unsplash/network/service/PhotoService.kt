package ru.maxim.unsplash.network.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import ru.maxim.unsplash.network.model.PhotoDto
import ru.maxim.unsplash.network.model.response.LikeResponse
import ru.maxim.unsplash.network.model.response.PhotoDownloadResponse
import ru.maxim.unsplash.network.model.response.PhotosSearchResponse

interface PhotoService {

    @GET("/photos")
    suspend fun getFeedPage(
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

    @GET("/users/{username}/photos")
    suspend fun getUserPhotosPage(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<List<PhotoDto>>

    @GET("/users/{username}/likes")
    suspend fun getUserLikesPage(
        @Path("username") userUsername: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<List<PhotoDto>>

    @GET("/photos/{photo_id}/download")
    suspend fun getDownloadUrl(@Path("photo_id") photoId: String): PhotoDownloadResponse

    @GET
    suspend fun downloadPhoto(@Url trackableDownloadUrl: String): Response<ResponseBody>
}