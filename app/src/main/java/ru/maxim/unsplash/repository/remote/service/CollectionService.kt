package ru.maxim.unsplash.repository.remote.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.maxim.unsplash.model.Collection
import ru.maxim.unsplash.model.response.CollectionsSearchResponse

interface CollectionService {

    @GET("/collections")
    suspend fun getAllPaginated(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<ArrayList<Collection>>

    @GET("/collections/{id}")
    suspend fun getById(@Path("id") id: String): Response<Collection>

    @GET("search/collections")
    suspend fun getSearchPaginated(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10
    ): Response<CollectionsSearchResponse>
}