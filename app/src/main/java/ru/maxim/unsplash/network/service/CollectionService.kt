package ru.maxim.unsplash.network.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.maxim.unsplash.network.model.CollectionDto
import ru.maxim.unsplash.network.model.response.CollectionsSearchResponse

interface CollectionService {

    @GET("/collections")
    suspend fun getAllPaginated(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<List<CollectionDto>>

    @GET("/collections/{id}")
    suspend fun getById(@Path("id") id: String): Response<CollectionDto>

    @GET("search/collections")
    suspend fun getSearchPaginated(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<CollectionsSearchResponse>
}