package ru.maxim.unsplash.network.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.maxim.unsplash.network.model.CollectionDto
import ru.maxim.unsplash.network.model.response.AddPhotoToCollectionResponse
import ru.maxim.unsplash.network.model.response.CollectionsSearchResponse

interface CollectionService {

    @GET("/collections")
    suspend fun getFeedPage(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<List<CollectionDto>>

    @GET("/collections/{id}")
    suspend fun getById(@Path("id") id: String): Response<CollectionDto>

    @GET("search/collections")
    suspend fun getSearchPage(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<CollectionsSearchResponse>

    @GET("/users/{username}/collections")
    suspend fun getUserCollectionsPage(
        @Path("username") userUsername: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<List<CollectionDto>>

    @POST("/collections/{collection_id}/add")
    suspend fun addPhotoToCollection(
         @Path("collection_id") collectionId: String,
         @Query("photo_id") photoId: String
    ): Response<AddPhotoToCollectionResponse>
}